package parser.node;

import lexer.token.Token;
import llvm.IrConstInt;
import llvm.IrValue;

public class NumberNode extends Node {
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


    public int execute() {
        return Integer.parseInt(intConst.getValue());
    }

    @Override
    public IrValue buildIR() {
        return new IrConstInt(Integer.parseInt(intConst.getValue()));
    }


}
