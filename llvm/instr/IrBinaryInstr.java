package llvm.instr;

import llvm.IrValue;
import llvm.type.IrValueType;

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

}
