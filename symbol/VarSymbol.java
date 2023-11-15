package symbol;

import llvm.IrValue;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private boolean isConst; //是否是常数

    private int dim = 0; //变量维度，0为变量，1为一维数组，2为二维数组

    private final int[] size = new int[2];

    private int initVal;//存储变量常量的初值

    private ArrayList<Integer> arrayInitVal = new ArrayList<>();//存储数组初值，将二维数组变为一维数组

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

    //获取常量初值
    public int getInitVal() {
        return initVal;
    }

    //获取一维数组初值
    public int getInitVal(int index) {
        return arrayInitVal.get(index);
    }

    //获取二维数组初值
    public int getInitVal(int index1, int index2) {
        return arrayInitVal.get(index1 * size[1] + index2);
    }

    public void setInitVal(int initVal) {
        this.initVal = initVal;
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
