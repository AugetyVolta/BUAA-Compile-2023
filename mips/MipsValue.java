package mips;

import java.util.HashMap;

public class MipsValue {
    public static final HashMap<Integer, MipsReg> regMap = new HashMap<>();

    static {
        regMap.put(0, MipsReg.$ZERO);
        regMap.put(1, MipsReg.$V0);
        regMap.put(2, MipsReg.$V1);
        regMap.put(3, MipsReg.$V2);
        regMap.put(4, MipsReg.$A0);
        regMap.put(5, MipsReg.$A1);
        regMap.put(6, MipsReg.$A2);
        regMap.put(7, MipsReg.$A3);
        regMap.put(8, MipsReg.$T0);
        regMap.put(9, MipsReg.$T1);
        regMap.put(10, MipsReg.$T2);
        regMap.put(11, MipsReg.$T3);
        regMap.put(12, MipsReg.$T4);
        regMap.put(13, MipsReg.$T5);
        regMap.put(14, MipsReg.$T6);
        regMap.put(15, MipsReg.$T7);
        regMap.put(16, MipsReg.$S0);
        regMap.put(17, MipsReg.$S1);
        regMap.put(18, MipsReg.$S2);
        regMap.put(19, MipsReg.$S3);
        regMap.put(20, MipsReg.$S4);
        regMap.put(21, MipsReg.$S5);
        regMap.put(22, MipsReg.$S6);
        regMap.put(23, MipsReg.$S7);
        regMap.put(24, MipsReg.$T8);
        regMap.put(25, MipsReg.$T9);
        regMap.put(26, MipsReg.$K0);
        regMap.put(27, MipsReg.$K1);
        regMap.put(28, MipsReg.$GP);
        regMap.put(29, MipsReg.$SP);
        regMap.put(30, MipsReg.$FP);
        regMap.put(31, MipsReg.$RA);
    }

    public MipsValue() {

    }
}
