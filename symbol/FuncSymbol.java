package symbol;

import llvm.IrValue;
import llvm.type.IrValueType;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<Integer> dims = new ArrayList<>(); //函数的每一个参数的维度，0常量，1一维数组，2二维数组
    private IrValue llvmValue;

    public FuncSymbol(SymbolType symbolType, DataType dataType, String name, int line) {
        super(symbolType, dataType, name, line);
        llvmValue = null;
    }

    public int getParamNum() {
        return dims.size();
    }

    public void addParam(int dim) {
        dims.add(dim);
    }

    public ArrayList<Integer> getDims() {
        return dims;
    }

    public void setLlvmValue(IrValue irValue) {
        this.llvmValue = irValue;
    }

    public IrValue getLlvmValue() {
        return llvmValue;
    }
}
