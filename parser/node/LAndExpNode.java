package parser.node;

import javax.swing.*;

public class LAndExpNode extends Node {
    private String name = "<LAndExp>";

    private EqExpNode eqExp = null;

    private LAndExpNode lAndExp = null;

    private TerminalNode operator = null;

    public LAndExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof EqExpNode) {
            eqExp = (EqExpNode) child;
        } else if (child instanceof LAndExpNode) {
            lAndExp = (LAndExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lAndExp != null) {
            sb.append(lAndExp.toString());
            sb.append(operator.toString());
            sb.append(eqExp.toString());
        } else if (eqExp != null) {
            sb.append(eqExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
