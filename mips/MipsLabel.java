package mips;

public class MipsLabel extends MipsValue {
    private final String name;

    public MipsLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s:\n", name);
    }
}
