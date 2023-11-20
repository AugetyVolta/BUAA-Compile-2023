package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsLa extends MipsInstr {
    int rt;
    private String label;

    public MipsLa(int rt, String label) {
        super("la");
        this.rt = rt;
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", getName(),
                regMap.get(rt).toString().toLowerCase(),
                label);
    }
}
