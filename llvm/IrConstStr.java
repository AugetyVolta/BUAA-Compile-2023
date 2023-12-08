package llvm;

import llvm.type.IrPointerType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrConstStr extends IrValue {
    private String content;

    private IrValueType refType;

    public IrConstStr(String name, IrValueType irValueType, String content) {
        super(name, new IrPointerType(irValueType));//数组指针类型 [a x i8]
        this.refType = irValueType;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("%s = constant %s c\"%s\\00\"", getName(), refType, content);
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildMipsGlobalString(getName().substring(1), content.replace("\n","\\n"));
    }

}
