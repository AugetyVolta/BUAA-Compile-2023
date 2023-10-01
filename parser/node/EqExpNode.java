package parser.node;

public class EqExpNode extends Node {
    private String name = "<EqExp>";

    private RelExpNode relExp=null;

    private TerminalNode operator=null;

    private EqExpNode eqExp=null;

    public EqExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof RelExpNode) {
            relExp = (RelExpNode) child;
        } else if (child instanceof EqExpNode) {
            eqExp = (EqExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (eqExp != null) {
            sb.append(eqExp.toString());
            sb.append(operator.toString());
            sb.append(relExp.toString());
        } else if (relExp != null) {
            sb.append(relExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
