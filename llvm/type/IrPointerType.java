package llvm.type;

/**
 * 指针类型
 */
public class IrPointerType extends IrValueType {
    private IrValueType refType; //指针的类型

    public IrPointerType(IrValueType refType) {
        this.refType = refType;
    }

    public IrValueType getRefType() {
        return refType;
    }

    @Override
    public String toString() {
        return refType + "*";
    }
}
