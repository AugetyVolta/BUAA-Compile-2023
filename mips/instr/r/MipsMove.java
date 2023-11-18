package mips.instr.r;

import mips.MipsModule;
import mips.instr.MipsInstr;

public class MipsMove extends MipsInstr {
    private int dst;
    private int src;

    public MipsMove(int dst, int src) {
        super("move");
        this.dst = dst;
        this.src = src;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", getName(),
                regMap.get(dst).toString().toLowerCase(),
                regMap.get(src).toString().toLowerCase());
    }
}
