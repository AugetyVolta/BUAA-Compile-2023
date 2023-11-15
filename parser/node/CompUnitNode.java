package parser.node;

import llvm.IrValue;
import symbol.SymbolManager;

import java.util.ArrayList;

public class CompUnitNode extends Node {
    private String name = "<CompUnit>";
    private ArrayList<DeclNode> decls = new ArrayList<>();
    private ArrayList<FuncDefNode> funcDefs = new ArrayList<>();
    private MainFuncDefNode mainFuncDef;

    public CompUnitNode() {
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof DeclNode) {
            decls.add((DeclNode) child);
        } else if (child instanceof FuncDefNode) {
            funcDefs.add((FuncDefNode) child);
        } else if (child instanceof MainFuncDefNode) {
            mainFuncDef = (MainFuncDefNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (DeclNode declNode : decls) {
            sb.append(declNode.toString());
        }
        for (FuncDefNode funcDefNode : funcDefs) {
            sb.append(funcDefNode.toString());
        }
        sb.append(mainFuncDef.toString());
        sb.append(this.name).append("\n");
        return sb.toString();
    }


    @Override
    public IrValue genIR() {
        SymbolManager.Manager.resetSymbolTable();
        super.genIR();
        return null;
    }
}
