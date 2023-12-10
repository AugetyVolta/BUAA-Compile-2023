package llvm;

import llvm.instr.*;
import llvm.type.IrValueType;
import mips.MipsBuilder;

import java.util.ArrayList;

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
    private ArrayList<IrBasicBlock> idoms = new ArrayList<>();
    private ArrayList<IrPcopyInstr> PCList = new ArrayList<>();

    public IrBasicBlock(String name, IrFunction function) {
        super(name, IrValueType.BBLOCK);
        this.instrs = new ArrayList<>();
        this.function = function;
        this.canAdd = true;
    }

    public void addPcopy(IrPcopyInstr instr) {
        PCList.add(instr);
    }

    public void setPcopy() {
        for (IrPcopyInstr pcopy : PCList) {
            insertBeforeLast(pcopy);
        }
    }

    public ArrayList<IrPcopyInstr> getPCList() {
        return PCList;
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

    public void insertInHead(IrInstr instr) {
        instrs.add(0, instr);
    }

    public void insertBeforeLast(IrInstr instr) {
        instrs.add(instrs.size() - 1, instr);
    }

    public void insertMove(IrInstr instr) {
        instrs.add(instrs.size() - 1, instr);
    }

    public ArrayList<IrInstr> getInstrs() {
        return instrs;
    }

    public IrInstr getLastInstr() {
        int size = instrs.size();
        return instrs.get(size - 1);
    }

    public void setIdoms(ArrayList<IrBasicBlock> idoms) {
        this.idoms = idoms;
    }

    public ArrayList<IrBasicBlock> getIdoms() {
        return idoms;
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

    public void buildMipsWithoutBr() {
        //构建基本块的label
        MipsBuilder.MIPSBUILDER.buildMipsLabel(getName());
        for (IrInstr instr : instrs) {
            if (!(instr instanceof IrBrInstr)) {
                instr.buildMips();
            } else {
                MipsBuilder.MIPSBUILDER.writeBackAll();
            }
        }
    }
}
