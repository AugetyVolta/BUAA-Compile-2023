package parser.node;

public class RelExpNode extends Node {
    private String name = "<RelExp>";

    private RelExpNode relExp=null;
    private AddExpNode addExp=null;
    private TerminalNode operator=null;

    public RelExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof RelExpNode) {
            relExp = (RelExpNode) child;
        } else if (child instanceof AddExpNode) {
            addExp = (AddExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (relExp != null) {
            sb.append(relExp.toString());
            sb.append(operator.toString());
            sb.append(addExp.toString());
        } else if (addExp != null) {
            sb.append(addExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
