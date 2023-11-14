package llvm;

import llvm.type.IrArrayType;
import llvm.type.IrIntegetType;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;

import java.util.ArrayList;

public class IrGlobalVariable extends IrValue {
    private boolean isConst;//是否常量
    private IrValueType refType;//所指变量的类型
    private int dim;//0常数,1一维数组,二维数组最终被化为一维数组操作
    private ArrayList<IrConstInt> initValues = new ArrayList<>();//初始化的值

    //变量名，所存的变量类型
    public IrGlobalVariable(String name, IrValueType irValueType, boolean isConst) {
        super(name, new IrPointerType(irValueType));
        this.refType = irValueType;
        this.isConst = isConst;
        if (irValueType == IrIntegetType.INT32) {
            dim = 0;
        } else if (irValueType instanceof IrArrayType) {
            dim = 1;
        }
    }

    public void addInitValue(IrConstInt constInt) {
        initValues.add(constInt);
    }

    public int getDim() {//获取维度
        return dim;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = dso_local ");
        if (isConst) {
            sb.append("constant ");
        } else {
            sb.append("global ");
        }
        sb.append(refType).append(" ");
        if (initValues.size() != 0) {
            if (dim == 0) {
                sb.append(initValues.get(0).toString());
            } else {
                sb.append("[");
                for (int i = 0; i < initValues.size(); i++) {
                    sb.append(initValues.get(i).toString());
                    if (i < initValues.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
            }
        } else {//没有初始化值,但是需要赋初值
            if (dim == 0) {
                sb.append("0");
            } else {
                sb.append("zeroinitializer");
            }
        }
        return sb.toString();
    }
}
