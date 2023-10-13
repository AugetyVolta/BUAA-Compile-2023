import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.node.CompUnitNode;
import error.Error;
import symbol.SymbolTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static utils.MyConfig.*;
import static utils.MyIO.writeFile;

public class Compiler {
    public static void main(String[] args) {
        ArrayList<Error> errorList = new ArrayList<>();
        SymbolTable symbolTable = new SymbolTable(null);
        Lexer lexer = new Lexer("testfile.txt");
        ArrayList<Token> tokenList = lexer.getTokenList();
        if (lexicalOutput) {
            lexer.display();
        }
        Parser parser = new Parser(tokenList, errorList);
        CompUnitNode compUnitNode = parser.parseCompUnit();
        if (parseOutput) {
            writeFile("output.txt", compUnitNode.toString());
        }
        if (errorOutput) {
            compUnitNode.checkError(errorList, symbolTable);
            Collections.sort(errorList);
            StringBuilder sb = new StringBuilder();
            for (Error error : errorList) {
                sb.append(error.toString());
            }
            writeFile("error.txt", sb.toString());
        }
    }
}
