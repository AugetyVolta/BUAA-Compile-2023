package llvm;

import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

public class IrConstInt extends IrValue {
    public IrConstInt(int value) {
        super(String.valueOf(value), IrIntegetType.INT32);
    }

    public int getValue() {
        return Integer.parseInt(getName());
    }

    @Override
    public String toString() {
        return getType() + " " + getName();
    }
}
