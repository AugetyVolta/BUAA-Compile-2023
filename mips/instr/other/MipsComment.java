package mips.instr.other;

import llvm.instr.IrInstr;
import mips.instr.MipsInstr;

public class MipsComment extends MipsInstr {
    private IrInstr irInstr;

    public MipsComment(IrInstr instr) {
        super("comment");
        this.irInstr = instr;
    }

    @Override
    public String toString() {
        return String.format("\n# %s", irInstr.toString());
    }
}
