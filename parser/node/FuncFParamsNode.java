package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class FuncFParamsNode extends Node {
    private String name = "<FuncFParams>";

    private ArrayList<FuncFParamNode> funcFParams = new ArrayList<>();

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    public FuncFParamsNode() {

    }

    public ArrayList<FuncFParamNode> getFuncFParams() {
        return funcFParams;
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof FuncFParamNode) {
            funcFParams.add((FuncFParamNode) child);
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcFParams.get(0).toString());
        if (commas.size() > 0) {
            for (int i = 0; i < commas.size(); i++) {
                sb.append(commas.get(i).toString());
                sb.append(funcFParams.get(i + 1).toString());
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
