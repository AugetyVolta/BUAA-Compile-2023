package optimizer;

import llvm.*;
import llvm.instr.*;
import llvm.type.IrIntegetType;

import java.util.*;

public class Mem2Reg {
    private boolean debug = false;
    private final IrModule module;
    private final HashMap<IrBasicBlock, ArrayList<IrBasicBlock>> dom = new HashMap<>();//A dom集合B
    private final HashMap<IrBasicBlock, ArrayList<IrBasicBlock>> domed = new HashMap<>();//A被集合B dom
    private final HashMap<IrBasicBlock, IrBasicBlock> iDom = new HashMap<>();//A被B dom,否则B无法去dom多个块
    private final HashMap<IrBasicBlock, ArrayList<IrBasicBlock>> iDoms = new HashMap<>();//A直接支配集合B
    private final HashMap<IrBasicBlock, ArrayList<IrBasicBlock>> DF = new HashMap<>();//A的支配边界
    private final HashMap<IrInstr, ArrayList<IrInstr>> defs = new HashMap<>();//重新定义了alloca指令的指令
    private final HashMap<IrInstr, ArrayList<IrInstr>> uses = new HashMap<>();//使用了alloca指令的指令
    private final HashMap<IrInstr, Stack<IrValue>> InComingVals = new HashMap<>();

    public Mem2Reg(IrModule module) {
        this.module = module;
        buildCFG();
        buildDom();
        buildIDom();
        buildDF();
        mem2Reg();
        removeLSA();
    }

