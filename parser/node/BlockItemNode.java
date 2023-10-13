package parser.node;

public class BlockItemNode extends Node {
    private String name = "<BlockItem>";

    private DeclNode decl = null;

    private StmtNode stmt = null;


    public BlockItemNode() {

    }

    public StmtNode getStmt() {
        return stmt;
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof DeclNode) {
            decl = (DeclNode) child;
        } else if (child instanceof StmtNode) {
            stmt = (StmtNode) child;
        }
    }

    @Override
    public String toString() {
        //不需要输出<BlockItem>
        if (decl != null) {
            return decl.toString();
        }
        if (stmt != null) {
            return stmt.toString();
        }
        return null;
    }

}
