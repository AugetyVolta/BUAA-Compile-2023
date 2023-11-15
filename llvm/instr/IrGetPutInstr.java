package llvm.instr;

import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

public class IrGetPutInstr extends IrInstr {

    //getint
    public IrGetPutInstr(String name, IrInstrType irInstrType) {
        super(name, IrIntegetType.INT32, irInstrType);
    }

    //putint putchar
    public IrGetPutInstr(String name, IrInstrType irInstrType, IrValue operand) {
        super(name, IrIntegetType.VOID, irInstrType);
        modifyOperand(operand, 0);
    }

    public IrValue getOperand() {
        return getOperand(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (getIrInstrType()) {
            case GETINT:
                sb.append(getName());
                sb.append(" = call i32 @getint()");
                break;
            case PUTINT:
                sb.append("call void @putint(");
                sb.append(getOperand().getType()).append(" ").append(getOperand().getName());
                sb.append(")");
                break;
            case PUTCH:
                sb.append("call void @putch(");
                sb.append(getOperand().getType()).append(" ").append(getOperand().getName());
                sb.append(")");
                break;
            default:
        }
        return sb.toString();
    }
}
