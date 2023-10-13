package symbol;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> symbols = new HashMap<>(); //符号表中存贮的符号

    private SymbolTable fatherTable = null;

    private boolean needReturn = false;

    private int loopLevel = 0;

    public SymbolTable(SymbolTable fatherTable) {
        this.fatherTable = fatherTable;
        if (fatherTable != null) {
            needReturn = fatherTable.isNeedReturn();
            loopLevel = fatherTable.getLoopLevel();
        }
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public Symbol getSymbol(String name) {
        return symbols.getOrDefault(name, null);
    }

    public boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    }

    public SymbolTable getFatherTable() {
        return this.fatherTable;
    }

    public boolean hasFather() {
        return fatherTable != null;
    }

    public void setNeedReturn(boolean needReturn) {
        this.needReturn = needReturn;
    }

    public boolean isNeedReturn() {
        return needReturn;
    }

    public void setLoopLevel(int loopLevel) {
        this.loopLevel = loopLevel;
    }

    public int getLoopLevel() {
        return loopLevel;
    }
}
