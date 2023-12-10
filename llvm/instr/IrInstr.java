package llvm.instr;

import llvm.IrBasicBlock;
import llvm.IrUser;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

public class IrInstr extends IrUser {
    private IrInstrType irInstrType;//指令类型
    private IrBasicBlock basicBlock;//所在基本块
    public boolean needDelete = false;

    //指令返回值变量名,指令返回值类型,指令类型
    public IrInstr(String name, IrValueType type, IrInstrType irInstrType) {
        super(name, type);
        this.irInstrType = irInstrType;
    }

    public IrInstrType getIrInstrType() {
        return irInstrType;
    }

    public void setIrInstrType(IrInstrType irInstrType) {
        this.irInstrType = irInstrType;
    }

    public IrBasicBlock getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(IrBasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }

    @Override
    public String toString() {
        return "";
    }

    public String hash() {
        return null;
    }
}
