package lexer;

import lexer.token.SyntaxType;
import lexer.token.Token;

import java.io.*;
import java.util.*;

public class Lexer {
    //tokenMap
    private static final HashMap<String, SyntaxType> tokenMap = new HashMap<String, SyntaxType>();
    // 用于按序存放tokens
    private final ArrayList<Token> tokenList = new ArrayList<>();
    // 源代码字符串
    private final String sourceCode;
    // 当前的行
    private int curLine;
    //当前的index
    private int curIndex;

    static {
        tokenMap.put("main", SyntaxType.MAINTK);
        tokenMap.put("int", SyntaxType.INTTK);
        tokenMap.put("const", SyntaxType.CONSTTK);
        tokenMap.put("break", SyntaxType.BREAKTK);
        tokenMap.put("continue", SyntaxType.CONTINUETK);
        tokenMap.put("if", SyntaxType.IFTK);
        tokenMap.put("else", SyntaxType.ELSETK);
        tokenMap.put("!", SyntaxType.NOT);
        tokenMap.put("&&", SyntaxType.AND);
        tokenMap.put("||", SyntaxType.OR);
        tokenMap.put("for", SyntaxType.FORTK);
        tokenMap.put("getint", SyntaxType.GETINTTK);
        tokenMap.put("printf", SyntaxType.PRINTFTK);
        tokenMap.put("return", SyntaxType.RETURNTK);
        tokenMap.put("void", SyntaxType.VOIDTK);
        tokenMap.put("+", SyntaxType.PLUS);
        tokenMap.put("-", SyntaxType.MINU);
        tokenMap.put("*", SyntaxType.MULT);
        tokenMap.put("/", SyntaxType.DIV);
        tokenMap.put("%", SyntaxType.MOD);
        tokenMap.put("<", SyntaxType.LSS);
        tokenMap.put("<=", SyntaxType.LEQ);
        tokenMap.put(">", SyntaxType.GRE);
        tokenMap.put(">=", SyntaxType.GEQ);
        tokenMap.put("==", SyntaxType.EQL);
        tokenMap.put("!=", SyntaxType.NEQ);
        tokenMap.put("=", SyntaxType.ASSIGN);
        tokenMap.put(";", SyntaxType.SEMICN);
        tokenMap.put(",", SyntaxType.COMMA);
        tokenMap.put("(", SyntaxType.LPARENT);
        tokenMap.put(")", SyntaxType.RPARENT);
        tokenMap.put("[", SyntaxType.LBRACK);
        tokenMap.put("]", SyntaxType.RBRACK);
        tokenMap.put("{", SyntaxType.LBRACE);
        tokenMap.put("}", SyntaxType.RBRACE);
    }

    public Lexer(String filePath) {
        this.sourceCode = readFile(filePath);
        this.curLine = 1;
        this.curIndex = 0;
        this.run(); //创建对象时就进行词法分析
    }

