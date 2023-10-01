package parser.node;

public class StmtExp extends StmtEle {
    private ExpNode exp = null;

    private TerminalNode semicn;

    public StmtExp() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof ExpNode) {
            exp = (ExpNode) child;
        } else if (child instanceof TerminalNode) {
            semicn = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append(exp.toString());
        }
        sb.append(semicn.toString());
        return sb.toString();
    }
}
