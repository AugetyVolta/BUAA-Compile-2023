package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class ConstDefNode extends Node {
    private String name = "<ConstDef>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<ConstExpNode> constExps = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    private TerminalNode assign;

    private ConstInitValNode constInitVal;

    public ConstDefNode() {

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
            } else if (((TerminalNode) child).type == SyntaxType.ASSIGN) {
                assign = (TerminalNode) child;
            }
        } else if (child instanceof ConstExpNode) {
            constExps.add((ConstExpNode) child);
        } else if (child instanceof ConstInitValNode) {
            constInitVal = (ConstInitValNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        for (int i = 0; i < lbracks.size(); i++) {
            sb.append(lbracks.get(i).toString());
            sb.append(constExps.get(i).toString());
            sb.append(rbracks.get(i).toString());
        }
        sb.append(assign.toString());
        sb.append(constInitVal.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

}
