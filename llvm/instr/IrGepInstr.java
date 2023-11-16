package llvm.instr;

import llvm.IrValue;
import llvm.type.IrArrayType;
import llvm.type.IrIntegetType;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;

public class IrGepInstr extends IrInstr {
    //getelementptr [6 x i32], [6 x i32]* @a, i32 0, i32 4 a[4] 返回一个指向a[4]的指针
    //getelementptr i32, i32* @a, i32 4 a[4] 返回一个指向a[4]的指针
    public IrGepInstr(String name, IrValue pointer, IrValue offset) {
        super(name, new IrPointerType(IrIntegetType.INT32), IrInstrType.GETELEMENTPTR);
        modifyOperand(pointer, 0);
        modifyOperand(offset, 1);
    }

    public IrValue getPointer() {
        return getOperand(0);
    }

    public IrValue getOffset() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IrValueType refType = ((IrPointerType) getPointer().getType()).getRefType();
        sb.append(getName()).append(" = getelementptr inbounds ");
        sb.append(refType).append(", ");
        sb.append(getPointer().getType()).append(" ").append(getPointer().getName()).append(", "); //type name,必须这样写，因为可能指针是一条指令，但是toString方法被重写了
        if (refType instanceof IrArrayType) { //如果传入的是[ x i32]*,就需要多加一个i32 0
            sb.append("i32 0, ");
        }
        sb.append(getOffset().getType()).append(" ").append(getOffset().getName()); //type name
        return sb.toString();
    }


}
