package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class LValNode extends Node {
    private String name = "<LVal>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<ExpNode> exps = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    public LValNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LBRACK) {
                lbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.RBRACK) {
                rbracks.add((TerminalNode) child);
            }
        } else if (child instanceof ExpNode) {
            exps.add((ExpNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (lbracks.size() != 0) {
            for (int i = 0; i < lbracks.size(); i++) {
                sb.append(lbracks.get(i).toString());
                sb.append(exps.get(i).toString());
                sb.append(rbracks.get(i).toString());
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
