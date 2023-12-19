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
        int reg;
        if (MipsBuilder.MIPSBUILDER.hasAllocReg(this)) {
            reg = MipsBuilder.MIPSBUILDER.getReg(this);
        } else {
            reg = MipsBuilder.MIPSBUILDER.allocReg(this);
        }
        //如果从全局变量中load,其实还是0维的,因为1维2维被使用之前都会有GEP指令
        if (getPointer() instanceof IrGlobalVariable) {
            //获取全局变量的地址
            MipsBuilder.MIPSBUILDER.buildLa(reg, getPointer().getName().substring(1));
            //获取全局变量地址中的值
            MipsBuilder.MIPSBUILDER.buildLw(reg, reg, 0);
        } else if (getPointer() instanceof IrGepInstr) {
            if(MipsBuilder.MIPSBUILDER.hasAllocReg(getPointer())){
                MipsBuilder.MIPSBUILDER.buildLw(reg, MipsBuilder.MIPSBUILDER.getReg(getPointer()), 0);
            }
            else{
                //取出所存的地址
                srcOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getPointer());
                MipsBuilder.MIPSBUILDER.buildLw(reg, 29, srcOffset);
                //取出地址中存的值
                MipsBuilder.MIPSBUILDER.buildLw(reg, reg, 0);
            }
        } else {
            //src相对于sp的offset
            srcOffset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getPointer());
            //取出src里面的值
            MipsBuilder.MIPSBUILDER.buildLw(reg, 29, srcOffset);
        }
        //构建新的变量
        MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
    }
}
