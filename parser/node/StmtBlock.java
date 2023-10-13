package parser.node;

import error.Error;
import symbol.SymbolTable;

import java.util.ArrayList;

public class StmtBlock extends StmtEle {
    private BlockNode block;

    public StmtBlock() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof BlockNode) {
            block = (BlockNode) child;
        }
    }

    @Override
    public String toString() {
        return block.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList, SymbolTable symbolTable) {
        SymbolTable newSymbolTable = new SymbolTable(symbolTable);
        // newSymbolTable.setNeedReturn(symbolTable.isNeedReturn());
        // newSymbolTable.setLoopLevel(symbolTable.getLoopLevel()); 这两个本身已经继承了
        super.checkError(errorList, newSymbolTable);
    }
}
