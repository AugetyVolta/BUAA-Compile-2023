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
        //构建比较指令
        switch (getCond()) {
            case EQ:
                MipsBuilder.MIPSBUILDER.buildSeq(10, 8, 9);
                break;
            case NE:
                MipsBuilder.MIPSBUILDER.buildSne(10, 8, 9);
                break;
            case SGE:
                MipsBuilder.MIPSBUILDER.buildSge(10, 8, 9);
                break;
            case SGT:
                MipsBuilder.MIPSBUILDER.buildSgt(10, 8, 9);
                break;
            case SLE:
                MipsBuilder.MIPSBUILDER.buildSle(10, 8, 9);
                break;
            case SLT:
                MipsBuilder.MIPSBUILDER.buildSlt(10, 8, 9);
                break;
        }
        //将指令的值存入内存
        int resultOffset = MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
        MipsBuilder.MIPSBUILDER.buildSw(10, 29, resultOffset);
    }
}
