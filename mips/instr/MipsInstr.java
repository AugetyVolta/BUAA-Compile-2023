package mips.instr;

import mips.MipsValue;

public class MipsInstr extends MipsValue {
    private final String name;

    public MipsInstr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
