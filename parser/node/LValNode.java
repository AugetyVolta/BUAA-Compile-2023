package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.*;

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
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        String name = ident.getName();
        //没找到
        if (symbolTable.getSymbol(name, SymbolType.VAR) == null) {
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

    @Override
    public int execute() {
        VarSymbol valSymbol = (VarSymbol) SymbolManager.Manager.getCurSymbolTable().getSymbol(ident.getName(), SymbolType.VAR);
        if (valSymbol == null) {
            return 0;
        }
        //用于获取引用的下标
        ArrayList<Integer> lens = new ArrayList<>();
        for (ExpNode expNode : exps) {
            lens.add(expNode.execute());
        }
        if (valSymbol.getDim() == 0) {//常数
            return valSymbol.getInitVal();
        } else if (valSymbol.getDim() == 1) {//一维数组
            return valSymbol.getInitVal(lens.get(0));
        } else if (valSymbol.getDim() == 2) {//二维数组
            return valSymbol.getInitVal(lens.get(0), lens.get(1));
        }
        return 0;
    }
}
