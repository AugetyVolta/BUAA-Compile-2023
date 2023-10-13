package parser.node;

public class StmtNode extends Node {
    private String name = "<Stmt>";

    private StmtEle stmtEle;

    public StmtNode() {

    }

    public StmtEle getStmtEle() {
        return stmtEle;
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof StmtEle) {
            stmtEle = (StmtEle) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(stmtEle.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }
}
