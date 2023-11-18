package mips;

import llvm.IrConstInt;

import java.util.ArrayList;

public class MipsGlobalData extends MipsValue {
    private String name;
    private int dim;//如果是数字类型的维度
    private int length;//数组类型的长度
    private ArrayList<IrConstInt> initValues;
    private String constString;
    private int dataType;//0是数字或者数组,1是字符串常量

    public MipsGlobalData(String name, int dim, int length, ArrayList<IrConstInt> initValues) {
        this.name = name;
        this.dim = dim;
        this.length = length;
        this.initValues = initValues;
        this.dataType = 0;
    }

    public MipsGlobalData(String name, String constString) {
        this.name = name;
        this.constString = constString;
        this.dataType = 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        if (dataType == 0) {
            int size = initValues.size();
            if (size != 0) {
                sb.append(".word ");
                if (dim == 0) {
                    sb.append(initValues.get(0).getValue());
                } else {
                    for (int i = size - 1; i >= 0; i--) {
                        sb.append(initValues.get(i).getValue());
                        if (i > 0) {
                            sb.append(",");
                        }
                    }
                }
            } else {
                if (dim == 0) {
                    sb.append(".word 0");//给附初值0
                } else {
                    sb.append(".space ").append(length * 4);//开辟空间
                }
            }
        } else {
            sb.append(".asciiz \"");
            sb.append(constString);
            sb.append("\"");
        }
        return sb.toString();
    }
}
