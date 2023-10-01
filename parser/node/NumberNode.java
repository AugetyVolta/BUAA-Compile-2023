package parser.node;

import lexer.token.Token;

public class NumberNode extends Node{
    private String name = "<Number>";

    private Token intConst;

    public NumberNode(Token token) {
        this.intConst = token;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intConst.toString()).append("\n");
        sb.append(name).append("\n");
        return sb.toString();
    }


}
