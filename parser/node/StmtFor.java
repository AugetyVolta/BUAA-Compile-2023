package parser.node;

import error.Error;
import lexer.token.SyntaxType;
import llvm.IrBasicBlock;
import llvm.IrBuilder;
import llvm.IrValue;
import symbol.SymbolManager;
import symbol.SymbolTable;

import java.util.ArrayList;

public class StmtFor extends StmtEle {
    private TerminalNode forTk;
    private TerminalNode lparent;
    private ForStmtNode forStmt1 = null;
    private TerminalNode semicn1 = null;
    private CondNode cond = null;
    private TerminalNode semicn2 = null;
    private ForStmtNode forStmt2 = null;
    private TerminalNode rparent;

    private StmtNode stmt;

    public StmtFor() {

    }

    public void setForStmt1(ForStmtNode forStmt1) {
        super.addChild(forStmt1);
        this.forStmt1 = forStmt1;
    }

    public void setCond(CondNode cond) {
        super.addChild(cond);
        this.cond = cond;
    }

    public void setForStmt2(ForStmtNode forStmt2) {
        super.addChild(forStmt2);
        this.forStmt2 = forStmt2;
    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.FORTK) {
                forTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                if (semicn1 == null) {
                    semicn1 = (TerminalNode) child;
                } else if (semicn2 == null) {
                    semicn2 = (TerminalNode) child;
                }
            }
        } else if (child instanceof StmtNode) {
            stmt = (StmtNode) child;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(forTk.toString());
        sb.append(lparent.toString());
        if (forStmt1 != null) {
            sb.append(forStmt1.toString());
        }
        sb.append(semicn1.toString());
        if (cond != null) {
            sb.append(cond.toString());
        }
        sb.append(semicn2.toString());
        if (forStmt2 != null) {
            sb.append(forStmt2.toString());
        }
        sb.append(rparent.toString());
        sb.append(stmt.toString());
        return sb.toString();
    }

    //为了去检查break和continue的问题
    @Override
    public void checkError(ArrayList<Error> errorList) {
        SymbolManager.Manager.enterLoop();
        super.checkError(errorList);
        SymbolManager.Manager.leaveLoop();
    }

    @Override
    public IrValue buildIR() {
        IrBasicBlock enterBlock = IrBuilder.IRBUILDER.getCurBasicBlock();
        IrBasicBlock condBlock = IrBuilder.IRBUILDER.buildBasicBlock();
        IrBasicBlock forStmt2Block = IrBuilder.IRBUILDER.buildBasicBlock();
        IrBasicBlock stmtBlock = IrBuilder.IRBUILDER.buildBasicBlock();
        IrBasicBlock afterForBlock = IrBuilder.IRBUILDER.buildBasicBlock();
        //进入for
        IrBuilder.IRBUILDER.enterLoop(forStmt2Block, afterForBlock);
        //构建forStmt1
        IrBuilder.IRBUILDER.setCurBasicBlock(enterBlock);
        if (forStmt1 != null) {
            forStmt1.buildIR();//将forStmt1加入到enterBlock中
        }
        IrBuilder.IRBUILDER.buildBrInstr(condBlock);//不论有没有初始化都应该跳到condBlock
        //构建stmt
        IrBuilder.IRBUILDER.setCurBasicBlock(stmtBlock);
        stmt.buildIR();
        IrBuilder.IRBUILDER.buildBrInstr(forStmt2Block);
        //构建cond
        IrBuilder.IRBUILDER.setCurBasicBlock(condBlock);
        if (cond != null) {
            cond.buildCondIR(stmtBlock, afterForBlock);
        } else {
            IrBuilder.IRBUILDER.buildBrInstr(stmtBlock);//无条件跳转
        }
        //构建forStmt2
        IrBuilder.IRBUILDER.setCurBasicBlock(forStmt2Block);
        if (forStmt2 != null) {//构建
            forStmt2.buildIR();
        }
        IrBuilder.IRBUILDER.buildBrInstr(condBlock);//无条件跳转到条件判断
        //退出循环
        IrBuilder.IRBUILDER.leaveLoop();
        //将当前block设置为afterForBlock
        IrBuilder.IRBUILDER.setCurBasicBlock(afterForBlock);
        return null;
    }
}
