package llvm.instr;

import llvm.IrBasicBlock;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;

public class IrAllocaInstr extends IrInstr {
    private IrValueType refType;

    public IrAllocaInstr(String name, IrValueType type) {
        super(name, new IrPointerType(type), IrInstrType.ALLOCA);
        this.refType = type;
    }

    @Override
    public String toString() {
        return String.format("%s = alloca %s", getName(), refType);
    }
}
