package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsAdd extends MipsInstr {
    private int rd;
    private int rs;
    private int rt;

    public MipsAdd(int rd, int rs, int rt) {
        super("add");
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %s", getName(),
                regMap.get(rd).toString().toLowerCase(),
                regMap.get(rs).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase());
    }

}
