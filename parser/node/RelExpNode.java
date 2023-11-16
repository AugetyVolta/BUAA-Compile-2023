package parser.node;

import llvm.IrBuilder;
import llvm.IrValue;
import llvm.instr.IrInstrType;
import llvm.type.IrIntegetType;

public class RelExpNode extends Node {
    private String name = "<RelExp>";

    private RelExpNode relExp = null;
    private AddExpNode addExp = null;
    private TerminalNode operator = null;

    public RelExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof RelExpNode) {
            relExp = (RelExpNode) child;
        } else if (child instanceof AddExpNode) {
            addExp = (AddExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (relExp != null) {
            sb.append(relExp.toString());
            sb.append(operator.toString());
            sb.append(addExp.toString());
        } else if (addExp != null) {
            sb.append(addExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public IrValue buildIR() {
        if (relExp != null) {//RelExp ('<' | '>' | '<=' | '>=') AddExp
            IrValue operand1 = relExp.buildIR();
            if (operand1.getType() == IrIntegetType.INT1) {
                operand1 = IrBuilder.IRBUILDER.buildZextInstr(IrIntegetType.INT32, operand1);
            }
            IrValue operand2 = addExp.buildIR();
            if (operator.getName().equals("<")) {
                return IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.SLT, operand1, operand2);
            } else if (operator.getName().equals(">")) {
                return IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.SGT, operand1, operand2);
            } else if (operator.getName().equals("<=")) {
                return IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.SLE, operand1, operand2);
            } else if (operator.getName().equals(">=")) {
                return IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.SGE, operand1, operand2);
            } else {
                System.out.println("error in RelExp");
                return null;
            }
        } else { //AddExp
            return addExp.buildIR();//不能转换为i1,因为EqExp中的相等判断还需要i32的类型,可能本身就是整数
        }
    }
}
