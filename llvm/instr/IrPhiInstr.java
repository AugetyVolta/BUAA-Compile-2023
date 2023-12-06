package llvm.instr;

import llvm.IrBasicBlock;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrPointerType;

import java.util.ArrayList;

public class IrPhiInstr extends IrInstr {
    private ArrayList<IrBasicBlock> predecessors;

    public IrPhiInstr(String name, ArrayList<IrBasicBlock> predecessors) {
        super(name, IrIntegetType.INT32, IrInstrType.PHI);
        this.predecessors = predecessors;
    }

    public void setOperand(IrBasicBlock pre, IrValue operand) {
        modifyOperand(operand, predecessors.indexOf(pre));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = phi ");
        sb.append(getType().toString());
        for (int i = 0; i < predecessors.size(); i++) {
            sb.append("[ ");
            sb.append(getOperand(i).getName()).append(", ");
            sb.append("%").append(predecessors.get(i).getName());
            sb.append(" ]");
            if (i < predecessors.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
