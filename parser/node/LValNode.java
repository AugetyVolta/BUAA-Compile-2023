package parser.node;

import error.Error;
import error.ErrorType;
import lexer.token.SyntaxType;
import llvm.IrBuilder;
import llvm.IrConstInt;
import llvm.IrValue;
import llvm.instr.IrInstrType;
import llvm.type.IrIntegetType;
import symbol.*;

import java.util.ArrayList;

public class LValNode extends Node {
    private String name = "<LVal>";

    private TerminalNode ident;

    private ArrayList<TerminalNode> lbracks = new ArrayList<>();

    private ArrayList<ExpNode> exps = new ArrayList<>();

    private ArrayList<TerminalNode> rbracks = new ArrayList<>();

    public LValNode() {

    }

    public String getName() {
        return ident.getName();
    }

    public int getLine() {
        return ident.getLine();
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
            }
        } else if (child instanceof ExpNode) {
            exps.add((ExpNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (lbracks.size() != 0) {
            for (int i = 0; i < lbracks.size(); i++) {
                sb.append(lbracks.get(i).toString());
                sb.append(exps.get(i).toString());
                sb.append(rbracks.get(i).toString());
            }
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        super.checkError(errorList);
        String name = ident.getName();
        //没找到
        if (symbolTable.getSymbol(name, SymbolType.VAR) == null) {
            Error error = new Error(ident.getLine(), ErrorType.UNDEFINED_SYMBOL);
            errorList.add(error);
        }
    }

    public int getDim(SymbolTable symbolTable) {
        VarSymbol varSymbol = null;
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1 != null) {
            if (symbolTable1.hasSymbol(ident.getName()) && symbolTable1.getSymbol(ident.getName()).getSymbolType() == SymbolType.VAR) {
                varSymbol = (VarSymbol) symbolTable1.getSymbol(ident.getName());
                break;
            }
            symbolTable1 = symbolTable1.getFatherTable();
        }
        return varSymbol.getDim() - lbracks.size();
    }

    @Override
    public int execute() {
        VarSymbol valSymbol = (VarSymbol) SymbolManager.Manager.getCurSymbolTable().getSymbol(ident.getName(), SymbolType.VAR);
        if (valSymbol == null) {
            return 0;
        }
        //用于获取引用的下标
        ArrayList<Integer> lens = new ArrayList<>();
        for (ExpNode expNode : exps) {
            lens.add(expNode.execute());
        }
        if (valSymbol.getDim() == 0) {//常数
            return valSymbol.getInitVal();
        } else if (valSymbol.getDim() == 1) {//一维数组
            return valSymbol.getInitVal(lens.get(0));
        } else if (valSymbol.getDim() == 2) {//二维数组
            return valSymbol.getInitVal(lens.get(0), lens.get(1));
        }
        return 0;
    }

    public IrValue getLValPointer() {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        VarSymbol lValsymbol = (VarSymbol) symbolTable.getSymbol(getName());
        IrValue lValPointer = lValsymbol.getLlvmValue();
        int dim = lValsymbol.getDim();//变量被定义时的维度
        if (dim == 0) {
            return lValPointer;
        } else if (dim == 1) {
            IrValue index0 = exps.get(0).buildIR();//数组下标
            return IrBuilder.IRBUILDER.buildGepInstr(lValPointer, index0);//下标相应位置指针
        } else {
            int[] size = lValsymbol.getSize();
            IrValue index0 = exps.get(0).buildIR();
            IrValue index1 = exps.get(1).buildIR();
            IrValue mulInstr = IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.MUL, index0, new IrConstInt(size[1]));
            IrValue addInstr = IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.ADD, mulInstr, index1);
            return IrBuilder.IRBUILDER.buildGepInstr(lValPointer, addInstr);//下标相应位置指针
        }
    }

    public IrValue getLValValue() {
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
        VarSymbol lValsymbol = (VarSymbol) symbolTable.getSymbol(getName());
        IrValue lValPointer = lValsymbol.getLlvmValue();
        int dim = lValsymbol.getDim();//变量定义时的维度
        if (dim == 0) {
            return IrBuilder.IRBUILDER.buildLoadInstr(lValPointer);
        } else if (dim == 1) {//变量定义时的维度维1
            if (getDim(symbolTable) == 0) {//被调用时的维度是0,即定义数组a[],调用a[x]
                IrValue index0 = exps.get(0).buildIR();//数组下标
                IrValue gepInstr = IrBuilder.IRBUILDER.buildGepInstr(lValPointer, index0);//下标相应位置指针
                return IrBuilder.IRBUILDER.buildLoadInstr(gepInstr);
            } else {//被调用时的维度是1,即定义数组a[],调用a
                return IrBuilder.IRBUILDER.buildGepInstr(lValPointer, new IrConstInt(0));
            }
        } else {
            int[] size = lValsymbol.getSize();
            if (getDim(symbolTable) == 0) {//被调用时的维度是0,即定义数组a[][],调用a[x][y]
                IrValue index0 = exps.get(0).buildIR();
                IrValue index1 = exps.get(1).buildIR();
                IrValue mulInstr = IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.MUL, index0, new IrConstInt(size[1]));
                IrValue addInstr = IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.ADD, mulInstr, index1);
                IrValue gepInstr = IrBuilder.IRBUILDER.buildGepInstr(lValPointer, addInstr);//下标相应位置指针
                return IrBuilder.IRBUILDER.buildLoadInstr(gepInstr);
            } else if (getDim(symbolTable) == 1) {//被调用时的维度是1,即定义数组a[][],调用a[x]
                IrValue index0 = exps.get(0).buildIR();
                IrValue mulInstr = IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.MUL, index0, new IrConstInt(size[1]));
                return IrBuilder.IRBUILDER.buildGepInstr(lValPointer, mulInstr);//下标相应位置指针
            } else {//被调用时的维度是2,即定义数组a[][],调用a
                return IrBuilder.IRBUILDER.buildGepInstr(lValPointer, new IrConstInt(0));
            }
        }
    }

}
