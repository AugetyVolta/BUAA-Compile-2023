package llvm.instr;

import llvm.IrBasicBlock;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrBrInstr extends IrInstr {
    private boolean isCond;//是否是条件跳转

    //条件跳转
    public IrBrInstr(String name, IrValue cond, IrBasicBlock basicBlock1, IrBasicBlock basicBlock2) {
        super(name, IrValueType.NONE, IrInstrType.BR);
        modifyOperand(cond, 0);
        modifyOperand(basicBlock1, 1);
        modifyOperand(basicBlock2, 2);
        this.isCond = true;
    }

    //无条件跳转
    public IrBrInstr(String name, IrBasicBlock basicBlock) {
        super(name, IrValueType.NONE, IrInstrType.BR);
        modifyOperand(basicBlock, 0);
        this.isCond = false;
    }

    public boolean isCond() {
        return isCond;
    }

    public IrValue getCond() {
        if (isCond) {
            return getOperand(0);
        } else {
            return null;
        }
    }

    public IrValue getLabel(int index) {
        if (isCond) {
            return getOperand(index);
        } else {
            return getOperand(0);
        }
    }

    @Override
    public String toString() {
        if (isCond) {
            return String.format("br i1 %s, label %%%s, label %%%s", getCond().getName(), getLabel(1).getName(), getLabel(2).getName());
        } else {
            return String.format("br label %%%s", getLabel(0).getName());
        }
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        MipsBuilder.MIPSBUILDER.writeBackAll();
        if (isCond) {
            if (!MipsBuilder.MIPSBUILDER.hasAllocReg(getCond())) {
                int condOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getCond());
                MipsBuilder.MIPSBUILDER.buildLw(26, 29, condOffset);
                //true
                MipsBuilder.MIPSBUILDER.buildBne(26, 0, getLabel(1).getName());
            } else {
                int reg = MipsBuilder.MIPSBUILDER.getReg(getCond());
                //true
                MipsBuilder.MIPSBUILDER.buildBne(reg, 0, getLabel(1).getName());
            }
            //false
            MipsBuilder.MIPSBUILDER.buildJ(getLabel(2).getName());
        } else {
            MipsBuilder.MIPSBUILDER.buildJ(getLabel(0).getName());
        }
    }
}
