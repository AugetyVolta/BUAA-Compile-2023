package llvm.instr;

import llvm.IrValue;
import llvm.type.IrValueType;

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
}
