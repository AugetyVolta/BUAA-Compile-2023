package parser.node;

import lexer.token.SyntaxType;

import java.util.ArrayList;

public class StmtPrintf extends StmtEle {
    private TerminalNode printfTk;

    private TerminalNode lparent;

    private TerminalNode strcon;

    private ArrayList<TerminalNode> commas = new ArrayList<>();

    private ArrayList<ExpNode> exps = new ArrayList<>();
    private TerminalNode rparent;
    private TerminalNode semicn;

    public StmtPrintf() {

    }

    @Override
    public void addChild(Node child) {
        super.addChild(child);
        if (child instanceof TerminalNode) {
            if (((TerminalNode) child).type == SyntaxType.PRINTFTK) {
                printfTk = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.LPARENT) {
                lparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.STRCON) {
                strcon = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.COMMA) {
                commas.add((TerminalNode) child);
            } else if (((TerminalNode) child).type == SyntaxType.RPARENT) {
                rparent = (TerminalNode) child;
            } else if (((TerminalNode) child).type == SyntaxType.SEMICN) {
                semicn = (TerminalNode) child;
            }
        } else if (child instanceof ExpNode) {
            exps.add((ExpNode) child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printfTk.toString());
        sb.append(lparent.toString());
        sb.append(strcon.toString());
        for (int i = 0; i < commas.size(); i++) {
            sb.append(commas.get(i).toString());
            sb.append(exps.get(i).toString());
        }
        sb.append(rparent.toString());
        sb.append(semicn.toString());
        return sb.toString();
    }
}
