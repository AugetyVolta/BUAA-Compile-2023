package parser.node;

import llvm.IrBuilder;
import llvm.IrValue;
import llvm.instr.IrInstrType;
import llvm.instr.IrRetInstr;
import llvm.type.IrIntegetType;

public class EqExpNode extends Node {
    private String name = "<EqExp>";

    private RelExpNode relExp = null;

    private TerminalNode operator = null;

    private EqExpNode eqExp = null;

    public EqExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof RelExpNode) {
            relExp = (RelExpNode) child;
        } else if (child instanceof EqExpNode) {
            eqExp = (EqExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (eqExp != null) {
            sb.append(eqExp.toString());
            sb.append(operator.toString());
            sb.append(relExp.toString());
        } else if (relExp != null) {
            sb.append(relExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    @Override
    public IrValue buildIR() {
        if (eqExp != null) {
            IrValue operand1 = eqExp.buildIR();
            if (operand1.getType() == IrIntegetType.INT1) {
                operand1 = IrBuilder.IRBUILDER.buildZextInstr(IrIntegetType.INT32, operand1);
            }
            IrValue operand2 = relExp.buildIR();
            if (operand2.getType() == IrIntegetType.INT1) {
                operand2 = IrBuilder.IRBUILDER.buildZextInstr(IrIntegetType.INT32, operand2);
            }
            if (operator.getName().equals("==")) {
                return IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.EQ, operand1, operand2);
            } else if (operator.getName().equals("!=")) {
                return IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.NE, operand1, operand2);
            } else {
                System.out.println("error in EqExp");
                return null;
            }
        } else {
            return relExp.buildIR(); //不能转为i1,因为EqExp ('==' | '!=') RelExp还是可能用到i32之间的比较
        }
    }
}
