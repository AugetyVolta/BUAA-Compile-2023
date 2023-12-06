package llvm;

import llvm.instr.*;
import llvm.type.IrValueType;
import mips.MipsBuilder;

import java.util.ArrayList;
import java.util.Iterator;

import static optimizer.Mem2Reg.removeDeadUse;

public class IrBasicBlock extends IrValue {
    private ArrayList<IrInstr> instrs;//基本块中的指令
    private IrFunction function;//父亲function
    private boolean canAdd;//是否可以继续加指令,因为之前已经有ret或者br指令了
    private ArrayList<IrBasicBlock> prev = new ArrayList<>();//前驱节点
    private ArrayList<IrBasicBlock> next = new ArrayList<>();//后继节点
    private ArrayList<IrValue> in = new ArrayList<>(); //in
    private ArrayList<IrValue> out = new ArrayList<>();//out
    private ArrayList<IrValue> def = new ArrayList<>(); //def
    private ArrayList<IrValue> use = new ArrayList<>();//use

    public IrBasicBlock(String name, IrFunction function) {
        super(name, IrValueType.BBLOCK);
        this.instrs = new ArrayList<>();
        this.function = function;
        this.canAdd = true;
    }

    public void addInstr(IrInstr instr) {
        if (canAdd) {
            instrs.add(instr);
            if (instr instanceof IrBrInstr || instr instanceof IrRetInstr) {
                canAdd = false;
            }
        } else {
            //避免mem2reg时alloc找到被删除的load和store
            removeDeadUse(instr);
        }
    }

    public ArrayList<IrInstr> getInstrs() {
        return instrs;
    }

    public IrInstr getLastInstr() {
        int size = instrs.size();
        return instrs.get(size - 1);
    }

    public IrFunction getFunction() {
        return function;
    }

    public void setFunction(IrFunction function) {
        this.function = function;
    }

    public void addPrevBlock(IrBasicBlock prevBlock) {
        prev.add(prevBlock);
    }

    public ArrayList<IrBasicBlock> getPrev() {
        return prev;
    }

    public void addNextBlock(IrBasicBlock nextBlock) {
        next.add(nextBlock);
    }

    public ArrayList<IrBasicBlock> getNext() {
        return next;
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
