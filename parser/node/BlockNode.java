package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class BlockNode extends Node {
    private String name = "<Block>";

    private TerminalNode lbrace;

    private ArrayList<BlockItemNode> blockItems = new ArrayList<>();

    private TerminalNode rbrace;

    public BlockNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.LBRACE) {
                lbrace = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RBRACE) {
                rbrace = (TerminalNode) child;
            }
        } else if (child instanceof BlockItemNode) {
            blockItems.add((BlockItemNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lbrace.toString());
        for (BlockItemNode item : blockItems) {
            sb.append(item.toString());
        }
        sb.append(rbrace.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }
}
