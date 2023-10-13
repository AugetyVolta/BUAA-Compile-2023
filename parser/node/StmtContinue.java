package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.SymbolTable;

import java.util.ArrayList;

public class StmtContinue extends StmtEle {
    private TerminalNode continueTk;
    private TerminalNode semicn;

    public StmtContinue() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.CONTINUETK) {
                continueTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(continueTk.toString());
        sb.append(semicn.toString());
        return sb.toString();
    }

    //m错误
    @Override
    public void checkError(ArrayList<Error> errorList, SymbolTable symbolTable) {
        if (symbolTable.getLoopLevel() == 0) { //没有在递归中
            Error error = new Error(continueTk.getLine(), ErrorType.ERROR_USED_BREAK_OR_CONTINUE);
            errorList.add(error);
        }
    }
}
