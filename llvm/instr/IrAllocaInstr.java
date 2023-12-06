package llvm.instr;

import llvm.IrBasicBlock;
import llvm.type.IrArrayType;
import llvm.type.IrIntegetType;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrAllocaInstr extends IrInstr {
    private IrValueType refType;

    public IrAllocaInstr(String name, IrValueType type) {
        super(name, new IrPointerType(type), IrInstrType.ALLOCA);
        this.refType = type;
    }

    @Override
    public String toString() {
        return String.format("%s = alloca %s", getName(), refType);
    }

    public IrValueType getRefType() {
        return refType;
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        //构建一个新的符号,为它开辟一个空间,这个指针的地址就是存的数的地址,对于数组来说,得到的是存着数组初始地址的地址
        if (refType == IrIntegetType.INT32) {
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
        } else if (refType instanceof IrArrayType) {
            int arrayLength = ((IrArrayType) refType).getEleNum();
            MipsBuilder.MIPSBUILDER.buildArraySymbol(this, arrayLength);
        }
    }
}
