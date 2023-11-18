package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsDiv extends MipsInstr {
    //div rs, rt
    //GPR[rs]/GPR[rt]
    private int rs;
    private int rt;

    public MipsDiv(int rs, int rt) {
        super("div");
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", getName(),
                regMap.get(rs).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase());
    }
}
