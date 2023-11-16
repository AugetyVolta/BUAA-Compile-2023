package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrValue;
import symbol.Symbol;
import symbol.SymbolManager;
import symbol.SymbolTable;
import symbol.VarSymbol;

import java.util.ArrayList;

public class StmtGetint extends StmtEle {
    private LValNode lVal;
    private TerminalNode assign;
    private TerminalNode getintTk;

    private TerminalNode lparent;

    private TerminalNode rparent;
    private TerminalNode semicn;

    public StmtGetint() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof LValNode) {
            lVal = (LValNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.ASSIGN) {
                assign = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.GETINTTK) {
                getintTk = (TerminalNode) child;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append(assign.toString());
        sb.append(getintTk.toString());
        sb.append(lparent.toString());
        sb.append(rparent.toString());
        sb.append(semicn.toString());
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        super.checkError(errorList); //首先去看lVal的错误
        String name = lVal.getName();
        Symbol symbol = symbolTable.getSymbol(name);
        if (symbol instanceof VarSymbol) { //h,给常量赋值
            if (((VarSymbol) symbol).isConst()) {
                Error error = new Error(lVal.getLine(), ErrorType.ASSIGN_TO_CONST);
                errorList.add(error);
            }
        }
    }

    @Override
    public IrValue buildIR() {
        IrValue lValPointer = lVal.getLValPointer();//需要获得lval的指针
        IrValue getintValue = IrBuilder.IRBUILDER.buildGetIntInstr();
        IrBuilder.IRBUILDER.buildStoreInstr(getintValue, lValPointer);
        return null;
    }
}
