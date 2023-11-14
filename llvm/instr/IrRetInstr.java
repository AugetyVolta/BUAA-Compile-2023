package llvm.instr;

import llvm.IrValue;
import llvm.type.IrValueType;

public class IrRetInstr extends IrInstr {

    public IrRetInstr(String name, IrValue retValue) {
        super(name, IrValueType.NONE, IrInstrType.RET);
        if (retValue != null) {
            modifyOperand(retValue, 0);
        }
    }

    public IrValue getRetValue() {
        return getOperand(0);
    }

    @Override
    public String toString() {
        IrValue retValue = getRetValue();
        if (retValue == null) {
            return "ret void";
        } else {
            return String.format("ret %s %s", retValue.getType(), retValue.getName());
        }
    }
}