    public void buildCFG() {
        //删除不可到达的块
        deleteUnreachableBlock();
        //构建CFG
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            for (IrBasicBlock basicBlock : basicBlocks) {
                IrInstr lastInstr = basicBlock.getLastInstr();
                if (lastInstr instanceof IrBrInstr) {
                    IrBrInstr brInstr = (IrBrInstr) lastInstr;
                    if (brInstr.isCond()) {//如果是条件跳转
                        IrBasicBlock trueBlock = (IrBasicBlock) brInstr.getLabel(1);
                        IrBasicBlock falseBlock = (IrBasicBlock) brInstr.getLabel(2);
                        //创建CFG
                        basicBlock.addNextBlock(trueBlock);
                        basicBlock.addNextBlock(falseBlock);
                        trueBlock.addPrevBlock(basicBlock);
                        falseBlock.addPrevBlock(basicBlock);
                    } else {//不是条件跳转
                        IrBasicBlock nextBlock = (IrBasicBlock) brInstr.getLabel(0);
                        //创建CFG
                        basicBlock.addNextBlock(nextBlock);
                        nextBlock.addPrevBlock(basicBlock);
                    }
                }
            }
        }
    }

    //bfs从根节点走一遍,没有遍历到的节点被删除
    public void deleteUnreachableBlock() {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            //后继块
            HashMap<IrBasicBlock, ArrayList<IrBasicBlock>> child = new HashMap<>();
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            //构建block指向跳转块的图
            for (IrBasicBlock basicBlock : basicBlocks) {
                child.put(basicBlock, new ArrayList<>());
                IrInstr lastInstr = basicBlock.getLastInstr();
                if (lastInstr instanceof IrBrInstr) {
                    IrBrInstr brInstr = (IrBrInstr) lastInstr;
                    if (brInstr.isCond()) {//如果是条件跳转
                        IrBasicBlock trueBlock = (IrBasicBlock) brInstr.getLabel(1);
                        IrBasicBlock falseBlock = (IrBasicBlock) brInstr.getLabel(2);
                        //加入到跳转块中
                        child.get(basicBlock).add(trueBlock);
                        child.get(basicBlock).add(falseBlock);
                    } else {//不是条件跳转
                        IrBasicBlock nextBlock = (IrBasicBlock) brInstr.getLabel(0);
                        child.get(basicBlock).add(nextBlock);
                    }
                }
            }
            //使用BFS遍历图
            HashMap<IrBasicBlock, Boolean> hasVisited = new HashMap<>();//是否遍历过
            Queue<IrBasicBlock> queue = new LinkedList<IrBasicBlock>();
            IrBasicBlock entryBlock = function.getBasicBlocks().get(0);
            //加入队中,是否访问设为true
            queue.offer(entryBlock);
            hasVisited.put(entryBlock, true);
            while (!queue.isEmpty()) {
                IrBasicBlock curBlock = queue.poll();
                ArrayList<IrBasicBlock> childBlocks = child.get(curBlock);
                for (IrBasicBlock childBlock : childBlocks) {
                    if (!hasVisited.containsKey(childBlock)) {
                        queue.offer(childBlock);
                        hasVisited.put(childBlock, true);
                    }
                }
            }
            //根据是否被访问删除块,并且删除load store指令的use,让usee不能再找到这个user
            Iterator<IrBasicBlock> blocks = basicBlocks.iterator();
            while (blocks.hasNext()) {
                IrBasicBlock block = blocks.next();
                if (!hasVisited.containsKey(block)) {
                    //删除被删除块中load store指令的use
                    ArrayList<IrInstr> instrs = block.getInstrs();
                    for (IrInstr instr : instrs) {
                        removeDeadUse(instr);
                    }
                    blocks.remove();
                }
            }
        }
    }

    public static void removeDeadUse(IrInstr instr) {
        if (instr instanceof IrStoreInstr || instr instanceof IrLoadInstr) { //删除被删除的load store指令的use
            ArrayList<IrUse> uses = instr.getOperands(); //找到load store指令使用的use
            Iterator<IrUse> it = uses.iterator();
            while (it.hasNext()) {
                IrUse use = it.next();
                IrValue usee = use.getIrUsee();
                usee.removeUse(use);
                it.remove();
            }
        }
    }

    //节点删除法构建支配树
    public void buildDom() {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            IrBasicBlock entryBlock = function.getBasicBlocks().get(0);
            //初始化Dom
            for (IrBasicBlock basicBlock : basicBlocks) {
                dom.put(basicBlock, new ArrayList<>());
                domed.put(basicBlock, new ArrayList<>());
            }
            for (IrBasicBlock basicBlock : basicBlocks) {
                //使用BFS遍历图
                HashMap<IrBasicBlock, Boolean> hasVisited = new HashMap<>();//是否遍历过
                Queue<IrBasicBlock> queue = new LinkedList<IrBasicBlock>();
                if (!basicBlock.equals(entryBlock)) {
                    //加入队中,是否访问设为true
                    queue.offer(entryBlock);
                    hasVisited.put(entryBlock, true);
                }
                while (!queue.isEmpty()) {
                    IrBasicBlock curBlock = queue.poll();
                    ArrayList<IrBasicBlock> nextBlocks = curBlock.getNext();//获得后继
                    for (IrBasicBlock nextBlock : nextBlocks) {
                        if (!hasVisited.containsKey(nextBlock) && !nextBlock.equals(basicBlock)) { //相当于删除当前basicBlock
                            queue.offer(nextBlock);
                            hasVisited.put(nextBlock, true);
                        }
                    }
                }
                //没有被遍历到的block被当前节点支配
                for (int i = 0; i < basicBlocks.size(); i++) {
                    IrBasicBlock block = basicBlocks.get(i);
                    if (!hasVisited.containsKey(block)) {
                        dom.get(basicBlock).add(block); //basicBlock支配block
                        domed.get(block).add(basicBlock); //block被basicBlock支配
                    }
                }
            }
            if (debug) {
                for (IrBasicBlock basicBlock : basicBlocks) {
                    System.out.println("支配者:" + basicBlock.getName());
                    ArrayList<IrBasicBlock> blocks = dom.get(basicBlock);
                    StringBuilder sb = new StringBuilder();
                    for (IrBasicBlock block : blocks) {
                        sb.append(block.getName()).append(" ");
                    }
                    System.out.println(sb.toString());
                }
                for (IrBasicBlock basicBlock : basicBlocks) {
                    System.out.println("被支配者:" + basicBlock.getName());
                    ArrayList<IrBasicBlock> blocks = domed.get(basicBlock);
                    StringBuilder sb = new StringBuilder();
                    for (IrBasicBlock block : blocks) {
                        sb.append(block.getName()).append(" ");
                    }
                    System.out.println(sb.toString());
                }
            }
        }
    }

    public boolean isIDom(IrBasicBlock domed, IrBasicBlock domer) {
        if (domer.equals(domed)) {
            return false;
        } else {
            //如果domer还支配其他支配domed的结点,说明不是直接支配
            ArrayList<IrBasicBlock> doms = dom.get(domer); //domer支配的基本块
            ArrayList<IrBasicBlock> domers = this.domed.get(domed);//支配domed的基本块
            for (IrBasicBlock block : domers) {
                if (!block.equals(domer) && doms.contains(block) && !block.equals(domed)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void buildIDom() {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            for (IrBasicBlock basicBlock : basicBlocks) {
                if (domed.get(basicBlock).size() == 1) { //只有自己支配自己,不存在A直接支配B
                    continue;
                } else {
                    ArrayList<IrBasicBlock> domers = domed.get(basicBlock);
                    for (IrBasicBlock block : domers) {
                        if (isIDom(basicBlock, block)) {
                            iDom.put(basicBlock, block);
                            //将basicblock加入block直接支配的节点集合中
                            ArrayList<IrBasicBlock> iDomBlocks = iDoms.getOrDefault(block, new ArrayList<>());
                            iDomBlocks.add(basicBlock);
                            iDoms.put(block, iDomBlocks);
                            block.setIdoms(iDomBlocks);
                            break;
                        }
                    }
                }
            }
            if (debug) {
                for (IrBasicBlock key : iDom.keySet()) {
                    System.out.println(key.getName() + "<-" + iDom.get(key).getName());
                }
            }
        }
    }

    public void buildDF() {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            for (IrBasicBlock basicBlock : basicBlocks) {
                DF.put(basicBlock, new ArrayList<>());
            }
            for (IrBasicBlock n : basicBlocks) {
                if (n.getPrev().size() >= 2) {
                    ArrayList<IrBasicBlock> predecessors = n.getPrev();//前驱
                    for (IrBasicBlock p : predecessors) {
                        IrBasicBlock runner = p;
                        while (!runner.equals(iDom.get(n))) {
                            if (!DF.get(runner).contains(n)) {
                                DF.get(runner).add(n);
                            }
                            runner = iDom.get(runner);
                        }
                    }
                }
            }
            if (debug) {
                for (IrBasicBlock n : basicBlocks) {
                    System.out.println("DF:" + n.getName());
                    StringBuilder sb = new StringBuilder();
                    for (IrBasicBlock block : DF.get(n)) {
                        sb.append(block.getName()).append(" ");
                    }
                    System.out.println(sb.toString());
                }
            }
        }
    }

    public void mem2Reg() {
        insertPhi();
        rename();
    }

    public void insertPhi() {
        for (IrFunction function : module.getIrFunctions()) {
            for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
                for (IrInstr instr : basicBlock.getInstrs()) {
                    if (instr instanceof IrAllocaInstr &&
                            ((IrAllocaInstr) instr).getRefType() == IrIntegetType.INT32) {
                        instr.needDelete = true;
                        //buildDefUse
                        uses.put(instr, new ArrayList<>());
                        defs.put(instr, new ArrayList<>());
                        InComingVals.put(instr, new Stack<>());
                        InComingVals.get(instr).push(new IrConstInt(0));
                        ArrayList<IrBasicBlock> defBlock = new ArrayList<>();
                        ArrayList<IrUse> uses = instr.getIrUses();
                        for (IrUse use : uses) {
                            IrUser user = use.getIrUser();
                            if (user instanceof IrLoadInstr) {
                                this.uses.get(instr).add((IrInstr) user);
                            } else if (user instanceof IrStoreInstr) {
                                defs.get(instr).add((IrInstr) user);
                                if (!defBlock.contains(((IrStoreInstr) user).getBasicBlock())) {
                                    defBlock.add(((IrStoreInstr) user).getBasicBlock());
                                }
                            }
                        }
                        //insertPhi
                        ArrayList<IrBasicBlock> F = new ArrayList<>();
                        Queue<IrBasicBlock> W = new LinkedList<IrBasicBlock>();
                        for (IrBasicBlock block : defBlock) {
                            W.offer(block);
                        }
                        while (!W.isEmpty()) {
                            IrBasicBlock X = W.poll();
                            for (IrBasicBlock Y : DF.getOrDefault(X, new ArrayList<>())) {
                                if (!F.contains(Y)) {
                                    ArrayList<IrInstr> instrInY = Y.getInstrs();
                                    IrPhiInstr phiInstr = IrBuilder.IRBUILDER.buildPhiInstr(Y, function, Y.getPrev()); //插入到Y的开头
                                    defs.get(instr).add(phiInstr);
                                    this.uses.get(instr).add(phiInstr);
                                    instrInY.add(0, phiInstr);
                                    F.add(Y);
                                    if (!defBlock.contains(Y)) {
                                        W.offer(Y);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void rename() {
        for (IrFunction function : module.getIrFunctions()) {
            IrBasicBlock entryBlock = function.getBasicBlocks().get(0);
            for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
                for (IrInstr instr : basicBlock.getInstrs()) {
                    if (instr instanceof IrAllocaInstr &&
                            ((IrAllocaInstr) instr).getRefType() == IrIntegetType.INT32) {
                        renameMain(instr, entryBlock);
                    }
                }
            }
        }
    }

    public void renameMain(IrInstr allocaInstr, IrBasicBlock entryBlock) {
        int defTime = 0; //当前块里的定义次数
        ArrayList<IrInstr> instrs = entryBlock.getInstrs();
        for (IrInstr instr : instrs) {
            //如果已经被删除了,就不能再处理了,否则会出错
            if (instr.needDelete) {
                continue;
            }
            if (uses.get(allocaInstr).contains(instr) && !(instr instanceof IrPhiInstr)) {
                //对于load指令来说,需要替换等号左边的部分,将之后的use替换
                replaceAllUse(instr, InComingVals.get(allocaInstr).peek());
                instr.needDelete = true;
            }
            //at def: push onto stack
            else if (defs.get(allocaInstr).contains(instr)) { //store instr
                if (instr instanceof IrStoreInstr) {
                    InComingVals.get(allocaInstr).push(((IrStoreInstr) instr).getFrom());
                    instr.needDelete = true;
                } else {
                    InComingVals.get(allocaInstr).push(instr); //phi
                }
                defTime++;
            }
        }
        //给后继块的phi指令插入选择
        for (IrBasicBlock nexts : entryBlock.getNext()) {
            instrs = nexts.getInstrs();
            for (IrInstr instr : instrs) {
                if (instr.needDelete) {
                    continue;
                }
                if (uses.get(allocaInstr).contains(instr) && instr instanceof IrPhiInstr) {
                    ((IrPhiInstr) instr).setOperand(entryBlock, InComingVals.get(allocaInstr).peek());
                }
            }
        }
        //call rename(v) on all children in D-tree,直接后继
        for (IrBasicBlock block : iDoms.getOrDefault(entryBlock, new ArrayList<>())) {
            renameMain(allocaInstr, block);
        }
        //for each def in this block pop from stack
        for (int i = 0; i < defTime; i++) {
            InComingVals.get(allocaInstr).pop();
        }
    }


    //替换所有的use
    public void replaceAllUse(IrValue src, IrValue dst) {
        ArrayList<IrUse> uses = src.getIrUses();
        for (IrUse use : uses) {
            use.setIrUsee(dst);
            //需要把dst的use也连接上
            dst.addUseToUser(use);
        }
        uses.clear();
    }

    //remove Load Store Alloca
    public void removeLSA() {
        for (IrFunction function : module.getIrFunctions()) {
            for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
                ArrayList<IrInstr> instrs = basicBlock.getInstrs();
                instrs.removeIf(instr -> instr.needDelete);
            }
        }
    }
}
