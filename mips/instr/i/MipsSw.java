package mips.instr.i;

import mips.instr.MipsInstr;
import mips.instr.other.MipsSyscall;

public class MipsSw extends MipsInstr {
    //sw rt, offset(base)
    //memory[GPR[base]+offset] -> GPR[rt]
    private int rt;
    private int base;
    private int offset;

    public MipsSw(int rt, int base, int offset) {
        super("sw");
        this.rt = rt;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %d(%s)", getName(),
                regMap.get(rt).toString().toLowerCase(),
                offset,
                regMap.get(base).toString().toLowerCase());
    }
}
