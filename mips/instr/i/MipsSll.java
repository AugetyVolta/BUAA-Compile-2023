package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsSll extends MipsInstr {
    // sll rd, rt, s
    private int rd;
    private int rt;
    private int s;

    public MipsSll(int rd, int rt, int s) {
        super("sll");
        this.rt = rt;
        this.rd = rd;
        this.s = s;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %d", getName(),
                regMap.get(rd).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase(),
                s);
    }
}
