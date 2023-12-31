package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsSubu extends MipsInstr {
    //subu rd, rs, rt
    //GPR[rd] <- GPR[rs] - GPR[rt]
    private int rd;
    private int rs;
    private int rt;

    public MipsSubu(int rd, int rs, int rt) {
        super("subu");
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
