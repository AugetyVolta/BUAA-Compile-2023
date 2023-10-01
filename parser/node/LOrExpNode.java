package parser.node;

public class LOrExpNode extends Node {
    private String name = "<LOrExp>";

    private LAndExpNode lAndExp = null;

    private TerminalNode operator = null;

    private LOrExpNode lOrExp = null;

    public LOrExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof LOrExpNode) {
            lOrExp = (LOrExpNode) child;
        } else if (child instanceof LAndExpNode) {
            lAndExp = (LAndExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lOrExp != null) {
            sb.append(lOrExp.toString());
            sb.append(operator.toString());
            sb.append(lAndExp.toString());
        } else if (lAndExp != null) {
            sb.append(lAndExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

}
