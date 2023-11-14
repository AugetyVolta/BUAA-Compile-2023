package parser.node;

import lexer.token.Token;

public class UnaryOpNode extends Node {
    private String name = "<UnaryOp>";

    private Token token;

    public UnaryOpNode(Token token) {
        this.token = token;
    }

    public String getName() {
        return token.getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.toString()).append("\n");
        sb.append(name).append("\n");
        return sb.toString();
    }

}