    public void run() {
        int srcLength = sourceCode.length();
        StringBuilder sb = new StringBuilder();
        for (curIndex = 0; curIndex < srcLength; ) {
            char cur = sourceCode.charAt(curIndex);
            if (cur == ' ') {
                curIndex++;
            } else if (cur == '\n') {
                curIndex++;
                curLine++; //增加行
            } else if (cur == '+') {
                Token tk = new Token(curLine, tokenMap.get("+"), "+");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '-') {
                Token tk = new Token(curLine, tokenMap.get("-"), "-");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '*') {
                Token tk = new Token(curLine, tokenMap.get("*"), "*");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '%') {
                Token tk = new Token(curLine, tokenMap.get("%"), "%");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == ';') {
                Token tk = new Token(curLine, tokenMap.get(";"), ";");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == ',') {
                Token tk = new Token(curLine, tokenMap.get(","), ",");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '(') {
                Token tk = new Token(curLine, tokenMap.get("("), "(");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == ')') {
                Token tk = new Token(curLine, tokenMap.get(")"), ")");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '[') {
                Token tk = new Token(curLine, tokenMap.get("["), "[");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == ']') {
                Token tk = new Token(curLine, tokenMap.get("]"), "]");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '{') {
                Token tk = new Token(curLine, tokenMap.get("{"), "{");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '}') {
                Token tk = new Token(curLine, tokenMap.get("}"), "}");
                tokenList.add(tk);
                curIndex++;
            } else if (cur == '!') {
                if (curIndex < srcLength - 1) { //如果后面还跟有字符
                    curIndex++;
                    char next = sourceCode.charAt(curIndex);
                    if (next == '=') {
                        Token tk = new Token(curLine, tokenMap.get("!="), "!=");
                        tokenList.add(tk);
                        curIndex++;
                    } else {
                        Token tk = new Token(curLine, tokenMap.get("!"), "!");
                        tokenList.add(tk);
                    }
                } else {
                    Token tk = new Token(curLine, tokenMap.get("!"), "!");
                    tokenList.add(tk);
                    curIndex++;
                }
            } else if (cur == '=') {
                if (curIndex < srcLength - 1) { //如果后面还跟有字符
                    curIndex++;
                    char next = sourceCode.charAt(curIndex);
                    if (next == '=') {
                        Token tk = new Token(curLine, tokenMap.get("=="), "==");
                        tokenList.add(tk);
                        curIndex++;
                    } else {
                        Token tk = new Token(curLine, tokenMap.get("="), "=");
                        tokenList.add(tk);
                    }
                } else {
                    Token tk = new Token(curLine, tokenMap.get("="), "=");
                    tokenList.add(tk);
                    curIndex++;
                }
            } else if (cur == '>') {
                if (curIndex < srcLength - 1) { //如果后面还跟有字符
                    curIndex++;
                    char next = sourceCode.charAt(curIndex);
                    if (next == '=') {
                        Token tk = new Token(curLine, tokenMap.get(">="), ">=");
                        tokenList.add(tk);
                        curIndex++;
                    } else {
                        Token tk = new Token(curLine, tokenMap.get(">"), ">");
                        tokenList.add(tk);
                    }
                } else {
                    Token tk = new Token(curLine, tokenMap.get(">"), ">");
                    tokenList.add(tk);
                    curIndex++;
                }
            } else if (cur == '<') {
                if (curIndex < srcLength - 1) { //如果后面还跟有字符
                    curIndex++;
                    char next = sourceCode.charAt(curIndex);
                    if (next == '=') {
                        Token tk = new Token(curLine, tokenMap.get("<="), "<=");
                        tokenList.add(tk);
                        curIndex++;
                    } else {
                        Token tk = new Token(curLine, tokenMap.get("<"), "<");
                        tokenList.add(tk);
                    }
                } else {
                    Token tk = new Token(curLine, tokenMap.get("<"), "<");
                    tokenList.add(tk);
                    curIndex++;
                }
            } else if (cur == '&') {
                if (curIndex < srcLength - 1) { //如果后面还跟有字符
                    curIndex++;
                    char next = sourceCode.charAt(curIndex);
                    if (next == '&') {
                        Token tk = new Token(curLine, tokenMap.get("&&"), "&&");
                        tokenList.add(tk);
                        curIndex++;
                    } else {
                        //TODO：只有一个&，报错
                    }
                } else {
                    //TODO：只有一个&，报错
                }
            } else if (cur == '|') {
                if (curIndex < srcLength - 1) { //如果后面还跟有字符
                    curIndex++;
                    char next = sourceCode.charAt(curIndex);
                    if (next == '|') {
                        Token tk = new Token(curLine, tokenMap.get("||"), "||");
                        tokenList.add(tk);
                        curIndex++;
                    } else {
                        //TODO：只有一个|，报错
                    }
                } else {
                    //TODO：只有一个|，报错
                }
            } else if (cur == '_') {
                sb.append(cur);
                curIndex++;
                if (curIndex < srcLength) {
                    while (curIndex < srcLength) {
                        cur = sourceCode.charAt(curIndex);
                        if ('0' <= cur && cur <= '9' || 'a' <= cur && cur <= 'z' || 'A' <= cur && cur <= 'Z' || cur == '_') {
                            sb.append(cur);
                            curIndex++;
                        } else {
                            break;
                        }
                    }
                    String ident = sb.toString();
                    sb.setLength(0);
                    Token tk = new Token(curLine, SyntaxType.IDENFR, ident);
                    tokenList.add(tk);
                } else {
                    Token tk = new Token(curLine, SyntaxType.IDENFR, "_");
                    tokenList.add(tk);
                }
                sb.setLength(0);//清空stringBuilder
            } else if ('0' <= cur && cur <= '9') {
                sb.append(cur);
                curIndex++;
                if (cur == '0') {
                    if (curIndex < srcLength) {
                        char next = sourceCode.charAt(curIndex);
                        if ('0' <= next && next <= '9') {
                            //TODO:前导0的错误
                        } else {
                            sb.setLength(0);
                            Token tk = new Token(curLine, SyntaxType.INTCON, "0");
                            tokenList.add(tk);
                        }
                    } else {
                        sb.setLength(0);
                        Token tk = new Token(curLine, SyntaxType.INTCON, "0");
                        tokenList.add(tk);
                    }
                } else {
                    while (curIndex < srcLength) {
                        cur = sourceCode.charAt(curIndex);
                        if ('0' <= cur && cur <= '9') {
                            sb.append(cur);
                            curIndex++;
                        } else {
                            if (cur == ' ') { //TODO:这里+-*/也会终止，需要在错误处理时重新弄
                                break;
                            } else {
                                //TODO:错误的数字常量
                            }
                            break;
                        }
                    }
                    String ident = sb.toString();
                    sb.setLength(0);
                    Token tk = new Token(curLine, SyntaxType.INTCON, ident);
                    tokenList.add(tk);
                }
                sb.setLength(0);//清空stringBuilder
            } else if ('a' <= cur && cur <= 'z' || 'A' <= cur && cur <= 'Z') {
                sb.append(cur);
                curIndex++;
                while (curIndex < srcLength) {
                    cur = sourceCode.charAt(curIndex);
                    if ('0' <= cur && cur <= '9' || 'a' <= cur && cur <= 'z' ||
                            'A' <= cur && cur <= 'Z' || cur == '_') {
                        sb.append(cur);
                        curIndex++;
                    } else {
                        if (cur == ' ') { //TODO:和上面一样，可能由运算符终结，分隔符是\n \t \r 空格
                            curIndex++;
                            break;
                        } else {
                            //TODO:错误的标识符
                        }
                        break;
                    }
                }
                String ident = sb.toString();
                sb.setLength(0);
                Token tk = new Token(curLine, tokenMap.getOrDefault(ident, SyntaxType.IDENFR), ident);//如果是标识符，是标识符，不是就是保留字
                tokenList.add(tk);
            } else if (cur == '"') {
                sb.append(cur);
                curIndex++;
                cur = sourceCode.charAt(curIndex);
                while (curIndex < srcLength && cur != '"') {
                    if (cur == 32 || cur == 33 || cur >= 40 && cur <= 126 || cur == '%') {
                        sb.append(cur);
                        if (cur == '%') {
                            if (curIndex < srcLength - 1) {
                                char next = sourceCode.charAt(curIndex + 1);
                                if (next == 'd') {
                                    sb.append('d');
                                    curIndex++;
                                } else {
                                    //TODO:报只有%的错
                                }
                            } else {
                                //TODO:报只有%的错
                            }
                        } else if (cur == '\\') {
                            if (curIndex < srcLength - 1) {
                                char next = sourceCode.charAt(curIndex + 1);
                                if (next == 'n') {
                                    sb.append('n');
                                    curIndex++;
                                } else {
                                    //TODO:报只有\的错
                                }
                            } else {
                                //TODO:报只有\的错
                            }
                        }
                    } else {
                        //TODO:报错有其他字符
                    }
                    curIndex++;
                    if (curIndex < srcLength) {
                        cur = sourceCode.charAt(curIndex);
                    } else {
                        break;
                    }
                }
                if (cur == '"') {
                    curIndex++;
                    sb.append(cur);
                    String formatString = sb.toString();
                    sb.setLength(0);
                    Token tk = new Token(curLine, SyntaxType.STRCON, formatString);
                    tokenList.add(tk);
                } else {
                    //TODO:只有前半“的错误
                }
                sb.setLength(0);
            } else if (cur == '/') {
                curIndex++;
                if (curIndex < srcLength) {
                    char next = sourceCode.charAt(curIndex);
                    if (next == '/') {
                        curIndex++;
                        while (curIndex < srcLength) {
                            cur = sourceCode.charAt(curIndex);
                            if (cur == '\n') {
                                curIndex++;
                                curLine++;
                                break;
                            }
                            curIndex++;
                        }
                    } else if (next == '*') {
                        curIndex++;
                        while (curIndex < srcLength - 1) { //TODO:这应该有一种只有一半/*的注释不全错误
                            cur = sourceCode.charAt(curIndex);
                            next = sourceCode.charAt(curIndex + 1);
                            if (cur == '\n') {
                                curLine++;
                            }
                            if (cur == '*' && next == '/') {
                                curIndex += 2;
                                break;
                            }
                            curIndex++;
                        }
                    } else {
                        Token tk = new Token(curLine, tokenMap.get("/"), "/");
                        tokenList.add(tk);
                    }
                } else {
                    Token tk = new Token(curLine, tokenMap.get("/"), "/");
                    tokenList.add(tk);
                }
            } else {
                curIndex++;
            }
        }
    }

    public Token getToken() {
        return null;
    }

    public boolean isFileEnd() {
        return curIndex < sourceCode.length();
    }

    public void listToken() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Token token : tokenList) {
            stringBuilder.append(token.toString()).append("\n");
        }
        writeFile("output.txt", stringBuilder.toString());
    }

    public String readFile(String filePath) {
        InputStream stream;
        try {
            stream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Scanner reader = new Scanner(stream);
        StringJoiner stringJoiner = new StringJoiner("\n"); //通过\n进行分割
        while (reader.hasNextLine()) {
            stringJoiner.add(reader.nextLine());
        }
        reader.close();
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringJoiner.toString().replace("\r", "");
    }

    public void writeFile(String filePath, String content) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print(content);
        writer.close();
    }
}
