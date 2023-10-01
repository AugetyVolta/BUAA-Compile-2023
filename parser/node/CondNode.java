package parser.node;

public class CondNode extends Node {
    private String name = "<Cond>";

    private LOrExpNode lOrExp;

    public CondNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof LOrExpNode) {
            lOrExp = (LOrExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }
}
