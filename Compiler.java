import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.node.CompUnitNode;

import java.util.ArrayList;

import static utils.MyIO.writeFile;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("testfile.txt");
        ArrayList<Token> tokenList = lexer.getTokenList();
        Parser parser = new Parser(tokenList);
        CompUnitNode compUnitNode = parser.parseCompUnit();
        writeFile("output.txt", compUnitNode.toString());
    }
}
