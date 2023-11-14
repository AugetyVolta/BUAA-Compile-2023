package parser.node;

public class ConstExpNode extends Node {
    private String name = "<ConstExp>";

    private AddExpNode addExp;

    public ConstExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof AddExpNode) {
            addExp = (AddExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

    public int execute() {
        return addExp.execute();
    }
}
