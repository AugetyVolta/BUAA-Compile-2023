package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrPointerType;
import symbol.*;

import java.util.ArrayList;

public class FuncFParamNode extends Node {
    private String name = "<FuncFParam>";

    private BTypeNode bType;

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    private ArrayList<ConstExpNode> constExps = new ArrayList<>();

    private VarSymbol varSymbol;

    public FuncFParamNode() {

    }

    public int getDim() {
        return lbracks.size();
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof BTypeNode) {
            bType = (BTypeNode) child;
        } else if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LBRACK) {
                lbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.RBRACK) {
                rbracks.add((TerminalNode) child);
            }
        } else if (child instanceof ConstExpNode) {
            constExps.add((ConstExpNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if (lbracks.size() != 0) {
            sb.append(lbracks.get(0).toString());
            sb.append(rbracks.get(0).toString());
            if (constExps.size() != 0) {
                for (int i = 0; i < constExps.size(); i++) {
                    sb.append(lbracks.get(i + 1).toString());
                    sb.append(constExps.get(i).toString());
                    sb.append(rbracks.get(i + 1).toString());
                }
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        super.checkError(errorList);
        if (symbolTable.hasSymbol(ident.getName())) { //error b
            Error error = new Error(ident.getLine(), ErrorType.REDEFINED_SYMBOL);
            errorList.add(error);
        } else {
            VarSymbol varSymbol = new VarSymbol(SymbolType.VAR,
                    DataType.INT,
                    ident.getName(),
                    ident.getLine(),
                    false,
                    lbracks.size());
            symbolTable.addSymbol(varSymbol);
            this.varSymbol = varSymbol;
            int dim = lbracks.size();
            if (dim == 2) { //二维数组
                varSymbol.setSize(0, constExps.get(0).execute()); //设置数组的大小
            }
        }
    }

    @Override
    public IrValue buildIR() {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        symbolTable.addSymbol(varSymbol);
        if (varSymbol.getDim() == 0) {
            IrValue param = IrBuilder.IRBUILDER.buildParam(IrIntegetType.INT32);
            IrValue allocaInstr = IrBuilder.IRBUILDER.buildAllocaInstr(IrIntegetType.INT32); //首先alloca变量,然后将传入的值存入指针中
            IrBuilder.IRBUILDER.buildStoreInstr(param, allocaInstr);
            varSymbol.setLlvmValue(allocaInstr);
            return param;
        } else {
            IrValue param = IrBuilder.IRBUILDER.buildParam(new IrPointerType(IrIntegetType.INT32));
            varSymbol.setLlvmValue(param);
            return param;
        }
    }
}
