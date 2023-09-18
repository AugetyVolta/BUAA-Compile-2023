package lexer.token;

public enum SyntaxType {
    /**
     * Reserved word
     * main,const,int,break,continue,if,
     * else,for,getint,printf,return,void
     */
    MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK,
    ELSETK, FORTK, GETINTTK, PRINTFTK, RETURNTK, VOIDTK,
    /**
     * Delimiter
     * ! && ||
     * + - * /
     * < <= > >= == !=
     * = ; , ( ) [ ] { }
     */
    NOT, AND, OR,
    PLUS, MINU, MULT, DIV, MOD,
    LSS, LEQ, GRE, GEQ, EQL, NEQ,
    ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE,
    /**
     * IntConst(number)
     */
    INTCON,
    /**
     * FormatString
     */
    STRCON,
    /**
     * Ident
     */
    IDENFR,
}
