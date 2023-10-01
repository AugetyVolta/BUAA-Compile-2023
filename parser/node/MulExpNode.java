package parser.node;

public class MulExpNode extends Node {
    private String name = "<MulExp>";

    private UnaryExpNode unaryExp = null;

    private MulExpNode mulExp = null;

    private TerminalNode operator=null;

    public MulExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof UnaryExpNode) {
            unaryExp = (UnaryExpNode) child;
        } else if (child instanceof MulExpNode) {
            mulExp = (MulExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mulExp != null) {
            sb.append(mulExp.toString());
            sb.append(operator.toString());
            sb.append(unaryExp.toString());
        } else if (unaryExp != null) {
            sb.append(unaryExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
