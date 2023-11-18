package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsMfhi extends MipsInstr {
    //mfhi rd
    //GPR[rd] <- HI
    private int rd;

    public MipsMfhi(int rd) {
        super("mfhi");
        this.rd = rd;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getName(), regMap.get(rd).toString().toLowerCase());
    }
}
