package llvm.instr;

import llvm.IrValue;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;

public class IrLoadInstr extends IrInstr {

    //将指针地址存的值存入name所指的变量中
    public IrLoadInstr(String name, IrValue pointer) {
        super(name, ((IrPointerType) pointer.getType()).getRefType(), IrInstrType.LOAD);
        modifyOperand(pointer, 0);
    }

    public IrValue getPointer() {
        return getOperand(0); //获取地址指针
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s", getName(), getType(), getPointer().getType(), getPointer().getName());
    }

}
