package parser.node;

import lexer.token.SyntaxType;
import symbol.SymbolTable;

public class PrimaryExpNode extends Node {
    private String name = "<PrimaryExp>";

    private TerminalNode lparent = null;

    private ExpNode exp = null;

    private TerminalNode rparent = null;

    private LValNode lVal = null;

    private NumberNode number = null;

    public PrimaryExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof ExpNode) {
            exp = (ExpNode) child;
        } else if (child instanceof LValNode) {
            lVal = (LValNode) child;
        } else if (child instanceof NumberNode) {
            number = (NumberNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lparent != null) {
            sb.append(lparent.toString());
            sb.append(exp.toString());
            sb.append(rparent.toString());
        } else if (lVal != null) {
            sb.append(lVal.toString());
        } else if (number != null) {
            sb.append(number.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    public int getDim(SymbolTable symbolTable) {
        if (exp != null) {
            return exp.getDim(symbolTable);
        } else if (lVal != null) {
            return lVal.getDim(symbolTable);
        } else { //number
            return 0;
        }
    }

    public int execute() {
        if (exp != null) {
            return exp.execute();
        } else if (lVal != null) {
            return lVal.execute();
        } else {
            return number.execute();
        }
    }
}
