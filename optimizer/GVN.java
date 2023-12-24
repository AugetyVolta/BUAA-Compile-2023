package optimizer;

import llvm.*;
import llvm.instr.*;
import llvm.type.IrIntegetType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GVN {
    private IrModule module;
    private HashMap<String, IrInstr> instrSet = new HashMap<>();

    public GVN(IrModule module) {
        this.module = module;
        gvn();
    }


    public void gvn() {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            instrSet.clear();
            gvnInBlock(function.getBasicBlocks().get(0));
        }
    }


    public void gvnInBlock(IrBasicBlock basicBlock) {
        ArrayList<IrInstr> instrs = new ArrayList<>();
        Iterator<IrInstr> it = basicBlock.getInstrs().iterator();
        while (it.hasNext()) {
            IrInstr instr = it.next();
            if (satisfyCond(instr)) {
                if (!instrSet.containsKey(instr.hash())) {
                    instrs.add(instr);
                    instrSet.put(instr.hash(), instr);
                } else {
                    IrValue value = instrSet.get(instr.hash());
                    replaceAllUse(instr, value);
                    //移除所有的use
                    for (IrUse use : instr.getOperands()) {
                        IrValue usee = use.getIrUsee();
                        usee.removeUse(use);
                    }
                    it.remove();
                }
            }
        }
        for (IrBasicBlock next : basicBlock.getIdoms()) {
            gvnInBlock(next);
        }
        for (IrInstr instr : instrs) {
            instrSet.remove(instr.hash());
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

    public void replaceAllUse(IrValue src, IrValue dst) {
        ArrayList<IrUse> uses = src.getIrUses();
        for (IrUse use : uses) {
            use.setIrUsee(dst);
            //需要把dst的use也连接上
            dst.addUseToUser(use);
        }
        uses.clear();
    }
}
