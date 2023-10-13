package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.FuncSymbol;
import symbol.SymbolTable;
import symbol.SymbolType;
import symbol.VarSymbol;

import java.util.ArrayList;

public class LValNode extends Node {
    private String name = "<LVal>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<ExpNode> exps = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    public LValNode() {

    }

    public String getName() {
        return ident.getName();
    }

    public int getLine() {
        return ident.getLine();
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
            }
        } else if (child instanceof ExpNode) {
            exps.add((ExpNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (lbracks.size() != 0) {
            for (int i = 0; i < lbracks.size(); i++) {
                sb.append(lbracks.get(i).toString());
                sb.append(exps.get(i).toString());
                sb.append(rbracks.get(i).toString());
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList, SymbolTable symbolTable) {
        boolean flag = false;
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1 != null) {
            if (symbolTable1.hasSymbol(ident.getName()) && symbolTable1.getSymbol(ident.getName()).getSymbolType() == SymbolType.VAR) {
                flag = true;
                break;
            }
            symbolTable1 = symbolTable1.getFatherTable();
        }
        if (!flag) {
            Error error = new Error(ident.getLine(), ErrorType.UNDEFINED_SYMBOL);
            errorList.add(error);
        }
    }

    public int getDim(SymbolTable symbolTable) {
        VarSymbol varSymbol = null;
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1 != null) {
            if (symbolTable1.hasSymbol(ident.getName()) && symbolTable1.getSymbol(ident.getName()).getSymbolType() == SymbolType.VAR) {
                varSymbol = (VarSymbol) symbolTable1.getSymbol(ident.getName());
                break;
            }
            symbolTable1 = symbolTable1.getFatherTable();
        }
        return varSymbol.getDim() - lbracks.size();
    }
}
