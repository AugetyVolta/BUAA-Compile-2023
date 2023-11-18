package symbol;

import llvm.IrConstInt;
import llvm.IrValue;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private boolean isConst; //是否是常数

    private int dim = 0; //变量维度，0为变量，1为一维数组，2为二维数组

    private final int[] size = new int[2];

    private ArrayList<Integer> arrayInitVal = new ArrayList<>();//存储变量初值,存储数组初值，将二维数组变为一维数组

    private IrValue llvmValue;

    public VarSymbol(SymbolType symbolType, DataType dataType, String name, int line, boolean isConst, int dim) {
        super(symbolType, dataType, name, line);
        this.isConst = isConst;
        this.dim = dim;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public void setSize(int x, int y) {
        size[0] = x;
        size[1] = y;
    }

    public int[] getSize() {
        return size;
    }

    //获取数组长度
    public int getLength() {
        if (dim == 1) {
            return size[1]; //对于一维数组长度存在size[1]
        } else {
            return size[0] * size[1];
        }
    }

    //获取常量初值
    public int getInitVal() {
        if (arrayInitVal.size() == 0) {
            return 0;//没有初始化
        } else {
            return arrayInitVal.get(0);
        }
    }

    //获取一维数组初值
    public int getInitVal(int index) {
        return arrayInitVal.get(index);
    }

    //获取二维数组初值
    public int getInitVal(int index1, int index2) {
        return arrayInitVal.get(index1 * size[1] + index2);
    }

    //生成ArrayList<IrConstInt> initValues
    public ArrayList<IrConstInt> getInitValues() {
        ArrayList<IrConstInt> initValues = new ArrayList<>();
        for (int value : arrayInitVal) {
            initValues.add(new IrConstInt(value));
        }
        return initValues;
    }

    public void setInitVal(int initVal) {
        this.arrayInitVal.add(initVal);
    }

    public ArrayList<Integer> getArrayInitVal() {
        return arrayInitVal;
    }

    public void setArrayInitVal(ArrayList<Integer> arrayInitVal) {
        this.arrayInitVal = arrayInitVal;
    }

    public void setLlvmValue(IrValue llvmValue) {
        this.llvmValue = llvmValue;
    }

    public IrValue getLlvmValue() {
        return llvmValue;
    }
}
