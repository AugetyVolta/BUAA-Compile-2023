package mips.instr.other;

import mips.instr.MipsInstr;

public class MipsLabel extends MipsInstr {

    public MipsLabel(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return String.format("\n%s:", getName());
    }
}
