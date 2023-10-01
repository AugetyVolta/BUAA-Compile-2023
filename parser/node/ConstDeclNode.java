package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class ConstDeclNode extends Node {
    private String name = "<ConstDecl>";

    private TerminalNode constTk;

    private BTypeNode bType;

    private ArrayList<ConstDefNode> constDefs = new ArrayList<>();

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    private TerminalNode semicn;

    public ConstDeclNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.CONSTTK) {
                constTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            }
        } else if (child instanceof BTypeNode) {
            bType = (BTypeNode) child;
        } else if (child instanceof ConstDefNode) {
            constDefs.add((ConstDefNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constTk.toString());
        sb.append(bType.toString());
        if (commas.size() == 0) {
            sb.append(constDefs.get(0).toString()); //只有一个constDef
        } else {
            sb.append(constDefs.get(0).toString());
            for (int i = 0; i < commas.size(); i++) {
                sb.append(commas.get(i).toString());
                sb.append(constDefs.get(i + 1).toString());
            }
        }
        sb.append(semicn.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }
}
