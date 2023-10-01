package parser.node;

import lexer.token.SyntaxType;
import lexer.token.Token;

public class TerminalNode extends Node {
    private Token token;

    public SyntaxType type;

    public TerminalNode(Token token) {
        this.token = token;
        this.type = token.getSyntaxType();
    }

    @Override
    public String toString() {
        return token.toString() + "\n";
    }
}
