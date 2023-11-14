package llvm;

import llvm.type.IrValueType;

import java.util.ArrayList;

public class IrUser extends IrValue {
    private ArrayList<IrUse> operands = new ArrayList<>(); //通过use连接的操作数,双向边

    public IrUser(String name, IrValueType type) {
        super(name, type);
    }

    public void addUse(IrUse use) {
        operands.add(use);
    }

    public IrValue getOperand(int opIndex) {
        for (IrUse use : operands) {
            if (use.getOperateIndex() == opIndex) {
                return use.getIrUsee();
            }
        }
        return null;
    }

    public void modifyOperand(IrValue newValue, int opIndex) {
        boolean needNew = true; //是否需要创建use,默认创建新use
        IrUse oldUse = null;
        for (IrUse use : operands) {
            if (use.getOperateIndex() == opIndex) {
                oldUse = use;
                needNew = false;
                break;
            }
        }
        if (needNew) {
            IrUse newUse = new IrUse(this, newValue, opIndex);
            this.operands.add(newUse);
        } else {
            oldUse.getIrUsee().removeUse(oldUse); // oldUse原对应的value中删去自己
            oldUse.setIrUsee(newValue); //为use设置新的value
            newValue.addUse(oldUse); //新的value中添加use
        }
    }

}
