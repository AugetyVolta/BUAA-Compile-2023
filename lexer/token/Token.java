package lexer.token;

public class Token {
    private SyntaxType syntaxType;
    protected String value;
    private int line;

    public Token(int line, SyntaxType syntaxType, String value) {
        this.line = line;
        this.syntaxType = syntaxType;
        this.value = value;
    }

    public SyntaxType getSyntaxType() {
        return syntaxType;
    }

    public void setSyntaxType(SyntaxType syntaxType) {
        this.syntaxType = syntaxType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return syntaxType.toString() + " " + value;
    }
}
