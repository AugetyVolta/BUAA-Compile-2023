package optimizer;

import llvm.IrModule;

import static utils.MyConfig.OpenMem2Reg;

public class Optimizer {
    private IrModule module;

    public Optimizer(IrModule module) {
        this.module = module;
        if (OpenMem2Reg) {
            new Mem2Reg(module);
        }
    }


}
