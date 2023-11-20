package llvm.instr;

import llvm.IrConstInt;
import llvm.IrGlobalVariable;
import llvm.IrValue;
import llvm.type.IrValueType;
import mips.MipsBuilder;
import mips.instr.MipsInstr;

public class IrStoreInstr extends IrInstr {
    public IrStoreInstr(String name, IrValue fromValue, IrValue pointer) {
        super(name, IrValueType.NONE, IrInstrType.STORE);
        modifyOperand(fromValue, 0);//原数
        modifyOperand(pointer, 1);//待存入的地址
    }

    public IrValue getFrom() {
        return getOperand(0);
    }

    public IrValue getTo() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s %s", getFrom().getType(), getFrom().getName(), getTo().getType(), getTo().getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        IrValue fromValue = getFrom();
        int fromOffset;//fromValue的地址
        IrValue toValue = getTo();
        int toOffset;//toValue的地址
        //sw的左操作数,如果是constInt,需要将它li到寄存器中,否则它一定是一个INT32类型,可以从内存中得到
        if (fromValue instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) fromValue).getValue());
        } else {
            fromOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(fromValue);
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, fromOffset); //lw t0,fromOffset(sp)
        }
        //sw的右操作数有可能是全局变量,否则是一个局部变量的地址(其实还是0维的,因为1维2维被使用之前都会有GEP指令)
        if (toValue instanceof IrGlobalVariable) {
            MipsBuilder.MIPSBUILDER.buildLa(9, toValue.getName().substring(1));//设置为全局变量的地址
            //构建store指令
            MipsBuilder.MIPSBUILDER.buildSw(8, 9, 0);
        } else if (toValue instanceof IrGepInstr) {
            //取出Gep传进来的地址
            toOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(toValue);
            MipsBuilder.MIPSBUILDER.buildLw(9, 29, toOffset);
            //构建store指令,存储到9号寄存器所存的地址
            MipsBuilder.MIPSBUILDER.buildSw(8, 9, 0);
        } else {
            toOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(toValue);
            //构建store指令
            MipsBuilder.MIPSBUILDER.buildSw(8, 29, toOffset);
        }
    }
}
