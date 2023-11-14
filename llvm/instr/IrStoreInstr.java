package llvm.instr;

import llvm.IrValue;
import llvm.type.IrValueType;

public class IrStoreInstr extends IrInstr {
    public IrStoreInstr(String name, IrValue fromValue, IrValue pointer) {
        super(name, IrValueType.NONE, IrInstrType.STORE);
        modifyOperand(fromValue, 0);//原数
        modifyOperand(pointer, 1);//待存入的地址
    }

    public IrValue getFrom() {
        return getOperand(0);
    }

    public IrValue getTo() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s %s", getFrom().getType(), getFrom().getName(), getTo().getType(), getTo().getName());
    }

}
