package symbol;

import java.util.Stack;

public class SymbolManager {
    public static SymbolManager Manager = new SymbolManager();

    private final Stack<SymbolTable> symbolTables = new Stack<>();

    private SymbolTable curSymbolTable;

    private int LoopLevel;//递归的深度

    private boolean isGlobal;//是否是全局变量

    public SymbolManager() {
        this.LoopLevel = 0;
        this.isGlobal = true;
        this.symbolTables.push(new SymbolTable(null));
        curSymbolTable = symbolTables.peek();
    }

    public void resetSymbolTable() {
        symbolTables.pop();//弹出之前的主符号表
        this.LoopLevel = 0;
        this.isGlobal = true;
        this.symbolTables.push(new SymbolTable(null));
        curSymbolTable = symbolTables.peek();
    }

    public SymbolTable getCurSymbolTable() {
        return curSymbolTable;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    //进入变量
    public void enterBlock() {
        isGlobal = false; //只要进入Block,就不再是全局变量,因为在全局变量定义完后才定义函数
        curSymbolTable = new SymbolTable(curSymbolTable);
        symbolTables.push(curSymbolTable);
    }

    public void leaveBlock() {
        symbolTables.pop();
        curSymbolTable = symbolTables.peek();
    }

    public void enterLoop() {
        LoopLevel++;
    }

    public void leaveLoop() {
        LoopLevel--;
    }

    public int getLoopLevel() {
        return LoopLevel;
    }
}
