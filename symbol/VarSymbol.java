package symbol;

public class VarSymbol extends Symbol {
    private boolean isConst; //是否是常数

    private int dim = 0; //维度，0为变量，1为一维数组，2为二维数组

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
}
