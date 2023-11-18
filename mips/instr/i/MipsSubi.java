package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsSubi extends MipsInstr {
    // subi rt, rs, immediate
    private int rt;
    private int rs;
    private int imm;

    public MipsSubi(int rt, int rs, int imm) {
        super("subi");
        this.rt = rt;
        this.rs = rs;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %d", getName(),
                regMap.get(rt).toString().toLowerCase(),
                regMap.get(rs).toString().toLowerCase(),
                imm);
    }
}
