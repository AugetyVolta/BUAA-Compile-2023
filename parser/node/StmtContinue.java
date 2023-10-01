package parser.node;

import lexer.token.SyntaxType;

public class StmtContinue extends StmtEle {
    private TerminalNode continueTk;
    private TerminalNode semicn;

    public StmtContinue() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.CONTINUETK) {
                continueTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(continueTk.toString());
        sb.append(semicn.toString());
        return sb.toString();
    }
}
