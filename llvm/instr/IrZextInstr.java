package llvm.instr;

import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrZextInstr extends IrInstr {

    //被赋值变量，目标类型，原value
    public IrZextInstr(String name, IrValueType irValueType, IrValue srcValue) {
        super(name, irValueType, IrInstrType.ZEXT);
        modifyOperand(srcValue, 0);
    }

    public IrValue getSrcValue() {
        return getOperand(0);
    }

    @Override
    public String toString() {
        return String.format("%s = zext %s %s to %s", getName(), getSrcValue().getType(), getSrcValue().getName(), getType());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        int srcValueOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getSrcValue());
        //只是改变了类型,不用创建新变量,只需要把之前的地址存进去就行
        MipsBuilder.MIPSBUILDER.getMipsSymbolTable().put(this, srcValueOffset);
        //构建寄存器
        if (MipsBuilder.MIPSBUILDER.hasAllocReg(getSrcValue())) {
            int reg1 = MipsBuilder.MIPSBUILDER.allocReg(this);
            int reg2 = MipsBuilder.MIPSBUILDER.getReg(getSrcValue());
            MipsBuilder.MIPSBUILDER.buildMove(reg1, reg2);
        }
        else{
            int reg1 = MipsBuilder.MIPSBUILDER.allocReg(this);
            int reg2 = MipsBuilder.MIPSBUILDER.allocReg(getSrcValue());
            MipsBuilder.MIPSBUILDER.buildMove(reg1, reg2);
        }
    }

    public String hash() {
        return this.toString().substring(0, this.toString().indexOf('='));
    }
}
