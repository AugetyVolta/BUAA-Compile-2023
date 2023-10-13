package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import lexer.token.Token;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.VarSymbol;

import java.util.ArrayList;

public class ForStmtNode extends Node {
    private String name = "<ForStmt>";

    private LValNode lVal;

    private TerminalNode assign;

    private ExpNode exp;

    public ForStmtNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof LValNode) {
            lVal = (LValNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.ASSIGN) {
                assign = (TerminalNode) child;
            }
        } else if (child instanceof ExpNode) {
            exp = (ExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append(assign.toString());
        sb.append(exp.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList, SymbolTable symbolTable) {
        super.checkError(errorList, symbolTable); //首先去看lVal的错误
        String name = lVal.getName();
        Symbol symbol = null;
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1 != null) {
            if (symbolTable1.hasSymbol(name)) {
                symbol = symbolTable1.getSymbol(name);
                break;
            }
            symbolTable1 = symbolTable1.getFatherTable();
        }
        if (symbol instanceof VarSymbol) { //h,给常量赋值
            if (((VarSymbol) symbol).isConst()) {
                Error error = new Error(lVal.getLine(), ErrorType.ASSIGN_TO_CONST);
                errorList.add(error);
            }
        }
    }
}
