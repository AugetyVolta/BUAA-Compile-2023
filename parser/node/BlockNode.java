package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.SymbolTable;

import java.util.ArrayList;

public class BlockNode extends Node {
    private String name = "<Block>";

    private TerminalNode lbrace;

    private ArrayList<BlockItemNode> blockItems = new ArrayList<>();

    private TerminalNode rbrace;

    private boolean isLoop = false;

    public BlockNode() {

    }

    public void setIsLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public ArrayList<BlockItemNode> getBlockItems() {
        return blockItems;
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

    public void checkErrorG(ArrayList<Error> errorList) {
        if (blockItems.size() == 0) {
            Error error = new Error(rbrace.getLine(), ErrorType.LACK_OF_RETURN);
            errorList.add(error);
            return;
        }
        BlockItemNode blockItem = blockItems.get(blockItems.size() - 1);
        boolean flag = false;
        StmtNode stmtNode = blockItem.getStmt();
        if (stmtNode != null) {
            StmtEle stmtEle = stmtNode.getStmtEle();
            if (stmtEle instanceof StmtReturn) {
                flag = true;
            }
        }
        if (!flag) {
            Error error = new Error(rbrace.getLine(), ErrorType.LACK_OF_RETURN);
            errorList.add(error);
        }
    }
}
