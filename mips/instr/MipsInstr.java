package mips.instr;

import llvm.instr.IrInstr;
import mips.MipsLabel;
import mips.MipsValue;

public class MipsInstr extends MipsValue {
    private final String name;

    public MipsInstr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
