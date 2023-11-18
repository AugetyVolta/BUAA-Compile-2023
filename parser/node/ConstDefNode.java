package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrConstInt;
import llvm.IrValue;
import llvm.instr.IrAllocaInstr;
import llvm.instr.IrGepInstr;
import llvm.instr.IrGetPutInstr;
import llvm.instr.IrInstr;
import llvm.type.IrArrayType;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;
import symbol.*;

import java.util.ArrayList;

import static utils.MyConfig.onDebug;

public class ConstDefNode extends Node {
    private VarSymbol varSymbol;
    private String name = "<ConstDef>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<ConstExpNode> constExps = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    private TerminalNode assign;

    private ConstInitValNode constInitVal;

    public ConstDefNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IDENFR) {
                ident = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LBRACK) {
                lbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.RBRACK) {
                rbracks.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.ASSIGN) {
                assign = (TerminalNode) child;
            }
        } else if (child instanceof ConstExpNode) {
            constExps.add((ConstExpNode) child);
        } else if (child instanceof ConstInitValNode) {
            constInitVal = (ConstInitValNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        for (int i = 0; i < lbracks.size(); i++) {
            sb.append(lbracks.get(i).toString());
            sb.append(constExps.get(i).toString());
            sb.append(rbracks.get(i).toString());
        }
        sb.append(assign.toString());
        sb.append(constInitVal.toString());
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        super.checkError(errorList);
        if (symbolTable.hasSymbol(ident.getName())) {
            Error error = new Error(ident.getLine(), ErrorType.REDEFINED_SYMBOL);
            errorList.add(error);
        } else {
            VarSymbol varSymbol = new VarSymbol(SymbolType.VAR,
                    DataType.INT,
                    ident.getName(),
                    ident.getLine(),
                    true,
                    lbracks.size());
            symbolTable.addSymbol(varSymbol);
            this.varSymbol = varSymbol;
            int dim = lbracks.size();
            if (dim == 0) { //常量
                varSymbol.setInitVal(constInitVal.execute());
            } else if (dim == 1) { //一维数组
                varSymbol.setSize(0, constExps.get(0).execute()); //设置数组的大小
                varSymbol.setArrayInitVal(constInitVal.executeArrayEle());
            } else if (dim == 2) { //二维数组
                varSymbol.setSize(constExps.get(0).execute(), constExps.get(1).execute()); //设置数组的大小
                varSymbol.setArrayInitVal(constInitVal.executeArrayEle());
            }

            if (onDebug) {
                StringBuilder sb = new StringBuilder();
                sb.append(ident.getName()).append(" ");
                sb.append(SymbolManager.Manager.isGlobal()).append(" ");
                sb.append(lbracks.size()).append(" ");
                sb.append("size").append("[" + varSymbol.getSize()[0] + "]").append("[" + varSymbol.getSize()[1] + "]").append("\n");
                if (dim == 0)
                    sb.append(varSymbol.getInitVal()).append("\n");
                else
                    sb.append(varSymbol.getArrayInitVal()).append("\n");
                System.out.print(sb.toString());
            }
        }
    }

    @Override
    public IrValue buildIR() {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        symbolTable.addSymbol(varSymbol);
        if (SymbolManager.Manager.isGlobal()) { //如果是全局变量
            ArrayList<IrConstInt> initValues = varSymbol.getInitValues();//获取初始值
            if (varSymbol.getDim() == 0) {//常数,一定被初始化,只有第一个值被初始化
                IrValue i32_pointer = IrBuilder.IRBUILDER.buildGlobalVariable(IrIntegetType.INT32, 1, true, initValues);
                varSymbol.setLlvmValue(i32_pointer);
            } else {//一维数组或者二维数组,一定被初始化
                IrArrayType irArrayType = new IrArrayType(varSymbol.getLength(), IrIntegetType.INT32);
                IrValue array_pointer = IrBuilder.IRBUILDER.buildGlobalVariable(irArrayType, varSymbol.getLength(), true, initValues);
                varSymbol.setLlvmValue(array_pointer);
            }
        } else { //如果不是全局变量,使用alloca函数
            ArrayList<IrConstInt> initValues = varSymbol.getInitValues();//获取初始值
            if (varSymbol.getDim() == 0) {//常数,一定被初始化,只有第一个值被初始化
                IrAllocaInstr i32_pointer = IrBuilder.IRBUILDER.buildAllocaInstr(IrIntegetType.INT32);
                varSymbol.setLlvmValue(i32_pointer);//被alloca出的指针就是当前的变量
                IrBuilder.IRBUILDER.buildStoreInstr(initValues.get(0), i32_pointer);
            } else {//一维数组或者二维数组,一定被初始化
                IrArrayType irArrayType = new IrArrayType(varSymbol.getLength(), IrIntegetType.INT32);
                IrAllocaInstr array_pointer = IrBuilder.IRBUILDER.buildAllocaInstr(irArrayType);
                varSymbol.setLlvmValue(array_pointer);//被alloca出的指针就是当前的变量
                for (int i = 0; i < varSymbol.getLength(); i++) {
                    IrGepInstr elemPointer = IrBuilder.IRBUILDER.buildGepInstr(array_pointer, new IrConstInt(i));//获取每一个数组元素指针
                    IrBuilder.IRBUILDER.buildStoreInstr(initValues.get(i), elemPointer);//构建store指令
                }
            }
        }
        return null;
    }
}
