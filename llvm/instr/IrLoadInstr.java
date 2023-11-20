package llvm.instr;

import llvm.IrGlobalVariable;
import llvm.IrValue;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrLoadInstr extends IrInstr {

    //将指针地址存的值存入name所指的变量中
    public IrLoadInstr(String name, IrValue pointer) {
        super(name, ((IrPointerType) pointer.getType()).getRefType(), IrInstrType.LOAD);
        modifyOperand(pointer, 0);
    }

    public IrValue getPointer() {
        return getOperand(0); //获取地址指针
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s", getName(), getType(), getPointer().getType(), getPointer().getName());
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        int srcOffset;
        //如果从全局变量中load,其实还是0维的,因为1维2维被使用之前都会有GEP指令
        if (getPointer() instanceof IrGlobalVariable) {
            //获取全局变量的地址
            MipsBuilder.MIPSBUILDER.buildLa(8, getPointer().getName().substring(1));
            //获取全局变量地址中的值
            MipsBuilder.MIPSBUILDER.buildLw(8, 8, 0);
        } else if (getPointer() instanceof IrGepInstr) {
            //取出所存的地址
            srcOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getPointer());
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, srcOffset);
            //取出地址中存的值
            MipsBuilder.MIPSBUILDER.buildLw(8, 8, 0);
        } else {
            //src相对于sp的offset
            srcOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getPointer());
            //取出src里面的值
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, srcOffset);
        }
        //构建新的变量
        int dstOffset = MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
        //将src中取出的值存进当前的地址
        MipsBuilder.MIPSBUILDER.buildSw(8, 29, dstOffset);
    }
}
