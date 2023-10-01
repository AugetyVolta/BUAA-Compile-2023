package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class ConstInitValNode extends Node {
    private String name = "<ConstInitVal>";

    private ConstExpNode constExp = null;

    private TerminalNode lbrace = null;

    private TerminalNode rbrace = null;

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    private ArrayList<ConstInitValNode> constInitVals = new ArrayList<>();

    public ConstInitValNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof ConstExpNode) {
            constExp = (ConstExpNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.LBRACE) {
                lbrace = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RBRACE) {
                rbrace = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            }
        } else if (child instanceof ConstInitValNode) {
            constInitVals.add((ConstInitValNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lbrace == null) {
            sb.append(constExp.toString());
        } else {
            sb.append(lbrace.toString());
            if (constInitVals.size() > 0) {
                sb.append(constInitVals.get(0).toString());
            }
            if (commas.size() > 0) {
                for (int i = 0; i < commas.size(); i++) {
                    sb.append(commas.get(i).toString());
                    sb.append(constInitVals.get(i + 1).toString());
                }
            }
            sb.append(rbrace.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
