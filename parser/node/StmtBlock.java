package parser.node;

import error.Error;
import symbol.SymbolManager;
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
    public void checkError(ArrayList<Error> errorList) {
        SymbolManager.Manager.enterBlock();
        super.checkError(errorList);
        SymbolManager.Manager.leaveBlock();
    }
}
