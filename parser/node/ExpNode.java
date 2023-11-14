package parser.node;

import symbol.SymbolTable;

public class ExpNode extends Node {
    private String name = "<Exp>";

    private AddExpNode addExp;

    public ExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof AddExpNode) {
            addExp = (AddExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

    public int getDim(SymbolTable symbolTable) {
        return addExp.getDim(symbolTable);
    }

    public int execute() {
        return addExp.execute();
    }
}
