package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrIcmpInstr extends IrInstr {
    private IrInstrType cond;

    //name是赋值的变量
    public IrIcmpInstr(String name, IrInstrType cond, IrValue operand1, IrValue operand2) {
        super(name, IrIntegetType.INT1, IrInstrType.ICMP);
        this.cond = cond;
        modifyOperand(operand1, 0);
        modifyOperand(operand2, 1);
    }

    public IrInstrType getCond() {
        return cond;
    }

    public IrValue getOperand1() {
        return getOperand(0);
    }

    public IrValue getOperand2() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        return String.format("%s = icmp %s %s %s, %s", getName(), getCond().toString().toLowerCase(), getOperand1().getType(), getOperand1().getName(), getOperand2().getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
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
        //构建比较指令
        switch (getCond()) {
            case EQ:
                MipsBuilder.MIPSBUILDER.buildSeq(reg3, reg1, reg2);
                break;
            case NE:
                MipsBuilder.MIPSBUILDER.buildSne(reg3, reg1, reg2);
                break;
            case SGE:
                MipsBuilder.MIPSBUILDER.buildSge(reg3, reg1, reg2);
                break;
            case SGT:
                MipsBuilder.MIPSBUILDER.buildSgt(reg3, reg1, reg2);
                break;
            case SLE:
                MipsBuilder.MIPSBUILDER.buildSle(reg3, reg1, reg2);
                break;
            case SLT:
                MipsBuilder.MIPSBUILDER.buildSlt(reg3, reg1, reg2);
                break;
        }
        //将指令的值存入内存
        MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
    }

    public String hash() {
        return this.toString().substring(0, this.toString().indexOf('='));
    }
}
