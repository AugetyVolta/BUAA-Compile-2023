package parser.node;

import llvm.IrBuilder;
import llvm.IrValue;
import llvm.instr.IrInstrType;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;
import symbol.SymbolTable;

public class MulExpNode extends Node {
    private String name = "<MulExp>";

    private UnaryExpNode unaryExp = null;

    private MulExpNode mulExp = null;

    private TerminalNode operator = null;

    public MulExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof UnaryExpNode) {
            unaryExp = (UnaryExpNode) child;
        } else if (child instanceof MulExpNode) {
            mulExp = (MulExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mulExp != null) {
            sb.append(mulExp.toString());
            sb.append(operator.toString());
            sb.append(unaryExp.toString());
        } else if (unaryExp != null) {
            sb.append(unaryExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    public int getDim(SymbolTable symbolTable) {
        return unaryExp.getDim(symbolTable);
    }

    public int execute() {
        if (mulExp != null) {
            if (operator.getName().equals("*")) {
                return mulExp.execute() * unaryExp.execute();
            } else if (operator.getName().equals("/")) {
                return mulExp.execute() / unaryExp.execute();
            } else if (operator.getName().equals("%")) {
                return mulExp.execute() % unaryExp.execute();
            }
        } else {
            return unaryExp.execute();
        }
        return 0;
    }

    @Override
    public IrValue buildIR() {
        if (mulExp != null) {
            IrValue operand1 = mulExp.buildIR();
            IrValue operand2 = unaryExp.buildIR();
            if (operator.getName().equals("*")) {
                return IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.MUL, operand1, operand2);
            } else if (operator.getName().equals("/")) {
                return IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.SDIV, operand1, operand2);
            } else if (operator.getName().equals("%")) {
                return IrBuilder.IRBUILDER.buildBinaryInstr(IrIntegetType.INT32, IrInstrType.SREM, operand1, operand2);
            } else {
                System.out.println("error in MulExp");
                return null;
            }
        } else {
            return unaryExp.buildIR();
        }
    }

}
