import lexer.Lexer;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("testfile.txt");
        lexer.listToken();
    }
}
