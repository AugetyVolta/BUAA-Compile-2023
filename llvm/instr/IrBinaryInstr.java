package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrBinaryInstr extends IrInstr {

    public IrBinaryInstr(String name, IrValueType irValueType, IrInstrType irInstrType, IrValue operand1, IrValue operand2) {
        super(name, irValueType, irInstrType);
        modifyOperand(operand1, 0);
        modifyOperand(operand2, 1);
    }

    public IrValue getOperand1() {
        return getOperand(0);
    }

    public IrValue getOperand2() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s", getName(), getIrInstrType().toString().toLowerCase(), getType(), getOperand1().getName(), getOperand2().getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        //两侧的东西一定都在内存中已经存了,因此直接获得的即可
        //operand1
        if (getOperand1() instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand1()).getValue());
        } else {
            int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset1);
        }
        //operand2
        if (getOperand2() instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(9, ((IrConstInt) getOperand2()).getValue());
        } else {
            int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
            MipsBuilder.MIPSBUILDER.buildLw(9, 29, offset2);
        }
        //开始构建指令
        switch (getIrInstrType()) {
            case ADD:
                MipsBuilder.MIPSBUILDER.buildAddu(10, 8, 9);
                break;
            case SUB:
                MipsBuilder.MIPSBUILDER.buildSubu(10, 8, 9);
                break;
            case MUL:
                MipsBuilder.MIPSBUILDER.buildMult(8, 9);
                MipsBuilder.MIPSBUILDER.buildMflo(10);
                break;
            case SDIV:
                MipsBuilder.MIPSBUILDER.buildDiv(8, 9);
                MipsBuilder.MIPSBUILDER.buildMflo(10);
                break;
            case SREM:
                MipsBuilder.MIPSBUILDER.buildDiv(8, 9);
                MipsBuilder.MIPSBUILDER.buildMfhi(10);
                break;
        }
        //将指令的值存入内存
        int resultOffset = MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
        MipsBuilder.MIPSBUILDER.buildSw(10, 29, resultOffset);
    }
}
