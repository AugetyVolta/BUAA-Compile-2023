package llvm;

import llvm.instr.*;
import llvm.type.IrIntegetType;
import llvm.type.IrValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class IrBuilder {
    public static IrBuilder IRBUILDER = new IrBuilder(); //全局静态变量
    private final IrModule module;//全局module,只有一个
    private IrBasicBlock curBasicBlock;//当前所处基本块
    private IrFunction curFunction;//当前所处函数
    private int globalVarCnt = 0;//全局变量的数量
    private int paramCnt = 0;//形参的数量
    private int basicBlockCnt = 0;//基本块的数量
    private final HashMap<IrFunction, Integer> varInFunctionCnt;//记录每一个函数块中的参数个数
    private final Stack<IrBasicBlock> loopStmt2Blocks = new Stack<>();//for循环的forStmt2
    private final Stack<IrBasicBlock> loopAfterBlocks = new Stack<>();//循环之后的block

    public IrBuilder() {
        this.module = new IrModule();
        this.curBasicBlock = null;
        this.curFunction = null;
        this.varInFunctionCnt = new HashMap<>();
    }

    //获取module
    public IrModule getModule() {
        return this.module;
    }

    //设置当前所处基本块
    public void setCurBasicBlock(IrBasicBlock irBasicBlock) {
        this.curBasicBlock = irBasicBlock;
    }

    //得到当前所处基本块
    public IrBasicBlock getCurBasicBlock() {
        return curBasicBlock;
    }

    //设置当前所处函数
    public void setCurFunction(IrFunction irFunction) {
        this.curFunction = irFunction;
    }

    public String generateVarName() {
        int varIndex = varInFunctionCnt.getOrDefault(curFunction, 0);
        //v%d标注指令对应的result变量的名字
        String formatName = String.format("%%v%d", varIndex++);
        //修改当前函数的总指令数
        varInFunctionCnt.put(curFunction, varIndex);
        return formatName;
    }

    public void enterLoop(IrBasicBlock loopStmt2Block, IrBasicBlock loopAfterBlock) {
        loopStmt2Blocks.push(loopStmt2Block);
        loopAfterBlocks.push(loopAfterBlock);
    }

    public void leaveLoop() {
        loopStmt2Blocks.pop();
        loopAfterBlocks.pop();
    }

    public IrBasicBlock getLoopStmt2Block() {
        return loopStmt2Blocks.peek();
    }

    public IrBasicBlock getLoopAfterBlock() {
        return loopAfterBlocks.peek();
    }

    //构建全局变量
    public IrGlobalVariable buildGlobalVariable(IrValueType irValueType, int length, boolean isConst, ArrayList<IrConstInt> initValues) {
        //@g%d标注全局变量的名字
        String formatName = String.format("@g%d", globalVarCnt++);
        IrGlobalVariable irGlobalVariable = new IrGlobalVariable(formatName, irValueType, length, isConst);
        if (initValues != null) {
            irGlobalVariable.setInitValues(initValues);
        }
        //将globalVariable加入到module中
        module.addIrGlobalVariable(irGlobalVariable);
        return irGlobalVariable;
    }

    //构建函数
    public IrFunction buildFunction(String name, IrIntegetType returnType) {
        //如果是main就是main,不是main就函数名@f_原函数名
        String formatName = name.equals("main") ? "@main" : "@f_" + name;
        IrFunction irFunction = new IrFunction(formatName, returnType);
        //将function加入到module中
        module.addIrFunction(irFunction);
        //将当前函数设为自己
        setCurFunction(irFunction);
        //函数一定会构建一个基本块，并且一开始就处在基本块中
        buildBasicBlock();
        return irFunction;
    }

    //构造函数参数
    public IrValue buildParam(IrValueType irValueType) {
        String formatName = String.format("%%a%d", paramCnt++);
        IrValue param = new IrValue(formatName, irValueType);
        //将param加入到function中
        curFunction.addParam(param);
        return param;
    }

    //构建基本块
    public IrBasicBlock buildBasicBlock() {
        //b%d标注基本块的名字
        String formatName = String.format("b%d", basicBlockCnt++);
        IrBasicBlock irBasicBlock = new IrBasicBlock(formatName, curFunction);
        //将basicBlock加入函数中
        curFunction.addBasicBlock(irBasicBlock);
        //并且会处于新建的basicBlock中
        setCurBasicBlock(irBasicBlock);
        return irBasicBlock;
    }

    //构建alloca指令
    public IrAllocaInstr buildAllocaInstr(IrValueType irValueType) {
        String formatName = generateVarName();
        IrAllocaInstr allocaInstr = new IrAllocaInstr(formatName, irValueType);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(allocaInstr);
        return allocaInstr;
    }

    //构建Binary指令
    public IrBinaryInstr buildBinaryInstr(IrValueType irValueType, IrInstrType irInstrType, IrValue operand1, IrValue operand2) {
        String formatName = generateVarName();
        IrBinaryInstr irBinaryInstr = new IrBinaryInstr(formatName, irValueType, irInstrType, operand1, operand2);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irBinaryInstr);
        return irBinaryInstr;
    }

    //构建Br指令(无条件跳转)
    public IrBrInstr buildBrInstr(IrBasicBlock basicBlock) {
        String formatName = generateVarName();
        IrBrInstr irBrInstr = new IrBrInstr(formatName, basicBlock);
        //如果前面没有return或者Br指令,将指令加入到当前基本块中
        int size = curBasicBlock.getInstrs().size();
        if (size == 0) {
            curBasicBlock.addInstr(irBrInstr);
        } else {
            IrInstr preInstr = curBasicBlock.getInstrs().get(size - 1);
            if (preInstr.getIrInstrType() != IrInstrType.RET &&
                    preInstr.getIrInstrType() != IrInstrType.BR) {
                curBasicBlock.addInstr(irBrInstr);
            }
        }
        return irBrInstr;
    }

    //构建Br指令(条件跳转)
    public IrBrInstr buildBrInstr(IrValue cond, IrBasicBlock basicBlock1, IrBasicBlock basicBlock2) {
        String formatName = generateVarName();
        IrBrInstr irBrInstr = new IrBrInstr(formatName, cond, basicBlock1, basicBlock2);
        //如果前面没有return或者Br指令,将指令加入到当前基本块中
        int size = curBasicBlock.getInstrs().size();
        if (size == 0) {
            curBasicBlock.addInstr(irBrInstr);
        } else {
            IrInstr preInstr = curBasicBlock.getInstrs().get(size - 1);
            if (preInstr.getIrInstrType() != IrInstrType.RET &&
                    preInstr.getIrInstrType() != IrInstrType.BR) {
                curBasicBlock.addInstr(irBrInstr);
            }
        }
        return irBrInstr;
    }

    //构建call指令
    public IrCallInstr buildCallInstr(IrFunction irFunction, ArrayList<IrValue> arguments) {//如果没参数,就new一个空的arraylist传入
        String formatName = generateVarName();
        IrCallInstr irCallInstr = new IrCallInstr(formatName, irFunction, arguments);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irCallInstr);
        return irCallInstr;
    }

    //构建getElementPtr指令
    public IrGepInstr buildGepInstr(IrValue pointer, IrValue offset) {
        String formatName = generateVarName();
        IrGepInstr irGepInstr = new IrGepInstr(formatName, pointer, offset);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irGepInstr);
        return irGepInstr;
    }

    //构建Icmp指令
    public IrIcmpInstr buildIcmpInstr(IrInstrType cond, IrValue operand1, IrValue operand2) {
        String formatName = generateVarName();
        IrIcmpInstr irIcmpInstr = new IrIcmpInstr(formatName, cond, operand1, operand2);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irIcmpInstr);
        return irIcmpInstr;
    }

    //构建Load指令
    public IrLoadInstr buildLoadInstr(IrValue pointer) {
        String formatName = generateVarName();
        IrLoadInstr irLoadInstr = new IrLoadInstr(formatName, pointer);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irLoadInstr);
        return irLoadInstr;
    }

    //构建store指令
    public IrStoreInstr buildStoreInstr(IrValue fromValue, IrValue pointer) {
        String formatName = generateVarName();
        IrStoreInstr irStoreInstr = new IrStoreInstr(formatName, fromValue, pointer);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irStoreInstr);
        return irStoreInstr;
    }

    //构建ret指令
    public IrRetInstr buildRetInstr(IrValue retValue) {
        String formatName = generateVarName();
        IrRetInstr irRetInstr = new IrRetInstr(formatName, retValue);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irRetInstr);
        return irRetInstr;
    }

    //构建zext指令
    public IrZextInstr buildZextInstr(IrValueType targetValueType, IrValue srcValue) {
        String formatName = generateVarName();
        IrZextInstr irZextInstr = new IrZextInstr(formatName, targetValueType, srcValue);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irZextInstr);
        return irZextInstr;
    }

    //构建getint指令
    public IrGetPutInstr buildGetIntInstr() {
        String formatName = generateVarName();
        IrGetPutInstr irGetPutInstr = new IrGetPutInstr(formatName, IrInstrType.GETINT);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irGetPutInstr);
        return irGetPutInstr;
    }

    //构建putint指令
    public IrGetPutInstr buildPutIntInstr(IrValue operand) {
        String formatName = generateVarName();
        IrGetPutInstr irGetPutInstr = new IrGetPutInstr(formatName, IrInstrType.PUTINT, operand);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irGetPutInstr);
        return irGetPutInstr;
    }

    //构建putch指令
    public IrGetPutInstr buildPutCharInstr(IrValue operand) {
        String formatName = generateVarName();
        IrGetPutInstr irGetPutInstr = new IrGetPutInstr(formatName, IrInstrType.PUTCH, operand);
        //将指令加入到当前基本块中
        curBasicBlock.addInstr(irGetPutInstr);
        return irGetPutInstr;
    }

}
