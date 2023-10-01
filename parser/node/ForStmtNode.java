package parser.node;

import lexer.token.SyntaxType;
import lexer.token.Token;

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

}
