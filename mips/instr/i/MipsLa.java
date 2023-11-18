package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsLa extends MipsInstr {
    private String label;

    public MipsLa(String label) {
        super("la");
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getName(), label);
    }
}
