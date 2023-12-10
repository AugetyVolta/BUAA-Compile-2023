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
        int reg1;
        if (getOperand1() instanceof IrConstInt) {
            reg1 = 26;
            MipsBuilder.MIPSBUILDER.buildLi(reg1, ((IrConstInt) getOperand1()).getValue());
        } else {
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand1())) {
                reg1 = MipsBuilder.MIPSBUILDER.getReg(getOperand1());
            } else {
                reg1 = MipsBuilder.MIPSBUILDER.allocReg(getOperand1());
                int offset1 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand1());
                MipsBuilder.MIPSBUILDER.buildLw(reg1, 29, offset1);
            }
        }
        //operand2
        int reg2;
        if (getOperand2() instanceof IrConstInt) {
            reg2 = 27;
            MipsBuilder.MIPSBUILDER.buildLi(reg2, ((IrConstInt) getOperand2()).getValue());
        } else {
            if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand2())) {
                reg2 = MipsBuilder.MIPSBUILDER.getReg(getOperand2());
            } else {
                reg2 = MipsBuilder.MIPSBUILDER.allocReg(getOperand2());
                int offset2 = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand2());
                MipsBuilder.MIPSBUILDER.buildLw(reg2, 29, offset2);
            }
        }
        int reg3;
        if (MipsBuilder.MIPSBUILDER.hasAllocReg(this)) {
            reg3 = MipsBuilder.MIPSBUILDER.getReg(this);
        } else {
            reg3 = MipsBuilder.MIPSBUILDER.allocReg(this);
        }
        //开始构建指令
        switch (getIrInstrType()) {
            case ADD:
                MipsBuilder.MIPSBUILDER.buildAddu(reg3, reg1, reg2);
                break;
            case SUB:
                MipsBuilder.MIPSBUILDER.buildSubu(reg3, reg1, reg2);
                break;
            case MUL:
                MipsBuilder.MIPSBUILDER.buildMult(reg1, reg2);
                MipsBuilder.MIPSBUILDER.buildMflo(reg3);
                break;
            case SDIV:
                MipsBuilder.MIPSBUILDER.buildDiv(reg1, reg2);
                MipsBuilder.MIPSBUILDER.buildMflo(reg3);
                break;
            case SREM:
                MipsBuilder.MIPSBUILDER.buildDiv(reg1, reg2);
                MipsBuilder.MIPSBUILDER.buildMfhi(reg3);
                break;
        }
        //在内存中构建位置
        MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
    }

    public String hash() {
        if (getIrInstrType() == IrInstrType.ADD || getIrInstrType() == IrInstrType.MUL) {
            StringBuilder sb = new StringBuilder();
            sb.append(getIrInstrType().toString().toLowerCase());
            sb.append(getType());
            String name1 = getOperand1().getName();
            String name2 = getOperand2().getName();
            if (name1.compareTo(name2) < 0) {
                sb.append(name1);
                sb.append(name2);
            } else {
                sb.append(name2);
                sb.append(name1);
            }
            return sb.toString();
        } else {
            return this.toString().substring(0, this.toString().indexOf('='));
        }
    }
}
