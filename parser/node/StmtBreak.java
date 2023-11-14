package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.SymbolManager;
import symbol.SymbolTable;

import java.util.ArrayList;

public class StmtBreak extends StmtEle {
    private TerminalNode breakTk;
    private TerminalNode semicn;

    public StmtBreak() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.BREAKTK) {
                breakTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(breakTk.toString());
        sb.append(semicn.toString());
        return sb.toString();
    }

    //m错误
    @Override
    public void checkError(ArrayList<Error> errorList) {
        int loopLevel = SymbolManager.Manager.getLoopLevel();
        if (loopLevel == 0) { //没有在递归中
            Error error = new Error(breakTk.getLine(), ErrorType.ERROR_USED_BREAK_OR_CONTINUE);
            errorList.add(error);
        }
    }
}
