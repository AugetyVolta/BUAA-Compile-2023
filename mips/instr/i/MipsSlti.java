package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsSlti extends MipsInstr {
    private int rt;
    private int rs;
    private int s;

    public MipsSlti(int rt, int rs, int s) {
        super("slti");
        this.rt = rt;
        this.rs = rs;
        this.s = s;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %d", getName(),
                regMap.get(rt).toString().toLowerCase(),
                regMap.get(rs).toString().toLowerCase(),
                s);
    }
}
