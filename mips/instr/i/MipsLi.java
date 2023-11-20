package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsLi extends MipsInstr {
    private int rt;
    private int imm;

    public MipsLi(int rt, int imm) {
        super("li");
        this.rt = rt;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %d", getName(),
                regMap.get(rt).toString().toLowerCase(),
                imm);
    }
}
