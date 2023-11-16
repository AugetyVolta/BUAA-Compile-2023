package parser.node;

import error.Error;
import llvm.IrValue;
import symbol.SymbolTable;

import java.util.ArrayList;

public abstract class Node {
    public ArrayList<Node> children = new ArrayList<>();

    public void addChild(Node child) {
        children.add(child);
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return null;
    }

    public void checkError(ArrayList<Error> errorList) {
        for (Node child : children) {
            child.checkError(errorList);
        }
    }

    public int execute() {
        return 0;
    }


    public IrValue buildIR() {
        for (Node node : children) {
            node.buildIR();
        }
        return null;
    }
}
