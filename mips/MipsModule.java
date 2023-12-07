package mips;

import java.util.ArrayList;

public class MipsModule {
    private final ArrayList<MipsGlobalData> mipsGlobalData;
    private final ArrayList<MipsValue> mipsTextData;

    public MipsModule() {
        this.mipsGlobalData = new ArrayList<>();
        this.mipsTextData = new ArrayList<>();
    }

    public void addGlobalData(MipsGlobalData globalData) {
        this.mipsGlobalData.add(globalData);
    }

    public void addTextData(MipsValue textData) {
        this.mipsTextData.add(textData);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        for (MipsGlobalData globalData : mipsGlobalData) {
            sb.append(globalData.toString()).append("\n");
        }
        sb.append("\n.text\n");
        sb.append("jal main\n");
        sb.append("j end\n\n");
        for (MipsValue textData : mipsTextData) {
            sb.append(textData.toString()).append("\n");
        }
        sb.append("end:\n");
        return sb.toString();
    }
}
