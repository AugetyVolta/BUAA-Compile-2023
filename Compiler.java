import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.node.CompUnitNode;
import error.Error;
import symbol.SymbolTable;

import java.util.ArrayList;
import java.util.Collections;

import static utils.MyConfig.*;
import static utils.MyIO.writeFile;

public class Compiler {
    public static void main(String[] args) {
        //全局错误表
        ArrayList<Error> errorList = new ArrayList<>();
        //顶层符号表
        SymbolTable symbolTable = new SymbolTable(null);
        //lexer
        Lexer lexer = new Lexer(filePath);
        ArrayList<Token> tokenList = lexer.getTokenList();
        if (lexicalOutput) {
            lexer.display();
        }
        //parser
        Parser parser = new Parser(tokenList, errorList);
        CompUnitNode compUnitNode = parser.parseCompUnit();
        if (parseOutput) {
            writeFile(outputPath, compUnitNode.toString());
        }
        //error handler
        if (errorOutput) {
            compUnitNode.checkError(errorList, symbolTable);
            Collections.sort(errorList);
            StringBuilder sb = new StringBuilder();
            for (Error error : errorList) {
                sb.append(error.toString());
            }
            writeFile(errorOutputPath, sb.toString());
        }
    }
}
