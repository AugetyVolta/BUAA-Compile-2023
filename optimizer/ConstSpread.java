package optimizer;

import llvm.*;
import llvm.instr.IrBinaryInstr;
import llvm.instr.IrInstr;
import llvm.instr.IrLoadInstr;
import mips.MipsBuilder;

import java.util.ArrayList;
import java.util.Iterator;

public class ConstSpread {
    private IrModule module;

    public ConstSpread(IrModule module) {
        this.module = module;
        //全局变量常量替换
        spreadGlobalConst();
        //运算中常量替换
        spreadCalulateConst();
    }

    public void spreadGlobalConst() {
        ArrayList<IrGlobalVariable> globalVariables = module.getIrGlobalVariables();
        for (IrGlobalVariable globalVariable : globalVariables) {
            if (globalVariable.isConst() && globalVariable.getDim() == 0) {
                for (IrFunction function : module.getIrFunctions()) {
                    for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
                        Iterator<IrInstr> it = basicBlock.getInstrs().iterator();
                        while (it.hasNext()) {
                            IrInstr instr = it.next();
                            if (instr instanceof IrLoadInstr && instr.getOperand(0).equals(globalVariable)) {
                                //将load的值都替换为常量值
                                IrValue value = globalVariable.getInitValues().get(0);
                                replaceAllUse(instr, value);
                                //删除原来的load指令
                                it.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public void spreadCalulateConst() {
        for (IrFunction function : module.getIrFunctions()) {
            for (IrBasicBlock basicBlock : function.getBasicBlocks()) {
                Iterator<IrInstr> it = basicBlock.getInstrs().iterator();
                while (it.hasNext()) {
                    IrInstr instr = it.next();
                    if (instr instanceof IrBinaryInstr) {
                        if (instr.getOperand(0) instanceof IrConstInt &&
                                instr.getOperand(1) instanceof IrConstInt) {
                            int operand1 = ((IrConstInt) instr.getOperand(0)).getValue();
                            int operand2 = ((IrConstInt) instr.getOperand(1)).getValue();
                            IrValue value = null;
                            switch (instr.getIrInstrType()) {
                                case ADD:
                                    value = new IrConstInt(operand1 + operand2);
                                    break;
                                case SUB:
                                    value = new IrConstInt(operand1 - operand2);
                                    break;
                                case MUL:
                                    value = new IrConstInt(operand1 * operand2);
                                    break;
                                case SDIV:
                                    value = new IrConstInt(operand1 / operand2);
                                    break;
                                case SREM:
                                    value = new IrConstInt(operand1 % operand2);
                                    break;
                            }
                            replaceAllUse(instr, value);
                            //删除原来指令
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    public void replaceAllUse(IrValue src, IrValue dst) {
        ArrayList<IrUse> uses = src.getIrUses();
        for (IrUse use : uses) {
            use.setIrUsee(dst);
            //需要把dst的use也连接上
            dst.addUseToUser(use);
        }
        uses.clear();
    }
}
