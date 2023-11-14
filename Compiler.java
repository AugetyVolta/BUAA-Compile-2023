import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.node.CompUnitNode;
import error.Error;
import symbol.SymbolManager;
import symbol.SymbolTable;

import java.util.ArrayList;
import java.util.Collections;

import static utils.MyConfig.*;
import static utils.MyIO.writeFile;

public class Compiler {
    public static void main(String[] args) {
        //全局错误表
        ArrayList<Error> errorList = new ArrayList<>();
        //SymbolManager
        SymbolTable symbolTable = SymbolManager.Manager.getCurSymbolTable();
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
            compUnitNode.checkError(errorList);
            Collections.sort(errorList);
            StringBuilder sb = new StringBuilder();
            for (Error error : errorList) {
                sb.append(error.toString());
            }
            writeFile(errorOutputPath, sb.toString());
        }
        //llvm
        if(llvmOutput){

        }
    }
}
