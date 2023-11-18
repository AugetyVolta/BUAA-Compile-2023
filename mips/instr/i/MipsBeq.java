package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsBeq extends MipsInstr {
    //beq rs, rt, offset
    private int rs;
    private int rt;
    private String label;

    public MipsBeq(int rs, int rt, String label) {
        super("beq");
        this.rs = rs;
        this.rt = rt;
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %s", getName(),
                regMap.get(rs).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase(),
                label);
    }
}
