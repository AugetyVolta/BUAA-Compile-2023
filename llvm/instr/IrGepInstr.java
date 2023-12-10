package llvm.instr;

import llvm.IrConstInt;
import llvm.IrConstStr;
import llvm.IrGlobalVariable;
import llvm.IrValue;
import llvm.type.IrArrayType;
import llvm.type.IrIntegetType;
import llvm.type.IrPointerType;
import llvm.type.IrValueType;
import mips.MipsBuilder;

public class IrGepInstr extends IrInstr {
    //getelementptr [6 x i32], [6 x i32]* @a, i32 0, i32 4 a[4] 返回一个指向a[4]的指针
    //getelementptr i32, i32* @a, i32 4 a[4] 返回一个指向a[4]的指针
    public IrGepInstr(String name, IrValue pointer, IrValue offset) {
        super(name, new IrPointerType(IrIntegetType.INT32), IrInstrType.GETELEMENTPTR);
        modifyOperand(pointer, 0);
        modifyOperand(offset, 1);
    }

    //用于字符串输出的Gep指令
    public IrGepInstr(String name, IrValue pointer) {
        super(name, new IrPointerType(IrIntegetType.INT8), IrInstrType.GETELEMENTPTR);
        modifyOperand(pointer, 0);
        modifyOperand(new IrConstInt(0), 1);
    }

    public IrValue getPointer() {
        return getOperand(0);
    }

    public IrValue getOffset() {
        return getOperand(1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IrValueType refType = ((IrPointerType) getPointer().getType()).getRefType();
        sb.append(getName()).append(" = getelementptr inbounds ");
        sb.append(refType).append(", ");
        sb.append(getPointer().getType()).append(" ").append(getPointer().getName()).append(", "); //type name,必须这样写，因为可能指针是一条指令，但是toString方法被重写了
        if (refType instanceof IrArrayType) { //如果传入的是[ x i32]*,就需要多加一个i32 0
            sb.append("i32 0, ");
        }
        sb.append(getOffset().getType()).append(" ").append(getOffset().getName()); //type name
        return sb.toString();
    }

    @Override
    public void buildMips() {
        MipsBuilder.MIPSBUILDER.buildComment(this);
        //如果是字符串常量,就不需要build这个load指令了,因为直接访问标签就行
        if (getPointer() instanceof IrConstStr) {
            return;
        }
        //取出目标所在的地址
        if (getPointer() instanceof IrGlobalVariable) {//如果是全局变量
            //基地址
            MipsBuilder.MIPSBUILDER.buildLa(8, getPointer().getName().substring(1));
        } else {//局部数组
            int offset = MipsBuilder.MIPSBUILDER.getSymbolOffset(getPointer());
            //基地址
            MipsBuilder.MIPSBUILDER.buildLw(8, 29, offset);
            //为了统一局部变量和函数传参,统一对于数组,都存的是数组的基地址,之后再load两次
        }
        //偏移量
        IrValue offset = getOffset();
        if (offset instanceof IrConstInt) {
            MipsBuilder.MIPSBUILDER.buildLi(9, ((IrConstInt) offset).getValue());
        } else if (offset instanceof IrGlobalVariable) { //由于全局变量使用前一定会被load,所以这条语句不会被执行
            //获得了全局变量的地址
            MipsBuilder.MIPSBUILDER.buildLa(9, offset.getName().substring(1));
            //从地址中读取值
            MipsBuilder.MIPSBUILDER.buildLw(9, 9, 0);
        } else { //一定是被load出来的
            MipsBuilder.MIPSBUILDER.buildLw(9, 29, MipsBuilder.MIPSBUILDER.getSymbolOffset(offset));
        }
        //偏移量+基地址
        MipsBuilder.MIPSBUILDER.buildSll(9, 9, 2);//要乘4
        MipsBuilder.MIPSBUILDER.buildAddu(8, 8, 9);
        //构建变量并存储数组初地址
        MipsBuilder.MIPSBUILDER.buildSw(8, 29, MipsBuilder.MIPSBUILDER.buildVarSymbol(this));
    }


    public String hash() {
        return this.toString().substring(0, this.toString().indexOf('='));
    }


}
