package mips.instr.i;

import mips.instr.MipsInstr;

public class MipsBne extends MipsInstr {
    //bne rs, rt, offset
    private int rs;
    private int rt;
    private String label;

    public MipsBne(int rs, int rt, String label) {
        super("bne");
        this.rs = rs;
        this.rt = rt;
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %s", getName(),
                regMap.get(rs).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase(),
                label);
    }
}
