package parser.node;

import lexer.token.SyntaxType;

public class MainFuncDefNode extends Node {
    private String name = "<MainFuncDef>";

    private TerminalNode intTk;

    private TerminalNode mainTk;

    private TerminalNode lparent;

    private TerminalNode rparent;

    private BlockNode block;

    public MainFuncDefNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.INTTK) {
                intTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.MAINTK) {
                mainTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof BlockNode) {
            block = (BlockNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intTk.toString());
        sb.append(mainTk.toString());
        sb.append(lparent.toString());
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }
}
