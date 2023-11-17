package llvm;

import llvm.instr.IrInstr;
import llvm.instr.IrInstrType;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

import java.util.ArrayList;

public class IrFunction extends IrUser {
    private IrIntegetType returnType; //函数返回值
    private ArrayList<IrValue> params;//对于函数来说，是形参param
    private ArrayList<IrBasicBlock> basicBlocks;//函数中的基本块

    public IrFunction(String name, IrIntegetType returnType) {
        super(name, IrValueType.FUNCTION);
        this.returnType = returnType;
        this.params = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
    }

    public IrIntegetType getReturnType() {
        return returnType;
    }

    public void setReturnType(IrIntegetType returnType) {
        this.returnType = returnType;
    }

    public void addParam(IrValue param) {
        params.add(param);
    }

    public void addBasicBlock(IrBasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public ArrayList<IrValue> getParams() {
        return params;
    }

    public ArrayList<IrBasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void checkReturn() {//检查最后有没有return指令
        if (returnType == IrIntegetType.INT32) {
            return;
        }
        /**
         * 如果函数只有一个基本块,最后一个块就是当前的块
         * 如果有多个基本块,if和for都会把最后一个块设为当前基本块
         * 因此,最后一个块就是当前的基本块
         */
        IrBasicBlock lastBasicBlock = IrBuilder.IRBUILDER.getCurBasicBlock();
        if (lastBasicBlock.getInstrs().size() == 0) {
            IrBuilder.IRBUILDER.buildRetInstr(null);
        } else {
            int size = lastBasicBlock.getInstrs().size();
            IrInstr lastInstr = lastBasicBlock.getInstrs().get(size - 1);
            if (lastInstr.getIrInstrType() != IrInstrType.RET) {
                IrBuilder.IRBUILDER.buildRetInstr(null);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder paramsBuilder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            IrValue param = params.get(i);
            paramsBuilder.append(param.getType()).append(" ").append(param.getName());
            if (i < params.size() - 1) {
                paramsBuilder.append(", ");
            }
        }
        sb.append("define dso_local " + returnType + " " + getName() + "(" + paramsBuilder.toString() + "){").append("\n");
        for (IrBasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock.toString()).append("\n");
        }
        sb.append("}").append("\n");
        return sb.toString();
    }
}
