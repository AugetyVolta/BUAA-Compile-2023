package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsSlt extends MipsInstr {
    //slt rd, rs, rt
    //GPR[rd] <- (GPR[rs] < GPR[rt]) 小于置1
    private int rd;
    private int rs;
    private int rt;

    public MipsSlt(int rd, int rs, int rt) {
        super("slt");
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
