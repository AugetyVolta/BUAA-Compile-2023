package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class FuncFParamNode extends Node {
    private String name = "<FuncFParam>";

    private BTypeNode bType;

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    private ArrayList<ConstExpNode> constExps = new ArrayList<>();

    public FuncFParamNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof BTypeNode) {
            bType = (BTypeNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LBRACK) {
                lbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.RBRACK) {
                rbracks.add((TerminalNode) child);
            }
        } else if (child instanceof ConstExpNode) {
            constExps.add((ConstExpNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if (lbracks.size() != 0) {
            sb.append(lbracks.get(0).toString());
            sb.append(rbracks.get(0).toString());
            if (constExps.size() != 0) {
                for (int i = 0; i < constExps.size(); i++) {
                    sb.append(lbracks.get(i + 1).toString());
                    sb.append(constExps.get(i).toString());
                    sb.append(rbracks.get(i + 1).toString());
                }
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
