package llvm.type;

/**
 * 数组类型,准备全部处理为一维数组
 */
public class IrArrayType extends IrValueType {
    private int length;//数组里元素总数,因为是一维数组,所以和所有的整数的数量一样
    private IrValueType eleType; // 该数组的元素的ValueType,可能之后为double?

    public IrArrayType(int length, IrIntegetType eleType) {
        this.length = length;
        this.eleType = eleType;
    }

    public int getEleNum() {
        return length;
    }

    public IrValueType getEleType() {
        return eleType;
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", length, eleType);
    }

}
