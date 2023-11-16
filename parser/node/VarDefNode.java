package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrConstInt;
import llvm.IrValue;
import llvm.instr.IrAllocaInstr;
import llvm.instr.IrGepInstr;
import llvm.type.IrArrayType;
import llvm.type.IrIntegetType;
import symbol.*;

import java.util.ArrayList;

import static utils.MyConfig.onDebug;

public class VarDefNode extends Node {
    private VarSymbol varSymbol;
    private String name = "<VarDef>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    private ArrayList<ConstExpNode> constExps = new ArrayList<>();

    private TerminalNode assign = null;

    private InitValNode initVal = null;

    public VarDefNode() {

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
        } else if (child instanceof InitValNode) {
            initVal = (InitValNode) child;
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
        if (assign != null) {
            sb.append(assign.toString());
            sb.append(initVal.toString());
        }
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
                    false,
                    lbracks.size());
            symbolTable.addSymbol(varSymbol);
            this.varSymbol = varSymbol;
            int dim = lbracks.size();
            //不论什么时候必须要保存数组的大小
            if (dim == 1) {
                varSymbol.setSize(0, constExps.get(0).execute());
            } else if (dim == 2) {
                varSymbol.setSize(constExps.get(0).execute(), constExps.get(1).execute());
            }
            //对于变量是全局并且附了初值才需要算它初始值,不然它的初始中有可能有变量
            if (SymbolManager.Manager.isGlobal() && initVal != null) {
                if (dim == 0) { //常量
                    varSymbol.setInitVal(initVal.execute());
                } else if (dim == 1) { //一维数组
                    varSymbol.setArrayInitVal(initVal.executeArrayEle());
                } else if (dim == 2) { //二维数组
                    varSymbol.setArrayInitVal(initVal.executeArrayEle());
                }
            }

            if (onDebug) {
                //ident isGlobal dim size[x][y] 内容
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
        if (SymbolManager.Manager.isGlobal()) { //对于全局变量(非常量)的定义,如果有初始值,也是可求值的,没有初始值,就会赋值0
            ArrayList<IrConstInt> initValues = initVal != null ? varSymbol.getInitValues() : null;//获取初始值
            if (varSymbol.getDim() == 0) {//如果initVal != null,就一定被初始化
                IrValue i32_pointer = IrBuilder.IRBUILDER.buildGlobalVariable(IrIntegetType.INT32, false, initValues);
                varSymbol.setLlvmValue(i32_pointer);
            } else {//一维数组或者二维数组,//如果initVal != null,就一定被初始化
                IrArrayType irArrayType = new IrArrayType(varSymbol.getLength(), IrIntegetType.INT32);
                IrValue array_pointer = IrBuilder.IRBUILDER.buildGlobalVariable(irArrayType, false, initValues);
                varSymbol.setLlvmValue(array_pointer);
            }
        } else {
            if (varSymbol.getDim() == 0) { //局部变量
                IrAllocaInstr i32_pointer = IrBuilder.IRBUILDER.buildAllocaInstr(IrIntegetType.INT32);//alloca
                varSymbol.setLlvmValue(i32_pointer);//被alloca出的指针就是当前的变量
                if (initVal != null) {
                    ArrayList<IrValue> inits = initVal.getInits();
                    IrBuilder.IRBUILDER.buildStoreInstr(inits.get(0), i32_pointer);
                }
            } else { //局部一维数组或二维数组
                IrArrayType irArrayType = new IrArrayType(varSymbol.getLength(), IrIntegetType.INT32);//alloca
                IrAllocaInstr array_pointer = IrBuilder.IRBUILDER.buildAllocaInstr(irArrayType);
                varSymbol.setLlvmValue(array_pointer);
                if (initVal != null) {
                    ArrayList<IrValue> inits = initVal.getInits();
                    for (int i = 0; i < inits.size(); i++) {
                        IrGepInstr elemPointer = IrBuilder.IRBUILDER.buildGepInstr(array_pointer, new IrConstInt(i));//获取每一个数组元素指针
                        IrBuilder.IRBUILDER.buildStoreInstr(inits.get(i), elemPointer);
                    }
                }
            }
        }
        return null;
    }
}
