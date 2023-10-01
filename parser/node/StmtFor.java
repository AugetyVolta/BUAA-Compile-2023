package parser.node;

import lexer.token.SyntaxType;

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
        this.forStmt1 = forStmt1;
    }

    public void setCond(CondNode cond) {
        this.cond = cond;
    }

    public void setForStmt2(ForStmtNode forStmt2) {
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
        sb.append(lparent.toString());
        sb.append(stmt.toString());
        return sb.toString();
    }
}
