package llvm.instr;

import llvm.IrConstInt;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrMoveInstr extends IrInstr {

    public IrMoveInstr(String name, IrValue dst, IrValue src) {
        super(name, IrValueType.NONE, IrInstrType.MOVE);
        modifyOperand(dst, 0);
        modifyOperand(src, 1);
    }

    @Override
    public String toString() {
        return String.format("move %s %s,%s %s", getOperand(0).getType(), getOperand(0).getName(), getOperand(1).getType(), getOperand(1).getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        //看看move的对象是否有定义
        int dstIndex;
        if (MipsBuilder.MIPSBUILDER.hasSymbol(getOperand(0))) {
            dstIndex = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand(0));
        } else {
            dstIndex = MipsBuilder.MIPSBUILDER.buildVarSymbol(getOperand(0));
        }
        if (getOperand(1) instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) getOperand(1)).getValue());
            //构建store进dst
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, dstIndex);
        } else {
            int srcIndex = MipsBuilder.MIPSBUILDER.getSymbolOffset(getOperand(1));
            //构建一个load取出src中的值
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, srcIndex);
            //构建store进dst
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, dstIndex);
        }
    }
}
