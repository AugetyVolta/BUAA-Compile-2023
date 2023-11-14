package llvm;

import llvm.type.IrValueType;

import java.util.ArrayList;

public class IrModule extends IrValue {
    private static ArrayList<String> declares = new ArrayList<>();
    private ArrayList<IrGlobalVariable> irGlobalVariables = new ArrayList<>(); //全局变量
    private ArrayList<IrFunction> irFunctions = new ArrayList<>();//函数声明

    static {
        declares.add("declare i32 @getint()");
        declares.add("declare void @putint(i32)");
        declares.add("declare void @putch(i32)");
        declares.add("declare void @putstr(i8*)");
    }

    public IrModule() {
        super("module", IrValueType.NONE);
    }

    public void addIrGlobalVariable(IrGlobalVariable irGlobalVariable) {
        irGlobalVariables.add(irGlobalVariable);
    }

    public void addIrFunction(IrFunction irFunction) {
        irFunctions.add(irFunction);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : declares) {
            sb.append(s).append("\n");
        }
        sb.append("\n");//空一行
        for (IrGlobalVariable globalVariable : irGlobalVariables) {
            sb.append(globalVariable.toString()).append("\n");
        }
        sb.append("\n");//空一行
        for (IrFunction function : irFunctions) {
            sb.append(function.toString()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

}
