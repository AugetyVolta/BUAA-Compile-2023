package mips.instr.r;

import mips.instr.MipsInstr;

public class MipsMult extends MipsInstr {
    //mult rs, rt
    private int rs;
    private int rt;

    public MipsMult(int rs, int rt) {
        super("mult");
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", getName(),
                regMap.get(rs).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase());
    }

}
