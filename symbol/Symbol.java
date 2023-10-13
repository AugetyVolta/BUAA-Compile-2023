package symbol;

public class Symbol {
    private SymbolType symbolType; // Var,Func

    private DataType dataType; //VOID,INT

    private String name; //Symbol的name

    private int line; //Symbol所处的行

    public Symbol() {

    }

    public Symbol(SymbolType symbolType, DataType dataType, String name, int line) {
        this.symbolType = symbolType;
        this.dataType = dataType;
        this.name = name;
        this.line = line;
    }

    public void setSymbolType(SymbolType symbolType) {
        this.symbolType = symbolType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }
}
