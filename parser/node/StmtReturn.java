package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import symbol.SymbolManager;
import symbol.SymbolTable;

import java.util.ArrayList;

public class StmtReturn extends StmtEle {
    private TerminalNode returnTk;

    private ExpNode exp = null;

    private TerminalNode semicn;

    public StmtReturn() {

    }

    public int getLine() {
        return returnTk.getLine();
    }

    public boolean hasExp() {
        return exp != null;
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.RETURNTK) {
                returnTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        } else if (child instanceof ExpNode) {
            exp = (ExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnTk.toString());
        if (exp != null) {
            sb.append(exp.toString());
        }
        sb.append(semicn.toString());
        return sb.toString();
    }

    //错误f
    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        if (exp != null && !symbolTable.isNeedReturn()) {
            Error error = new Error(returnTk.getLine(), ErrorType.ERROR_USED_RETURN);
            errorList.add(error);
        }
    }

    @Override
    public IrValue buildIR() {
        if (exp == null) {
            IrBuilder.IRBUILDER.buildRetInstr(null);
        } else {
            IrBuilder.IRBUILDER.buildRetInstr(exp.buildIR());
        }
        return null;
    }
}
