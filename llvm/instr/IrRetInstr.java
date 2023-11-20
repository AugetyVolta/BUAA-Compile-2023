package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrRetInstr extends IrInstr {

    public IrRetInstr(String name, IrValue retValue) {
        super(name, IrValueType.NONE, IrInstrType.RET);
        if (retValue != null) {
            modifyOperand(retValue, 0);
        }
    }

    public IrValue getRetValue() {
        return getOperand(0);
    }

    @Override
    public String toString() {
        IrValue retValue = getRetValue();
        if (retValue == null) {
            return "ret void";
        } else {
            return String.format("ret %s %s", retValue.getType(), retValue.getName());
        }
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        IrValue retValue = getRetValue();
        if (retValue == null) {
            MipsBuilder.MIPSBUILDER.buildJr(31);
        } else {
            if (retValue instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(1, ((IrConstInt) retValue).getValue());
                MipsBuilder.MIPSBUILDER.buildJr(31);
            } else {
                int retValueOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getRetValue());
                MipsBuilder.MIPSBUILDER.buildLw(1, 29, retValueOffset);//将返回值取出,放到v0
                MipsBuilder.MIPSBUILDER.buildJr(31);
            }
        }
    }
}
