package parser.node;

import lexer.token.SyntaxType;

public class FuncDefNode extends Node {
    private String name = "<FuncDef>";

    private FuncTypeNode funcType;

    private TerminalNode ident;

    private TerminalNode lparent;

    private FuncFParamsNode funcFParams = null;

    private TerminalNode rparent;

    private BlockNode block;

    public FuncDefNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof FuncTypeNode) {
            funcType = (FuncTypeNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof FuncFParamsNode) {
            funcFParams = (FuncFParamsNode) child;
        } else if (child instanceof BlockNode) {
            block = (BlockNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append(lparent.toString());
        if (funcFParams != null) {
            sb.append(funcFParams.toString());
        }
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }
}
