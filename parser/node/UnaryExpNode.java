package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import symbol.*;

import java.util.ArrayList;

public class UnaryExpNode extends Node {
    private String name = "<UnaryExp>";

    private PrimaryExpNode primaryExp = null;

    private TerminalNode ident = null;

    private TerminalNode lparent = null;

    private FuncRParamsNode funcRParams = null;

    private TerminalNode rparent = null;

    private UnaryOpNode unaryOp = null;

    private UnaryExpNode unaryExp = null;

    public UnaryExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof PrimaryExpNode) {
            primaryExp = (PrimaryExpNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            }
        } else if (child instanceof FuncRParamsNode) {
            funcRParams = (FuncRParamsNode) child;
        } else if (child instanceof UnaryOpNode) {
            unaryOp = (UnaryOpNode) child;
        } else if (child instanceof UnaryExpNode) {
            unaryExp = (UnaryExpNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (primaryExp != null) {
            sb.append(primaryExp.toString());
        } else if (ident != null) {
            sb.append(ident.toString());
            sb.append(lparent.toString());
            if (funcRParams != null) {
                sb.append(funcRParams.toString());
            }
            sb.append(rparent.toString());
        } else if (unaryOp != null) {
            sb.append(unaryOp.toString());
            sb.append(unaryExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();

        super.checkError(errorList);
        if (ident == null) { //如果ident为空，就不需要检查参数不匹配问题
            return;
        }
        FuncSymbol funcSymbol = (FuncSymbol) symbolTable.getSymbol(ident.getName(), SymbolType.FUNC);
        //check c error
        if (funcSymbol == null) {
            Error error = new Error(ident.getLine(), ErrorType.UNDEFINED_SYMBOL);
            errorList.add(error);
        }
        //check d error 函数形参只能为int类型
        if (funcSymbol != null) {
            int paramNum = funcSymbol.getParamNum(); //定义参数个数
            ArrayList<Integer> dims = funcSymbol.getDims(); //定义时每个参数维度
            if (funcRParams == null) {
                if (paramNum > 0) {
                    Error error = new Error(ident.getLine(), ErrorType.MIS_MATCH_PARAM_NUM);
                    errorList.add(error);
                }
            } else {
                int rParamNum = funcRParams.getExps().size(); //实际的参数个数
                ArrayList<ExpNode> expNodes = funcRParams.getExps(); //填入的参数
                if (rParamNum != paramNum) {
                    Error error = new Error(ident.getLine(), ErrorType.MIS_MATCH_PARAM_NUM);
                    errorList.add(error);
                } else {
                    //check e error
                    for (int i = 0; i < paramNum; i++) {
                        if (dims.get(i) != expNodes.get(i).getDim(symbolTable)) {
                            Error error = new Error(ident.getLine(), ErrorType.MIS_MATCH_PARAM_TYPE);
                            errorList.add(error);
                        }
                    }
                }
            }
        }
    }

    public int getDim(SymbolTable symbolTable) {
        if (primaryExp != null) {
            return primaryExp.getDim(symbolTable);
        } else if (ident != null) { //表明是函数调用,void是-1维
            FuncSymbol funcSymbol = null;
            SymbolTable symbolTable1 = symbolTable;
            while (symbolTable1 != null) {
                if (symbolTable1.hasSymbol(ident.getName()) && symbolTable1.getSymbol(ident.getName()).getSymbolType() == SymbolType.FUNC) {
                    funcSymbol = (FuncSymbol) symbolTable1.getSymbol(ident.getName());
                    break;
                }
                symbolTable1 = symbolTable1.getFatherTable();
            }
            //因为每行最多一个错误，所以一定能找到
            if (funcSymbol.getDataType() == DataType.INT) {
                return 0;
            } else {
                return -1;
            }
        } else { //如果有UnaryOp，那一定是0维
            return 0;
        }
    }

    public int execute() {
        if (primaryExp != null) {
            return primaryExp.execute();
        } else if (unaryOp != null) {
            if (unaryOp.getName().equals("+")) {
                return unaryExp.execute();
            } else if (unaryOp.getName().equals("-")) {
                return -1 * unaryExp.execute();
            }
        }
        //函数调用不是编译时能求值的类型
//      else if (ident != null) {
//
//      }
        return 0;
    }
}
