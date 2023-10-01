package parser.node;

public class DeclNode extends Node {
    private String name = "<Decl>";
    private ConstDeclNode constDecl = null;

    private VarDeclNode varDecl = null;

    public DeclNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof ConstDeclNode) {
            constDecl = (ConstDeclNode) child;
        } else if (child instanceof VarDeclNode) {
            varDecl = (VarDeclNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constDecl != null) {
            sb.append(constDecl.toString());
        } else if (varDecl != null) {
            sb.append(varDecl.toString());
        }
        //不需要输出<Decl>
        return sb.toString();
    }
}
