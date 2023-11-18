package mips.instr.j;

import mips.instr.MipsInstr;

public class MipsJal extends MipsInstr {
    //jal label
    private final String labelName;

    public MipsJal(String labelName) {
        super("jal");
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getName(), labelName);
    }
}
