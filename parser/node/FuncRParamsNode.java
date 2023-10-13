package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class FuncRParamsNode extends Node {
    private String name = "<FuncRParams>";

    private ArrayList<ExpNode> exps = new ArrayList<>();

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    public FuncRParamsNode() {

    }

    public ArrayList<ExpNode> getExps() {
        return exps;
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof ExpNode) {
            exps.add((ExpNode) child);
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(exps.get(0).toString());
        if (commas.size() > 0) {
            for (int i = 0; i < commas.size(); i++) {
                sb.append(commas.get(i).toString());
                sb.append(exps.get(i + 1).toString());
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
