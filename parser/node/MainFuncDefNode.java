package parser.node;

import error.Error;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrFunction;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import symbol.DataType;
import symbol.SymbolManager;
import symbol.SymbolTable;

import java.util.ArrayList;

public class MainFuncDefNode extends Node {
    private String name = "<MainFuncDef>";

    private TerminalNode intTk;

    private TerminalNode mainTk;

    private TerminalNode lparent;

    private TerminalNode rparent;

    private BlockNode block;

    public MainFuncDefNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.INTTK) {
                intTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.MAINTK) {
                mainTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof BlockNode) {
            block = (BlockNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intTk.toString());
        sb.append(mainTk.toString());
        sb.append(lparent.toString());
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        //定义新符号表，开始处理形参和block
        SymbolManager.Manager.enterBlock();
        SymbolTable newSymbolTable = SymbolManager.Manager.getCurSymbolTable();
        newSymbolTable.setNeedReturn(true);
        //处理g错误
        block.checkErrorG(errorList);
        block.checkError(errorList);
        //退出块
        SymbolManager.Manager.leaveBlock();
    }

    @Override
    public IrValue buildIR() {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        IrIntegetType retValueType = IrIntegetType.INT32;
        IrFunction irFunction = IrBuilder.IRBUILDER.buildFunction("main", retValueType);
        //定义新符号表
        SymbolManager.Manager.enterBlock();
        SymbolTable newSymbolTable = SymbolManager.Manager.getCurSymbolTable();
        newSymbolTable.setNeedReturn(true);
        //去build函数中的block
        block.buildIR();
        //退出块
        SymbolManager.Manager.leaveBlock();
        return null;
    }
}
