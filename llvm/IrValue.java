package llvm;

import llvm.type.IrValueType;

import java.util.ArrayList;
import java.util.Iterator;

public class IrValue {
    //对于每个value的name
    private String name;
    private IrValueType type;
    private ArrayList<IrUse> irUses;//def-use链

    public IrValue(String name, IrValueType type) {
        this.name = name;
        this.type = type;
        this.irUses = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public IrValueType getType() {
        return type;
    }

    public ArrayList<IrUse> getIrUses() {
        return irUses;
    }

    public void addUse(IrUse use) {
        this.irUses.add(use);
    }

    public void removeUse(IrUse oldUse) {
        Iterator<IrUse> iterator = irUses.iterator();
        while (iterator.hasNext()) {
            IrUse use = iterator.next();
            if (use == oldUse) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public String toString() {
        return type + " " + name;
    }

}
