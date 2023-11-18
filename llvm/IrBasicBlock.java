package llvm;

import llvm.instr.IrInstr;
import llvm.type.IrValueType;
import mips.MipsBuilder;

import java.util.ArrayList;

public class IrBasicBlock extends IrValue {
    private ArrayList<IrInstr> instrs;//基本块中的指令
    private IrFunction function;//父亲function

    public IrBasicBlock(String name, IrFunction function) {
        super(name, IrValueType.BBLOCK);
        this.instrs = new ArrayList<>();
        this.function = function;
    }

    public void addInstr(IrInstr instr) {
        instrs.add(instr);
    }

    public ArrayList<IrInstr> getInstrs() {
        return instrs;
    }

    public IrFunction getFunction() {
        return function;
    }

    public void setFunction(IrFunction function) {
        this.function = function;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(":").append("\n\t");
        for (IrInstr instr : instrs) {
            sb.append(instr).append("\n\t");
        }
        return sb.toString();
    }

    @Override
    public void buildMips() {
        //构建基本块的label
        MipsBuilder.MIPSBUILDER.buildMipsLabel(getName());
        for (IrInstr instr : instrs) {
            instr.buildMips();
        }
    }
}
