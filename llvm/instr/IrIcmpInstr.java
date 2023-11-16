package llvm.instr;

import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

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
}
