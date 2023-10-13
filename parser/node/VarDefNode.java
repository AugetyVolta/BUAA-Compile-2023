package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.DataType;
import symbol.SymbolTable;
import symbol.SymbolType;
import symbol.VarSymbol;

import java.util.ArrayList;

public class VarDefNode extends Node {
    private String name = "<VarDef>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    private ArrayList<ConstExpNode> constExps = new ArrayList<>();

    private TerminalNode assign = null;

    private InitValNode initVal = null;

    public VarDefNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LBRACK) {
                lbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.RBRACK) {
                rbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.ASSIGN) {
                assign = (TerminalNode) child;
            }
        } else if (child instanceof ConstExpNode) {
            constExps.add((ConstExpNode) child);
        } else if (child instanceof InitValNode) {
            initVal = (InitValNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        for (int i = 0; i < lbracks.size(); i++) {
            sb.append(lbracks.get(i).toString());
            sb.append(constExps.get(i).toString());
            sb.append(rbracks.get(i).toString());
        }
        if (assign != null) {
            sb.append(assign.toString());
            sb.append(initVal.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList, SymbolTable symbolTable) {
        if (symbolTable.hasSymbol(ident.getName())) {
            Error error = new Error(ident.getLine(), ErrorType.REDEFINED_SYMBOL);
            errorList.add(error);
        } else {
            VarSymbol varSymbol = new VarSymbol(SymbolType.VAR,
                    DataType.INT,
                    ident.getName(),
                    ident.getLine(),
                    false,
                    lbracks.size());
            symbolTable.addSymbol(varSymbol);
        }
    }
}
