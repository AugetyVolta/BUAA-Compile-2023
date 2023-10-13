package parser.node;

import error.ErrorType;
import error.Error;
import lexer.token.SyntaxType;
import symbol.DataType;
import symbol.FuncSymbol;
import symbol.SymbolTable;
import symbol.SymbolType;

import java.util.ArrayList;

public class FuncDefNode extends Node {
    private String name = "<FuncDef>";

    private FuncTypeNode funcType;

    private TerminalNode ident;

    private TerminalNode lparent;

    private FuncFParamsNode funcFParams = null;

    private TerminalNode rparent;

    private BlockNode block;

    public FuncDefNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof FuncTypeNode) {
            funcType = (FuncTypeNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof FuncFParamsNode) {
            funcFParams = (FuncFParamsNode) child;
        } else if (child instanceof BlockNode) {
            block = (BlockNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append(lparent.toString());
        if (funcFParams != null) {
            sb.append(funcFParams.toString());
        }
        sb.append(rparent.toString());
        sb.append(block.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList, SymbolTable symbolTable) {
        if (symbolTable.hasSymbol(ident.getName())) { //error b
            Error error = new Error(ident.getLine(), ErrorType.REDEFINED_SYMBOL);
            errorList.add(error);
        } else {
            DataType dataType = funcType.getFuncType() == SyntaxType.INTTK ? DataType.INT : DataType.VOID;
            //funcSymbol
            FuncSymbol funcSymbol = new FuncSymbol(SymbolType.FUNC,
                    dataType,
                    ident.getName(),
                    ident.getLine());
            symbolTable.addSymbol(funcSymbol);
            if (funcFParams != null) { //如果函数有参数
                ArrayList<FuncFParamNode> paramNodes = funcFParams.getFuncFParams();
                //为形参设置维度
                for (FuncFParamNode funcFParamNode : paramNodes) {
                    funcSymbol.addParam(funcFParamNode.getDim());
                }
            }
        }
        //定义新符号表，开始处理形参和block
        SymbolTable newSymbolTable = new SymbolTable(symbolTable);
        newSymbolTable.setNeedReturn(funcType.getFuncType() == SyntaxType.INTTK);
        if (funcFParams != null) {
            funcFParams.checkError(errorList, newSymbolTable);
        }
        //处理g错误
        if (funcType.getFuncType() == SyntaxType.INTTK) {
            block.checkErrorG(errorList);
        }
        block.checkError(errorList, newSymbolTable);
    }
}
