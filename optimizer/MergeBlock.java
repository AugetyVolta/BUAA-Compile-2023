package optimizer;

import llvm.*;
import llvm.instr.IrBrInstr;
import llvm.instr.IrInstr;
import llvm.instr.IrRetInstr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

public class MergeBlock {
    private IrModule module;

    public MergeBlock(IrModule module) {
        this.module = module;
        //合并相邻跳转块,这个只能放到后端做
        //MergeNeighborBlock();
        //合并只有一个出口的基本块
        MergerOneOutBlock();
        //合并只有一个空跳转指令的块
        MergeEmptyBlock();
        //处理掉br指令条件跳转但是跳转到同一个块的问题
        removeFakeCondBr();
    }

    public void MergerOneOutBlock() {
        for (IrFunction function : module.getIrFunctions()) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            ListIterator<IrBasicBlock> it = basicBlocks.listIterator();
            while (it.hasPrevious()) {
                IrBasicBlock block = it.previous();
                if (block.getNext().size() == 0) {
                    while (block.getPrev().size() == 1 //block只有一个前驱
                            && block.getPrev().get(0).getNext().size() == 1) { //前驱也只有block一个后继
                        it.remove();
                        IrBasicBlock newBlock = block.getPrev().get(0);
                        mergeBlockFromEnd(block.getPrev().get(0), block);
                        block = newBlock;
                    }
                }
            }
        }
    }

    public void mergeBlockFromEnd(IrBasicBlock prevBlock, IrBasicBlock block) {
        ArrayList<IrInstr> instrs = block.getInstrs();
        //将block的指令插在prevBlock中
        for (IrInstr instr : instrs) {
            prevBlock.insertBeforeLast(instr);
        }
        //将block从prevBlock的后继中删除
        prevBlock.getNext().remove(block);
        //删除prevBlock的br指令
        int size = prevBlock.getInstrs().size();
        prevBlock.getInstrs().remove(size - 1);
    }

    public void mergeBlock(IrBasicBlock block, IrBasicBlock nextBlock) {
        ArrayList<IrInstr> instrs = block.getInstrs();
        Collections.reverse(instrs);
        //将block的指令插在下一个块中,除了br指令
        for (IrInstr instr : instrs) {
            if (!(instr instanceof IrBrInstr) && !(instr instanceof IrRetInstr)) {
                nextBlock.insertInHead(instr);
            }
        }
        Collections.reverse(instrs);
        //将block从nextBlock的前驱中删除
        nextBlock.getPrev().remove(block);
        //将block的前驱加在nextBlock的前驱中
        for (IrBasicBlock prev : block.getPrev()) {
            if (!nextBlock.getPrev().contains(prev)) {
                //将block的前驱加在nextBlock的前驱中
                nextBlock.addPrevBlock(prev);
                //prev的后继加入nextBlock
                prev.addNextBlock(nextBlock);
            }
            //前驱块删除后继中的block
            prev.getNext().remove(block);
            //设置前驱的br
            IrBrInstr br = (IrBrInstr) prev.getLastInstr();
            if (br.isCond()) {
                if (br.getLabel(1).equals(block)) {
                    br.modifyOperand(nextBlock, 1);
                } else {
                    br.modifyOperand(nextBlock, 2);
                }
            } else {
                br.modifyOperand(nextBlock, 0);
            }

        }
    }

    public void MergeEmptyBlock() {
        for (IrFunction function : module.getIrFunctions()) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            Iterator<IrBasicBlock> it = basicBlocks.iterator();
            while (it.hasNext()) {
                IrBasicBlock block = it.next();
                if (block.getNext().size() == 1 && block.getInstrs().size() == 1) {
                    it.remove();
                    mergeBlock(block, block.getNext().get(0));
                }
            }
        }
    }

    public void removeFakeCondBr() {
        for (IrFunction function : module.getIrFunctions()) {
            while (needRemoveFakeCondBr(function)) {
                for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
                    IrInstr lastInstr = basicBlock.getLastInstr();
                    //如果br的两个选择都跳入同一个位置
                    if (lastInstr instanceof IrBrInstr && ((IrBrInstr) lastInstr).isCond() && lastInstr.getOperand(1).equals(lastInstr.getOperand(2))) {
                        //随便删除一个后继//这里不用删了,因为在上面已经特判了不会出现有相同的后继，插入时
                        //basicBlock.getNext().remove(0);
                        //处理br的cond条件的use关系
                        IrValue cond = ((IrBrInstr) lastInstr).getCond();
                        if (cond.getIrUses().size() == 1) { //说明只是被该条件br语句使用
                            if (cond instanceof IrInstr) {
                                //删除cond这条指令
                                IrBasicBlock parentBlock = ((IrInstr) cond).getBasicBlock();
                                parentBlock.getInstrs().remove(cond);
                                //删除这条指令中的operand关系
                                ArrayList<IrUse> uses = ((IrInstr) cond).getOperands();
                                for (IrUse use : uses) {
                                    IrValue usee = use.getIrUsee();
                                    usee.removeUse(use);
                                }
                            }
                        }
                        //删除旧跳转,插入新跳转
                        basicBlock.getInstrs().remove(lastInstr);
                        IrBrInstr brInstr = new IrBrInstr("", basicBlock.getNext().get(0));
                        brInstr.setBasicBlock(basicBlock);
                        basicBlock.getInstrs().add(brInstr);
                    }
                }
                MergeEmptyBlock();
            }
        }
    }

    public boolean needRemoveFakeCondBr(IrFunction function) {
        for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
            IrInstr lastInstr = basicBlock.getLastInstr();
            if (lastInstr instanceof IrBrInstr &&
                    ((IrBrInstr) lastInstr).isCond() &&
                    lastInstr.getOperand(1).equals(lastInstr.getOperand(2))) {
                return true;
            }
        }
        return false;
    }
}
