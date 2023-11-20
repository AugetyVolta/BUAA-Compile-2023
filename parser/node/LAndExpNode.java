package parser.node;

import llvm.IrBasicBlock;
import llvm.IrBuilder;
import llvm.IrConstInt;
import llvm.IrValue;
import llvm.instr.IrInstrType;
import llvm.type.IrIntegetType;

import javax.swing.*;

public class LAndExpNode extends Node {
    private String name = "<LAndExp>";

    private EqExpNode eqExp = null;

    private LAndExpNode lAndExp = null;

    private TerminalNode operator = null;

    public LAndExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof EqExpNode) {
            eqExp = (EqExpNode) child;
        } else if (child instanceof LAndExpNode) {
            lAndExp = (LAndExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lAndExp != null) {
            sb.append(lAndExp.toString());
            sb.append(operator.toString());
            sb.append(eqExp.toString());
        } else if (eqExp != null) {
            sb.append(eqExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    public void buildLAndExpIR(IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        if (lAndExp != null) { //LAndExp '&&' EqExp
            IrBasicBlock enterForLAndExp = IrBuilder.IRBUILDER.getCurBasicBlock();
            //为EqExp创建新的block
            IrBasicBlock enterForEqExp = IrBuilder.IRBUILDER.buildBasicBlock(false);
            //将当前的指令块设置为进来时的block,开始构建LAndExp
            IrBuilder.IRBUILDER.setCurBasicBlock(enterForLAndExp);
            lAndExp.buildLAndExpIR(enterForEqExp, falseBlock);//如果为true就跳转到EqExp
            //接下来build EqExp
            IrBuilder.IRBUILDER.setCurBasicBlock(enterForEqExp);
            IrBuilder.IRBUILDER.addBasicBlock(enterForEqExp);
        } //EqExp
        IrValue eqExpValue = eqExp.buildIR();
        if (eqExpValue.getType() == IrIntegetType.INT32) {
            IrValue icmpInstr = IrBuilder.IRBUILDER.buildIcmpInstr(IrInstrType.NE, eqExpValue, new IrConstInt(0));
            IrBuilder.IRBUILDER.buildBrInstr(icmpInstr, trueBlock, falseBlock);
        } else {
            IrBuilder.IRBUILDER.buildBrInstr(eqExpValue, trueBlock, falseBlock);
        }
    }
}
