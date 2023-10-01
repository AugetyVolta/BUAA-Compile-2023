package parser.node;

import lexer.token.SyntaxType;

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
}
