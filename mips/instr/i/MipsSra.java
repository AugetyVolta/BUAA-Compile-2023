package mips.instr.i;
import mips.instr.MipsInstr;

public class MipsSra extends MipsInstr {
    private int rd;
    private int rt;
    private int s;

    public MipsSra(int rd, int rt, int s) {
        super("sra");
        this.rd = rd;
        this.rt = rt;
        this.s = s;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s, %d", getName(),
                regMap.get(rd).toString().toLowerCase(),
                regMap.get(rt).toString().toLowerCase(),
                s);
    }
}
