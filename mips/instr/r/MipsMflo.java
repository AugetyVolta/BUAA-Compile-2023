package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsMflo extends MipsInstr {
    //mflo rd
    //GPR[rd] <- LO
    private int rd;

    public MipsMflo(int rd) {
        super("mflo");
        this.rd = rd;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getName(), regMap.get(rd).toString().toLowerCase());
    }
}
