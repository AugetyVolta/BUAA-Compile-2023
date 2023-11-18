package mips.instr.j;

import mips.instr.MipsInstr;

public class MipsJ extends MipsInstr {
    //j label
    private final String labelName;

    public MipsJ(String labelName) {
        super("j");
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getName(), labelName);
    }
}
