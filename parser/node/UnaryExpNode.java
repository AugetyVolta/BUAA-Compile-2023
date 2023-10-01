package parser.node;

import lexer.token.SyntaxType;

public class UnaryExpNode extends Node {
    private String name = "<UnaryExp>";

    private PrimaryExpNode primaryExp = null;

    private TerminalNode ident = null;

    private TerminalNode lparent = null;

    private FuncRParamsNode funcRParams = null;

    private TerminalNode rparent = null;

    private UnaryOpNode unaryOp = null;

    private UnaryExpNode unaryExp = null;

    public UnaryExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof PrimaryExpNode) {
            primaryExp = (PrimaryExpNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof FuncRParamsNode) {
            funcRParams = (FuncRParamsNode) child;
        } else if (child instanceof UnaryOpNode) {
            unaryOp = (UnaryOpNode) child;
        } else if (child instanceof UnaryExpNode) {
            unaryExp = (UnaryExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (primaryExp != null) {
            sb.append(primaryExp.toString());
        } else if (ident != null) {
            sb.append(ident.toString());
            sb.append(lparent.toString());
            if (funcRParams != null) {
                sb.append(funcRParams.toString());
            }
            sb.append(rparent.toString());
        } else if (unaryOp != null) {
            sb.append(unaryOp.toString());
            sb.append(unaryExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }
}
