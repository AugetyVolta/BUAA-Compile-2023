package parser.node;

public class AddExpNode extends Node {
    private String name = "<AddExp>";

    private MulExpNode mulExp=null;

    private AddExpNode addExp=null;

    private TerminalNode operator=null;

    public AddExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof MulExpNode) {
            mulExp = (MulExpNode) child;
        } else if (child instanceof AddExpNode) {
            addExp = (AddExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (addExp != null) {
            sb.append(addExp.toString());
            sb.append(operator.toString());
            sb.append(mulExp.toString());
        } else if (mulExp != null) {
            sb.append(mulExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }


}
