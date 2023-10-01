package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class InitValNode extends Node {
    private String name = "<InitVal>";

    private ExpNode exp;

    private TerminalNode lbrace = null;

    private TerminalNode rbrace = null;

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    private ArrayList<InitValNode> initVals = new ArrayList<>();


    public InitValNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof ExpNode) {
            exp = (ExpNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.LBRACE) {
                lbrace = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RBRACE) {
                rbrace = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            }
        } else if (child instanceof InitValNode) {
            initVals.add((InitValNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lbrace == null) {
            sb.append(exp.toString());
        } else {
            sb.append(lbrace.toString());
            if (initVals.size() > 0) {
                sb.append(initVals.get(0).toString());
            }
            if (commas.size() > 0) {
                for (int i = 0; i < commas.size(); i++) {
                    sb.append(commas.get(i).toString());
                    sb.append(initVals.get(i + 1).toString());
                }
            }
            sb.append(rbrace.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
