package parser.node;

import llvm.IrBasicBlock;
import llvm.IrBuilder;

public class LOrExpNode extends Node {
    private String name = "<LOrExp>";

    private LAndExpNode lAndExp = null;

    private TerminalNode operator = null;

    private LOrExpNode lOrExp = null;

    public LOrExpNode() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof LOrExpNode) {
            lOrExp = (LOrExpNode) child;
        } else if (child instanceof LAndExpNode) {
            lAndExp = (LAndExpNode) child;
        } else if (child instanceof TerminalNode) {
            operator = (TerminalNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lOrExp != null) {
            sb.append(lOrExp.toString());
            sb.append(operator.toString());
            sb.append(lAndExp.toString());
        } else if (lAndExp != null) {
            sb.append(lAndExp.toString());
        }
        sb.append(name).append("\n");
        return sb.toString();
    }

    public void buildLOrExpIR(IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        if (lOrExp != null) {  //LOrExp '||' LAndExp
            IrBasicBlock enterForLOrExp = IrBuilder.IRBUILDER.getCurBasicBlock();//进入函数时LOrExp处的block,其实就是LOrExp应该放的位置
            IrBasicBlock enterForLAndExp = IrBuilder.IRBUILDER.buildBasicBlock();
            lAndExp.buildLAndExpIR(trueBlock, falseBlock);
            //将当前的指令块设置为进来时的block,开始构建LOrExp
            IrBuilder.IRBUILDER.setCurBasicBlock(enterForLOrExp);
            lOrExp.buildLOrExpIR(trueBlock, enterForLAndExp);//如果为true跳转到trueBlock,否则到LAndExp
        } else { //LAndExp
            lAndExp.buildLAndExpIR(trueBlock, falseBlock);
        }
    }

}
