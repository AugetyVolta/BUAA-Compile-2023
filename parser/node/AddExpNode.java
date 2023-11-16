package parser.node;

import llvm.IrBuilder;
import llvm.IrValue;
import llvm.instr.IrInstrType;
import llvm.type.IrIntegetType;
import symbol.SymbolTable;

import java.util.Objects;

public class AddExpNode extends Node {
    private String name = "<AddExp>";

    private MulExpNode mulExp = null;

    private AddExpNode addExp = null;

    private TerminalNode operator = null;

    public AddExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof MulExpNode) {
            mulExp = (MulExpNode) child;
        } else if (child instanceof AddExpNode) {
            addExp = (AddExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (addExp != null) {
            sb.append(addExp.toString());
            sb.append(operator.toString());
            sb.append(mulExp.toString());
        } else if (mulExp != null) {
            sb.append(mulExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    public int getDim(SymbolTable symbolTable) {
        return mulExp.getDim(symbolTable);
    }

    public int execute() {
        if (addExp != null) {
            if (operator.getName().equals("+")) {
                return addExp.execute() + mulExp.execute();
            } else if (operator.getName().equals("-")) {
                return addExp.execute() - mulExp.execute();
            }
        } else {
            return mulExp.execute();
        }
        return 0;
    }

    @Override
    public IrValue buildIR() {
        if (addExp != null) { //AddExp ('+' | '−') MulExp
            IrValue addExpValue = addExp.buildIR();
            IrValue mulExpValue = mulExp.buildIR();
            if (operator.getName().equals("+")) {
                return IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.ADD, addExpValue, mulExpValue);
            } else if (operator.getName().equals("-")) {
                return IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.SUB, addExpValue, mulExpValue);
            } else {
                System.out.println("error in AddExp");
                return null;
            }
        } else {//MulExp
            return mulExp.buildIR();
        }
    }


}
