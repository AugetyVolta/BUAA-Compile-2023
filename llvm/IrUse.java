package llvm;

import llvm.type.IrValueType;

public class IrUse {
    private IrUser irUser; //user
    private IrValue irUsee; //被使用的value
    private int operateIndex;//第几个操作数,0,1,2

    public IrUse(IrUser irUser, IrValue irUsee, int operateIndex) {
        this.irUser = irUser;
        this.irUsee = irUsee;
        this.operateIndex = operateIndex;
        irUser.addUse(this);
        irUsee.addUse(this);
    }

    public IrUser getIrUser() {
        return irUser;
    }

    public void setIrUser(IrUser irUser) {
        this.irUser = irUser;
    }

    public IrValue getIrUsee() {
        return irUsee;
    }

    public void setIrUsee(IrValue irUsee) {
        this.irUsee = irUsee;
    }

    public int getOperateIndex() {
        return operateIndex;
    }

    public void setOperateIndex(int operateIndex) {
        this.operateIndex = operateIndex;
    }
}
