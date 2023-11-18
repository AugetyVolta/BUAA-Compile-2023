package mips.instr.other;

import mips.instr.MipsInstr;

public class MipsSyscall extends MipsInstr {
    public MipsSyscall() {
        super("syscall");
    }

    @Override
    public String toString() {
        return getName();
    }
}
