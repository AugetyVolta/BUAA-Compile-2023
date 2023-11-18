package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsSgt extends MipsInstr {
    //sgt rd, rs, rt
    //GPR[rd] <- (GPR[rs] > GPR[rt]) 大于置1
    private int rd;
    private int rs;
    private int rt;

    public MipsSgt(int rd, int rs, int rt) {
        super("sgt");
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
