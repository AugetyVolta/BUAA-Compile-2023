package symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> symbols = new HashMap<>(); //符号表中存贮的符号
    private ArrayList<SymbolTable> childSymbolTables = new ArrayList<>();//子符号表
    private SymbolTable fatherTable = null;
    private boolean needReturn = false;

    //会自动继承顶层符号表的循环数和是否需要返回值
    public SymbolTable(SymbolTable fatherTable) {
        this.fatherTable = fatherTable;
        if (fatherTable != null) {
            needReturn = fatherTable.isNeedReturn();
            //将自己加入到父亲的子符号表中
            fatherTable.addChildSymbolTable(this);
        }
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    } //当前符号表有无符号

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

    //自下而上递归且根据类型查找符号
    public Symbol getSymbol(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        } else if (fatherTable != null) {
            return fatherTable.getSymbol(name);
        } else {
            return null;
        }
    }

    //自下而上递归且根据类型查找符号
    public Symbol getSymbol(String name, SymbolType symbolType) {
        if (symbols.containsKey(name) && symbols.get(name).getSymbolType() == symbolType) {
            return symbols.get(name);
        } else if (fatherTable != null) {
            return fatherTable.getSymbol(name, symbolType);
        } else {
            return null;
        }
    }

    //添加子符号表
    public void addChildSymbolTable(SymbolTable symbolTable) {
        childSymbolTables.add(symbolTable);
    }

    public ArrayList<SymbolTable> getChildSymbolTables() {
        return childSymbolTables;
    }
}
