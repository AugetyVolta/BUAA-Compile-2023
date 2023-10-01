package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class VarDeclNode extends Node {
    private String name = "<VarDecl>";

    private BTypeNode bType;

    private ArrayList<VarDefNode> varDefs = new ArrayList<>();

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    private TerminalNode semicn;

    public VarDeclNode() {

    }


    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof BTypeNode) {
            bType = (BTypeNode) child;
        } else if (child instanceof VarDefNode) {
            varDefs.add((VarDefNode) child);
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(varDefs.get(0).toString());
        if (commas.size() > 0) {
            for (int i = 0; i < commas.size(); i++) {
                sb.append(commas.get(0).toString());
                sb.append(varDefs.get(i + 1).toString());
            }
        }
        sb.append(semicn.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

}
