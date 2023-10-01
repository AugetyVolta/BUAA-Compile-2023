package parser.node;

import lexer.token.SyntaxType;

public class StmtBreak extends StmtEle {
    private TerminalNode breakTk;
    private TerminalNode semicn;

    public StmtBreak() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.BREAKTK) {
                breakTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(breakTk.toString());
        sb.append(semicn.toString());
        return sb.toString();
    }


}
