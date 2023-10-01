package parser.node;

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
}
