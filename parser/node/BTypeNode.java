package parser.node;

import lexer.token.Token;

public class BTypeNode extends Node {
    private String name = "<BType>";
    private Token token;

    public BTypeNode(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        //不需要输出<BType>
        return token.toString() + "\n";
    }


}
