package llvm.instr;

import llvm.IrConstInt;
import llvm.IrFunction;
import llvm.IrGlobalVariable;
import llvm.IrValue;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

import java.util.ArrayList;

public class IrCallInstr extends IrInstr {
    private int argumentSize;

    public IrCallInstr(String name, IrFunction irFunction, ArrayList<IrValue> arguments) {
        super(name, irFunction.getReturnType(), IrInstrType.CALL);
        this.argumentSize = arguments.size();
        modifyOperand(irFunction, 0);
        //需要构建use-def关系,需要把所有的argument加入到操作数中
        for (int i = 0; i < arguments.size(); i++) {
            modifyOperand(arguments.get(i), i + 1);
        }
    }

    public IrFunction getFunction() {
        return (IrFunction) getOperand(0);
    }

    public IrValue getArgument(int index) { //index从1开始
        return getOperand(index);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getType() != IrIntegetType.VOID) {
            sb.append(getName()).append(" = ");
        }
        sb.append("call").append(" ");
        sb.append(getType()).append(" ");
        sb.append(getFunction().getName()).append("(");
        for (int i = 1; i <= argumentSize; i++) {
            IrValue argument = getArgument(i);
            sb.append(argument.getType()).append(" ").append(argument.getName());
            if (i < argumentSize) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        //保存sp和ra
        int curOffset = MipsBuilder.MIPSBUILDER.getCurOffset();
        MipsBuilder.MIPSBUILDER.buildSw(29, 29, curOffset);
        curOffset = MipsBuilder.MIPSBUILDER.moveCurOffset(4);
        MipsBuilder.MIPSBUILDER.buildSw(31, 29, curOffset);
        curOffset = MipsBuilder.MIPSBUILDER.moveCurOffset(4);
        int spRecord = curOffset;
        //传入参数
        for (int i = 1; i <= argumentSize; i++) {
            IrValue argument = getArgument(i);
            if (argument instanceof IrConstInt) {
                MipsBuilder.MIPSBUILDER.buildLi(8, ((IrConstInt) argument).getValue());
                MipsBuilder.MIPSBUILDER.buildSw(8, 29, curOffset);
            } else if (argument instanceof IrGlobalVariable) {
                MipsBuilder.MIPSBUILDER.buildLa(8, argument.getName().substring(1));
                MipsBuilder.MIPSBUILDER.buildLw(8, 8, 0);
                MipsBuilder.MIPSBUILDER.buildSw(8, 29, curOffset);
            } else { //一定是被load出来的
                int offset = MipsBuilder.MIPSBUILDER.getSymbolOffset(argument);
                MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset);
                MipsBuilder.MIPSBUILDER.buildSw(8, 29, curOffset);
            }
            curOffset = MipsBuilder.MIPSBUILDER.moveCurOffset(4);//每次传一个参数,需要把指针下移
        }
        //调整sp指针位置
        MipsBuilder.MIPSBUILDER.buildAddi(29, 29, spRecord);
        //跳入函数
        MipsBuilder.MIPSBUILDER.buildJal(getFunction().getName().substring(1));
        //恢复ra和sp
        MipsBuilder.MIPSBUILDER.buildLw(31, 29, 4);
        MipsBuilder.MIPSBUILDER.buildLw(29, 29, 8);
        //如果函数有返回值,需要构建一个变量
        if (getFunction().getReturnType() != IrIntegetType.VOID) {
            int offset = MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            MipsBuilder.MIPSBUILDER.buildSw(1, 29, offset);
        }
    }
}
