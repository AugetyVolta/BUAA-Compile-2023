package llvm.instr;

import llvm.IrValue;
import llvm.type.IrIntegetType;

public class IrPcopyInstr extends IrInstr {
    public IrPcopyInstr(String name) {
        super(name, IrIntegetType.INT32, IrInstrType.PCOPY);
    }

    @Override
    public String toString() {
        return String.format("pcopy %s %s, %s %s", getOperand(0).getType(),
                getOperand(0).getName(),
                getOperand(1).getType(),
                getOperand(1).getName());
    }


}
