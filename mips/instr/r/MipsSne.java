package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsSne extends MipsInstr {
    //sne rd, rs, rt
    //GPR[rd] <- (GPR[rs] != GPR[rt]) 不等置1
    private int rd;
    private int rs;
    private int rt;

    public MipsSne(int rd, int rs, int rt) {
        super("sne");
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
