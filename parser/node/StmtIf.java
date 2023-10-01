package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class StmtIf extends StmtEle {
    private TerminalNode ifTk;

    private TerminalNode lparent;

    private CondNode cond;

    private TerminalNode rparent;

    private ArrayList<StmtNode> stmts = new ArrayList<>();

    private TerminalNode elseTk = null;

    public StmtIf() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IFTK) {
                ifTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.ELSETK) {
                elseTk = (TerminalNode) child;
            }
        } else if (child instanceof CondNode) {
            cond = (CondNode) child;
        } else if (child instanceof StmtNode) {
            stmts.add((StmtNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ifTk.toString());
        sb.append(lparent.toString());
        sb.append(cond.toString());
        sb.append(rparent.toString());
        sb.append(stmts.get(0).toString());
        if (elseTk != null) {
            sb.append(elseTk.toString());
            sb.append(stmts.get(1).toString());
        }
        return sb.toString();
    }
}
