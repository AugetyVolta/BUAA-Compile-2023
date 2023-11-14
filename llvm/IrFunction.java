package llvm;

import llvm.instr.IrInstr;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

import java.util.ArrayList;

public class IrFunction extends IrUser {
    private IrIntegetType returnType; //函数返回值
    private ArrayList<IrValue> params;//对于函数来说，是形参param
    private ArrayList<IrBasicBlock> basicBlocks;//函数中的基本快

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
