package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsJr extends MipsInstr {
    //jr rs
    private int rs;

    public MipsJr(int rs) {
        super("jr");
        this.rs = rs;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getName(), regMap.get(rs).toString().toLowerCase());
    }
}
