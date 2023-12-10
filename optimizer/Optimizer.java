package optimizer;

import llvm.IrModule;

import static utils.MyConfig.OpenMem2Reg;
import static utils.MyIO.writeFile;

public class Optimizer {
    private IrModule module;

    public Optimizer(IrModule module) {
        this.module = module;
        new MergeBlock(module);
        if (OpenMem2Reg) {
            new Mem2Reg(module);
            writeFile("llvm_phi.txt", module.toString());
        }
        new ConstSpread(module);
        new DeadCodeElimination(module);
        new GVN(module);
        new DeadCodeElimination(module);
        if (OpenMem2Reg) {
            new RemovePhi(module);
        }
        writeFile("llvm_move.txt", module.toString());
        new MergeBlock(module);
        new DeadCodeElimination(module);
        new GVN(module);
        writeFile("llvm_merge.txt", module.toString());
    }

}
