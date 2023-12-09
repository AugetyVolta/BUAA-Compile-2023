package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;
import mips.MipsBuilder;
import mips.MipsModule;

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
            case PUTSTR:
                sb.append("call void @putstr(");
                sb.append(getOperand().getType()).append(" ").append(getOperand().getName());
                sb.append(")");
                break;
            default:
        }
        return sb.toString();
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        int reg;
        switch (getIrInstrType()) {
            case GETINT:
                MipsBuilder.MIPSBUILDER.buildLi(1, 5);//v0
                MipsBuilder.MIPSBUILDER.buildSyscall();
                MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
                reg = MipsBuilder.MIPSBUILDER.allocReg(this);
                MipsBuilder.MIPSBUILDER.buildMove(reg, 1);
                break;
            case PUTINT:
                if (getOperand() instanceof IrConstInt) {
                    MipsBuilder.MIPSBUILDER.buildLi(4, ((IrConstInt) getOperand()).getValue());
                } else {
                    if (MipsBuilder.MIPSBUILDER.hasAllocReg(getOperand())) {
                        reg = MipsBuilder.MIPSBUILDER.getReg(getOperand());
                        MipsBuilder.MIPSBUILDER.buildMove(4, reg);
                    } else {
                        //将值lw到a0中
                        int operandOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand());
                        MipsBuilder.MIPSBUILDER.buildLw(4, 29, operandOffset);
                    }
                }
                MipsBuilder.MIPSBUILDER.buildLi(1, 1);//v0
                MipsBuilder.MIPSBUILDER.buildSyscall();
                break;
            case PUTCH:
                MipsBuilder.MIPSBUILDER.buildLi(4, ((IrConstInt) getOperand()).getValue());
                MipsBuilder.MIPSBUILDER.buildLi(1, 11);//v0
                MipsBuilder.MIPSBUILDER.buildSyscall();
                break;
            case PUTSTR:
                MipsBuilder.MIPSBUILDER.buildLa(4, ((IrGepInstr) getOperand()).getPointer().getName().substring(1));
                MipsBuilder.MIPSBUILDER.buildLi(1, 4);//v0
                MipsBuilder.MIPSBUILDER.buildSyscall();
                break;
            default:
        }
    }
}
