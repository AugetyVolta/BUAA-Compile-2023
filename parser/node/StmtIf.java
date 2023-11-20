package parser.node;

import lexer.token.SyntaxType;
import llvm.IrBasicBlock;
import llvm.IrBuilder;
import llvm.IrValue;
import llvm.instr.IrCallInstr;
import mips.MipsBuilder;

import java.util.ArrayList;

public class StmtIf extends StmtEle {
    private TerminalNode ifTk;

    private TerminalNode lparent;

    private CondNode cond;

    private TerminalNode rparent;

    private ArrayList<StmtNode> stmts = new ArrayList<>();

    private TerminalNode elseTk = null;

    public StmtIf() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.IFTK) {
                ifTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.ELSETK) {
                elseTk = (TerminalNode) child;
            }
        } else if (child instanceof CondNode) {
            cond = (CondNode) child;
        } else if (child instanceof StmtNode) {
            stmts.add((StmtNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ifTk.toString());
        sb.append(lparent.toString());
        sb.append(cond.toString());
        sb.append(rparent.toString());
        sb.append(stmts.get(0).toString());
        if (elseTk != null) {
            sb.append(elseTk.toString());
            sb.append(stmts.get(1).toString());
        }
        return sb.toString();
    }

    @Override
    public IrValue buildIR() {
        //block0 原本所在的block,即cond第一部分应该加在block0
        IrBasicBlock block0 = IrBuilder.IRBUILDER.getCurBasicBlock();
        //block1 then
        IrBasicBlock block1 = IrBuilder.IRBUILDER.buildBasicBlock(false);//构建then block
        //block2 else
        IrBasicBlock block2 = IrBuilder.IRBUILDER.buildBasicBlock(false);//构建else block
        //跳出的块
        IrBasicBlock block3 = IrBuilder.IRBUILDER.buildBasicBlock(false);
        //进入cond,设置跳转,首先把block设置为一开始进来的block
        IrBuilder.IRBUILDER.setCurBasicBlock(block0);
        if (elseTk != null) {
            cond.buildCondIR(block1, block2);//then else
        } else {
            cond.buildCondIR(block1, block3);//then 后面的block
        }
        //构建thenBlock block1 then
        IrBuilder.IRBUILDER.setCurBasicBlock(block1);
        IrBuilder.IRBUILDER.addBasicBlock(block1);
        stmts.get(0).buildIR();
        IrBuilder.IRBUILDER.buildBrInstr(block3); //跳转到block3
        //构建elseBlock block2 else
        if (elseTk != null) {
            IrBuilder.IRBUILDER.setCurBasicBlock(block2);
            IrBuilder.IRBUILDER.addBasicBlock(block2);
            stmts.get(1).buildIR();
            IrBuilder.IRBUILDER.buildBrInstr(block3); //跳转到block3
        }
        //将当前块设置为block3
        IrBuilder.IRBUILDER.setCurBasicBlock(block3);
        IrBuilder.IRBUILDER.addBasicBlock(block3);
        return null;
    }
}
