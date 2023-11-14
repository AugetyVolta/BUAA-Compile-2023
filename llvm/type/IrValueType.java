package llvm.type;

/**
 * value的type
 */
public class IrValueType {
    public static IrValueType FUNCTION = new IrValueType();//函数类型
    public static IrValueType BBLOCK = new IrValueType();//基本块类型
    public static IrValueType NONE = new IrValueType();//没有类型的value的类型,module之类

    public IrValueType() {

    }

    @Override
    public String toString() {
        if (this == FUNCTION) {
            return "function";
        } else if (this == BBLOCK) {
            return "bblock";
        } else if (this == NONE) {
            return "none";
        }
        return "unknown";
    }
}
