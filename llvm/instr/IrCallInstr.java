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
        //将函数是否被调用设置为true
        irFunction.isCalled = true;
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
        //保存a1-a3寄存器
        //MipsBuilder.MIPSBUILDER.saveAReg();
        //传入参数
        int aReg = 5;
        for (int i = 1; i <= argumentSize; i++) {
            IrValue argument = getArgument(i);
            int reg;
            if (i <= 0) {
                if (MipsBuilder.MIPSBUILDER.hasAllocReg(argument)) {
                    reg = MipsBuilder.MIPSBUILDER.getReg(argument);
                    if (argument instanceof IrConstInt) {
                        MipsBuilder.MIPSBUILDER.buildLi(aReg, ((IrConstInt) argument).getValue());
                    } else if (argument instanceof IrGlobalVariable) {
                        MipsBuilder.MIPSBUILDER.buildLa(26, argument.getName().substring(1));
                        MipsBuilder.MIPSBUILDER.buildLw(aReg, 26, 0);
                    } else { //一定是被load出来的
                        MipsBuilder.MIPSBUILDER.buildMove(aReg, reg);
                    }
                } else {
                    if (argument instanceof IrConstInt) {
                        MipsBuilder.MIPSBUILDER.buildLi(aReg, ((IrConstInt) argument).getValue());
                    } else if (argument instanceof IrGlobalVariable) {
                        MipsBuilder.MIPSBUILDER.buildLa(26, argument.getName().substring(1));
                        MipsBuilder.MIPSBUILDER.buildLw(aReg, 26, 0);
                    } else { //一定是被load出来的
                        int offset = MipsBuilder.MIPSBUILDER.getSymbolOffset(argument);
                        MipsBuilder.MIPSBUILDER.buildLw(aReg, 29, offset);
                    }
                }
                aReg++;//a1,a2,a3
                curOffset = MipsBuilder.MIPSBUILDER.moveCurOffset(4);//每次传一个参数,需要把指针下移
            } else {
                if (MipsBuilder.MIPSBUILDER.hasAllocReg(argument)) {
                    reg = MipsBuilder.MIPSBUILDER.getReg(argument);
                    if (argument instanceof IrConstInt) {
                        MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) argument).getValue());
                        MipsBuilder.MIPSBUILDER.buildSw(26, 29, curOffset);
                    } else if (argument instanceof IrGlobalVariable) {
                        MipsBuilder.MIPSBUILDER.buildLa(26, argument.getName().substring(1));
                        MipsBuilder.MIPSBUILDER.buildLw(26, 26, 0);
                        MipsBuilder.MIPSBUILDER.buildSw(26, 29, curOffset);
                    } else { //一定是被load出来的
                        MipsBuilder.MIPSBUILDER.buildSw(reg, 29, curOffset);
                    }
                } else {
                    if (argument instanceof IrConstInt) {
                        MipsBuilder.MIPSBUILDER.buildLi(26, ((IrConstInt) argument).getValue());
                        MipsBuilder.MIPSBUILDER.buildSw(26, 29, curOffset);
                    } else if (argument instanceof IrGlobalVariable) {
                        MipsBuilder.MIPSBUILDER.buildLa(26, argument.getName().substring(1));
                        MipsBuilder.MIPSBUILDER.buildLw(26, 26, 0);
                        MipsBuilder.MIPSBUILDER.buildSw(26, 29, curOffset);
                    } else { //一定是被load出来的
                        int offset = MipsBuilder.MIPSBUILDER.getSymbolOffset(argument);
                        MipsBuilder.MIPSBUILDER.buildLw(26, 29, offset);
                        MipsBuilder.MIPSBUILDER.buildSw(26, 29, curOffset);
                    }
                }
                curOffset = MipsBuilder.MIPSBUILDER.moveCurOffset(4);//每次传一个参数,需要把指针下移
            }
        }
        //将所有的寄存器写回地址
        MipsBuilder.MIPSBUILDER.writeBackAll();
        //调整sp指针位置
        MipsBuilder.MIPSBUILDER.buildAddi(29, 29, spRecord);
        //跳入函数
        MipsBuilder.MIPSBUILDER.buildJal(getFunction().getName().substring(1));
        //恢复ra和sp
        MipsBuilder.MIPSBUILDER.buildLw(31, 29, 4);
        MipsBuilder.MIPSBUILDER.buildLw(29, 29, 8);
        //MipsBuilder.MIPSBUILDER.popAReg();
        //如果函数有返回值,需要构建一个变量
        if (getFunction().getReturnType() != IrIntegetType.VOID) {
            MipsBuilder.MIPSBUILDER.buildVarSymbol(this);
            //为返回值分配寄存器
            int reg = MipsBuilder.MIPSBUILDER.allocReg(this);
            MipsBuilder.MIPSBUILDER.buildMove(reg, 1);//将返回值move给分配的寄存器
        }
    }

    public String hash() {
        if (getType() != IrIntegetType.VOID) {
            return this.toString().substring(0, this.toString().indexOf('='));
        }
        return null;
    }

}
