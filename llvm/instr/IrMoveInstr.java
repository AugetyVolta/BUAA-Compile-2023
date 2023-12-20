package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrMoveInstr extends IrInstr {

    public IrMoveInstr(String name, IrValue dst, IrValue src) {
        super(name, IrValueType.NONE, IrInstrType.MOVE);
        modifyOperand(dst, 0);
        modifyOperand(src, 1);
    }

    @Override
    public String toString() {
        return String.format("move %s %s,%s %s", getOperand(0).getType(), getOperand(0).getName(), getOperand(1).getType(), getOperand(1).getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        //看看move的对象是否有定义
        int reg1;
        if (MipsBuilder.MIPSBUILDER.hasSymbol(getOperand(0))) {
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand(0))) {
                reg1 = MipsBuilder.MIPSBUILDER.getReg(getOperand(0));
            } else {
                reg1 = MipsBuilder.MIPSBUILDER.allocReg(getOperand(0));
            }
        } else {
            MipsBuilder.MIPSBUILDER.buildVarSymbol(getOperand(0));
            reg1 = MipsBuilder.MIPSBUILDER.allocReg(getOperand(0));
        }
        int reg2;
        if (getOperand(1) instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(reg1, ((IrConstInt) getOperand(1)).getValue());
        } else {
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand(1))) {
                reg2 = MipsBuilder.MIPSBUILDER.getReg(getOperand(1));
                MipsBuilder.MIPSBUILDER.buildMove(reg1, reg2);
            } else {
                int offset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand(1));
                MipsBuilder.MIPSBUILDER.buildLw(reg1,29,offset);
            }

        }
    }
}
