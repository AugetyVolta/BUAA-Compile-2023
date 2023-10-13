package parser.node;

import lexer.token.SyntaxType;
import lexer.token.Token;
import symbol.SymbolType;

public class FuncTypeNode extends Node {
    private String name = "<FuncType>";

    private Token token;

    public FuncTypeNode(Token token) {
        this.token = token;
    }

    public SyntaxType getFuncType() {
        return token.getSyntaxType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.toString()).append("\n");
        sb.append(name).append("\n");
        return sb.toString();
    }

}
