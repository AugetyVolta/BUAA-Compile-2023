package optimizer;

import llvm.*;
import llvm.instr.*;
import llvm.type.IrIntegetType;

import java.util.ArrayList;
import java.util.Iterator;

import static utils.MyConfig.mipsOutputPath;
import static utils.MyIO.writeFile;

public class RemovePhi {
    private IrModule module;

    public RemovePhi(IrModule module) {
        this.module = module;
        CriticalEdgeSplitting();
    }

    public void CriticalEdgeSplitting() {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            ArrayList<IrBasicBlock> basicBlocks = new ArrayList<>(function.getBasicBlocks());
            for (IrBasicBlock block : basicBlocks) {
                ArrayList<IrBasicBlock> predecessors = new ArrayList<>(block.getPrev());
                //step 1
                for (IrBasicBlock prev : predecessors) {
                    if (prev.getNext().size() > 1) {
                        IrBasicBlock newBlock = IrBuilder.IRBUILDER.buildBasicBlock(function);
                        //删除之前块的前后继信息并插入新块
                        prev.getNext().remove(block);
                        prev.addNextBlock(newBlock);
                        block.getPrev().add(block.getPrev().indexOf(prev), newBlock);
                        block.getPrev().remove(prev);
                        //连接新块
                        newBlock.addPrevBlock(prev);
                        newBlock.addNextBlock(block);
                        IrBuilder.IRBUILDER.buildBrInstr(function, newBlock, block);
                        //设置br
                        IrBrInstr br = (IrBrInstr) prev.getLastInstr();
                        if (br.isCond()) {
                            if (br.getLabel(1).equals(block)) {
                                br.modifyOperand(newBlock, 1);
                            } else {
                                br.modifyOperand(newBlock, 2);
                            }
                        } else {
                            br.modifyOperand(newBlock, 0);
                        }
                    }
                }
            }
            for (IrBasicBlock block : function.getBasicBlocks()) {
                //step 2
                Iterator<IrInstr> it = block.getInstrs().iterator();
                while (it.hasNext()) {
                    IrInstr instr = it.next();
                    if (instr instanceof IrPhiInstr) {
                        for (int i = 0; i < ((IrPhiInstr) instr).getOps(); i++) {
                            String formatName = IrBuilder.IRBUILDER.generateVarName(function);
                            IrPcopyInstr pcopyInstr = new IrPcopyInstr(formatName);
                            pcopyInstr.setName(formatName);
                            pcopyInstr.modifyOperand(instr, 0);
                            pcopyInstr.modifyOperand(instr.getOperand(i), 1);
                            //将pcopy加到前驱块中
                            block.getPrev().get(i).addPcopy(pcopyInstr);
                        }
                        it.remove();
                    }
                }
            }
            basicBlocks = function.getBasicBlocks();
            for (IrBasicBlock basicBlock : basicBlocks) {
                Iterator<IrInstr> it = basicBlock.getInstrs().iterator();
                while (it.hasNext()) {
                    IrInstr instr = it.next();
                    if (instr instanceof IrPcopyInstr && instr.getName().equals("")) {
                        it.remove();
                    }
                }
                //将Pcopy指令展开
                basicBlock.setPcopy();
            }
            removePcopy(function);
        }
    }

    public void removePcopy(IrFunction function) {
        for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
            ArrayList<IrMoveInstr> moveList = new ArrayList<>();
            ArrayList<IrPcopyInstr> pcopyList = basicBlock.getPCList();
            while (satisfyCond(pcopyList) && pcopyList.size() != 0) {
                Iterator<IrPcopyInstr> it = pcopyList.iterator();
                while (satisfyCond(pcopyList) && it.hasNext()) {
                    IrPcopyInstr pcopy = it.next();
                    if (singleAssign(pcopy, pcopyList)) {
                        //append b<-a to seq
                        String formatName = IrBuilder.IRBUILDER.generateVarName(function);
                        IrMoveInstr moveInstr = new IrMoveInstr(formatName, pcopy.getOperand(0), pcopy.getOperand(1));
                        moveInstr.setBasicBlock(basicBlock);
                        moveList.add(moveInstr);
                        //删除
                        it.remove();
                        basicBlock.getInstrs().remove(pcopy);
                    } else {
                        if (pcopy.getOperand(0).equals(pcopy.getOperand(1))) {
                            continue;
                        }
                        //创建一个新变量
                        String formatName = IrBuilder.IRBUILDER.generateVarName(function);
                        IrValue value = new IrValue(formatName, IrIntegetType.INT32);
                        //a'<-a加入
                        IrMoveInstr moveInstr = new IrMoveInstr(IrBuilder.IRBUILDER.generateVarName(function), value, pcopy.getOperand(1));
                        moveInstr.setBasicBlock(basicBlock);
                        moveList.add(moveInstr);
                        pcopy.modifyOperand(value, 1);
                    }
                }
            }
            //加入basicBlock中
            for (IrMoveInstr moveInstr : moveList) {
                basicBlock.insertMove(moveInstr);
            }
        }
    }

    public boolean satisfyCond(ArrayList<IrPcopyInstr> pcopyList) {
        boolean flag = false;
        for (IrPcopyInstr pcopy : pcopyList) {
            if (!pcopy.getOperand(0).equals(pcopy.getOperand(1))) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean singleAssign(IrPcopyInstr pcopyInstr, ArrayList<IrPcopyInstr> pcopyList) {
        boolean flag = true;
        for (IrPcopyInstr instr : pcopyList) {
            if (pcopyInstr.getOperand(0).equals(instr.getOperand(1)) &&
                    !instr.getOperand(0).equals(pcopyInstr.getOperand(0)) &&
                    !instr.getOperand(0).equals(pcopyInstr.getOperand(1))) {
                flag = false;
            }
        }
        return flag;
    }


}
