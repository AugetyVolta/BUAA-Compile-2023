package parser.node;

import lexer.token.SyntaxType;

public class StmtReturn extends StmtEle {
    private TerminalNode returnTk;

    private ExpNode exp = null;

    private TerminalNode semicn;

    public StmtReturn() {

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
}
