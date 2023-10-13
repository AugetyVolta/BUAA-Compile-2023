package symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<Integer> dims = new ArrayList<>();

    public FuncSymbol(SymbolType symbolType, DataType dataType, String name, int line) {
        super(symbolType, dataType, name, line);
    }

    public int getParamNum() {
        return dims.size();
    }

    public void addParam(int dim) {
        dims.add(dim);
    }

    public ArrayList<Integer> getDims() {
        return dims;
    }
}
