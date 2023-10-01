package parser.node;

public class StmtBlock extends StmtEle {
    private BlockNode block;

    public StmtBlock() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof BlockNode) {
            block = (BlockNode) child;
        }
    }

    @Override
    public String toString() {
        return block.toString();
    }
}
