package llvm;

import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

public class IrConstInt extends IrValue {
    private int value;

    public IrConstInt(int value) {
        super(String.valueOf(value), IrIntegetType.INT32);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getType() + " " + getName();
    }
}
