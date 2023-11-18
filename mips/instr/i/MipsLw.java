package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsLw extends MipsInstr {
    //lw rt, offset(base)
    //GPR[rt] <- memory[GPR[base]+offset]
    private int rt;
    private int base;
    private int offset;

    public MipsLw(int rt, int base, int offset) {
        super("lw");
        this.rt = rt;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %d(%s)", getName(),
                regMap.get(rt).toString().toLowerCase(),
                offset,
                regMap.get(base).toString().toLowerCase());
    }
}
