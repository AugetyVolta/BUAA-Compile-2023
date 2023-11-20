package llvm.type;

/**
 * int的类型,i32,i1,void
 */
public class IrIntegetType extends IrValueType {
    public int numBits;
    public final static IrIntegetType VOID = new IrIntegetType(0);
    public final static IrIntegetType INT1 = new IrIntegetType(1);
    public final static IrIntegetType INT8 = new IrIntegetType(8);
    public final static IrIntegetType INT32 = new IrIntegetType(32);

    public IrIntegetType(int numBits) {
        this.numBits = numBits;
    }

    public static IrIntegetType getVOID() {
        return VOID;
    }

    public static IrIntegetType getINT1() {
        return INT1;
    }

    public static IrIntegetType getInt8() {
        return INT8;
    }

    public static IrIntegetType getINT32() {
        return INT32;
    }

    public int getNumBits() {
        return numBits;
    }

    @Override
    public String toString() {
        switch (numBits) {
            case 0:
                return "void";
            case 1:
                return "i1";
            case 8:
                return "i8";
            case 32:
                return "i32";
            default:
                return null;
        }
    }
}
