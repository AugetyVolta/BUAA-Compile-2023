package llvm;

import llvm.instr.*;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class IrFunction extends IrUser {
    private IrIntegetType returnType; //函数返回值
    private ArrayList<IrValue> params = new ArrayList<>();//对于函数来说，是形参param
    private ArrayList<IrBasicBlock> basicBlocks = new ArrayList<>();//函数中的基本块
    public boolean isCalled = false;

    public IrFunction(String name, IrIntegetType returnType) {
        super(name, IrValueType.FUNCTION);
        this.returnType = returnType;
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

    @Override
    public void buildMips() {
        //构建函数名
        MipsBuilder.MIPSBUILDER.buildMipsLabel(getName().substring(1));
        //进入函数
        MipsBuilder.MIPSBUILDER.enterFunction();
        //填入参数
        for (IrValue param : params) {
            MipsBuilder.MIPSBUILDER.buildVarSymbol(param);
        }
        //allocReg();
        //相邻块跳转优化
        for (int i = 0; i < basicBlocks.size(); i++) {
            IrBasicBlock basicBlock = basicBlocks.get(i);
            if (i < basicBlocks.size() - 1) {
                if (basicBlock.getNext().size() == 1 &&
                        basicBlock.getNext().get(0).equals(basicBlocks.get(i + 1))) {
                    basicBlock.buildMipsWithoutBr();
                } else {
                    basicBlock.buildMips();
                }
            } else {
                basicBlock.buildMips();
            }
        }
    }

    public void allocReg() {
        ArrayList<IrValue> values = new ArrayList<>();
//        for (IrValue param : params) {
//            values.add(param);
//        }
        for (IrBasicBlock basicBlock : getBasicBlocks()) {
            for (IrInstr instr : basicBlock.getInstrs()) {
                if (satisfyCond(instr)) {
                    values.add(instr);
                }
            }
        }
        Collections.sort(values);
        for (int i = 0; i < 10 && i < values.size(); i++) {
            IrValue value = values.get(i);
            MipsBuilder.MIPSBUILDER.setReg(value, i + 5);
            MipsBuilder.MIPSBUILDER.buildVarSymbol(value);
        }
    }

    public boolean satisfyCond(IrInstr instr) {
        if (instr instanceof IrBinaryInstr || instr instanceof IrGepInstr ||
                instr instanceof IrIcmpInstr || instr instanceof IrZextInstr ||
                instr instanceof IrCallInstr && instr.getType() != IrIntegetType.VOID) {
            return true;
        }
        return false;
    }
}
