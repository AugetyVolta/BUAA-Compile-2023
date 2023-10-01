package parser.node;

import lexer.token.Token;

public class FuncTypeNode extends Node {
    private String name = "<FuncType>";

    private Token token;

    public FuncTypeNode(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.toString()).append("\n");
        sb.append(name).append("\n");
        return sb.toString();
    }

}
