package optimizer;

import llvm.*;
import llvm.instr.*;
import llvm.type.IrIntegetType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class DeadCodeElimination {
    private IrModule module;

    public DeadCodeElimination(IrModule module) {
        this.module = module;
        //正向反向都删除一遍
        deleteFromTop(true);
        deleteFromTop(false);
        deleteFromTop(true);
        deleteFromTop(false);
        //删除没有被调用的函数
        removeDeadFunc();
    }


    public void deleteFromTop(boolean reverse) {
        ArrayList<IrFunction> functions = module.getIrFunctions();
        for (IrFunction function : functions) {
            ArrayList<IrBasicBlock> basicBlocks = function.getBasicBlocks();
            if (reverse) {
                Collections.reverse(basicBlocks);
            }
            for (IrBasicBlock basicBlock : basicBlocks) {
                ArrayList<IrInstr> instrs = basicBlock.getInstrs();
                if (reverse) {
                    Collections.reverse(instrs);
                }
                Iterator<IrInstr> it = instrs.iterator();
                while (it.hasNext()) {
                    IrInstr instr = it.next();
                    if (!hasSideAffect(instr) && instr.getIrUses().size() == 0) {
                        it.remove();
                        //移除所有的use
                        for (IrUse use : instr.getOperands()) {
                            IrValue usee = use.getIrUsee();
                            usee.removeUse(use);
                        }
                    }
                }
                //翻转回去
                if (reverse) {
                    Collections.reverse(instrs);
                }
            }
            if (reverse) {
                Collections.reverse(basicBlocks);
            }
        }
    }

    public boolean hasSideAffect(IrInstr instr) {
        if ((instr instanceof IrBrInstr) ||
                (instr instanceof IrCallInstr) ||
                (instr instanceof IrGetPutInstr) ||
                (instr instanceof IrRetInstr) ||
                (instr instanceof IrStoreInstr) //全局变量赋值
        ) {
            return true;
        }
        return false;
    }

    public void removeDeadFunc() {
        Iterator<IrFunction> it = module.getIrFunctions().iterator();
        while (it.hasNext()) {
            IrFunction function = it.next();
            //如果不是main函数且没被调用的函数就要删除
            if (!function.isCalled && !function.getName().equals("@main")) {
                it.remove();
            }
        }
    }
}
