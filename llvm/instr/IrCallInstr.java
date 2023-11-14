package llvm.instr;

import llvm.IrFunction;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

import java.util.ArrayList;

public class IrCallInstr extends IrInstr {
    private int argumentSize;

    public IrCallInstr(String name, IrFunction irFunction, ArrayList<IrValue> arguments) {
        super(name, irFunction.getReturnType(), IrInstrType.CALL);
        this.argumentSize = arguments.size();
        modifyOperand(irFunction, 0);
        //需要构建use-def关系,需要把所有的argument加入到操作数中
        for (int i = 0; i < arguments.size(); i++) {
            modifyOperand(arguments.get(i), i + 1);
        }
    }

    public IrFunction getFunction() {
        return (IrFunction) getOperand(0);
    }

    public IrValue getArgument(int index) { //index从1开始
        return getOperand(index);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getType() != IrIntegetType.VOID) {
            sb.append(getName()).append(" = ");
        }
        sb.append("call").append(" ");
        sb.append(getType()).append(" ");
        sb.append(getFunction().getName()).append("(");
        for (int i = 1; i <= argumentSize; i++) {
            IrValue argument = getArgument(i);
            sb.append(argument.getType()).append(" ").append(argument.getName());
            if (i < argumentSize) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
