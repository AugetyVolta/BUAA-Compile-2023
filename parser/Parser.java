package parser;

import lexer.token.SyntaxType;
import lexer.token.Token;
import parser.node.*;

import java.util.ArrayList;

public class Parser {
    private final ArrayList<Token> tokenList;

    private int curIndex;

    private Token curToken;

    public Parser(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.curIndex = 0;
        this.curToken = tokenList.get(this.curIndex);
    }

    public boolean isFinish() {
        return curIndex >= tokenList.size();
    }

    public void nextToken() {
        curIndex++;
        if (!isFinish()) {
            curToken = tokenList.get(curIndex);
        }
    }

    //预读取，以当前为基础的后面第offset个token
    public Token preRead(int offset) {
        if (curIndex + offset < tokenList.size()) {
            return tokenList.get(curIndex + offset);
        }
        return null;
    }

    public CompUnitNode parseCompUnit() {
        CompUnitNode compUnitNode = new CompUnitNode();
        //{Decl}
        while (isDecl()) {
            DeclNode declNode = parseDecl();
            compUnitNode.addChild(declNode);
        }
        //{FuncDef}
        while (isFuncDef()) {
            FuncDefNode funcDefNode = parseFuncDef();
            compUnitNode.addChild(funcDefNode);
        }
        //MainFuncDef
        MainFuncDefNode mainFuncDefNode = parseMainFuncDef();
        compUnitNode.addChild(mainFuncDefNode);
        return compUnitNode;
    }

    public boolean isDecl() {
        if (curToken.getSyntaxType() == SyntaxType.CONSTTK) {
            return true;
        }
        Token preRead1 = preRead(1);
        Token preRead2 = preRead(2);
        if (curToken.getSyntaxType() == SyntaxType.INTTK &&
                preRead1.getSyntaxType() == SyntaxType.IDENFR && (
                preRead2.getSyntaxType() == SyntaxType.COMMA ||
                        preRead2.getSyntaxType() == SyntaxType.SEMICN ||
                        preRead2.getSyntaxType() == SyntaxType.LBRACK ||
                        preRead2.getSyntaxType() == SyntaxType.ASSIGN
        )) {
            return true;
        }
        return false;
    }

    public boolean isFuncDef() {
        Token preRead1 = preRead(1);
        Token preRead2 = preRead(2);
        return preRead1.getSyntaxType() == SyntaxType.IDENFR &&
                preRead2.getSyntaxType() == SyntaxType.LPARENT;
    }

    public DeclNode parseDecl() {
        DeclNode declNode = new DeclNode();
        if (curToken.getSyntaxType() == SyntaxType.CONSTTK) {
            ConstDeclNode constDeclNode = parseConstDecl();
            declNode.addChild(constDeclNode);
        } else {
            VarDeclNode varDeclNode = parseVarDecl();
            declNode.addChild(varDeclNode);
        }
        return declNode;
    }

    public ConstDeclNode parseConstDecl() {
        ConstDeclNode constDeclNode = new ConstDeclNode();
        //const
        TerminalNode constTk = new TerminalNode(curToken);
        constDeclNode.addChild(constTk);
        nextToken();
        //BType
        BTypeNode bTypeNode = parseBType();
        constDeclNode.addChild(bTypeNode);
        //constDef
        ConstDefNode constDefNode = parseConstDef();
        constDeclNode.addChild(constDefNode);
        //{, ConstDef}
        while (curToken.getSyntaxType() == SyntaxType.COMMA) {
            TerminalNode comma = new TerminalNode(curToken);
            nextToken();
            ConstDefNode constDefNode1 = parseConstDef();
            constDeclNode.addChild(comma);
            constDeclNode.addChild(constDefNode1);
        }
        //;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            constDeclNode.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少分号 报错
            return null;
        }
        return constDeclNode;
    }

    public BTypeNode parseBType() {
        //int
        BTypeNode bTypeNode = null;
        if (curToken.getSyntaxType() == SyntaxType.INTTK) {
            bTypeNode = new BTypeNode(curToken);
            nextToken();
        }
        return bTypeNode;
    }

    public ConstDefNode parseConstDef() {
        ConstDefNode constDefNode = new ConstDefNode();
        //ident
        TerminalNode ident = new TerminalNode(curToken);
        constDefNode.addChild(ident);
        nextToken();
        //{ '[' ConstExp ']' }
        while (curToken.getSyntaxType() == SyntaxType.LBRACK) {
            // [
            TerminalNode lbrack = new TerminalNode(curToken);
            constDefNode.addChild(lbrack);
            nextToken();
            // ConstExp
            ConstExpNode constExpNode = parseConstExp();
            constDefNode.addChild(constExpNode);
            // ]
            if (curToken.getSyntaxType() == SyntaxType.RBRACK) {
                TerminalNode rbrack = new TerminalNode(curToken);
                constDefNode.addChild(rbrack);
                nextToken();
            } else {
                //TODO:缺少右]
            }
        }
        //=
        TerminalNode assign = new TerminalNode(curToken);
        constDefNode.addChild(assign);
        nextToken();
        //ConstInitVal
        ConstInitValNode constInitValNode = parseConstInitVal();
        constDefNode.addChild(constInitValNode);
        return constDefNode;
    }

    public ConstExpNode parseConstExp() {
        //AddExp
        ConstExpNode constExpNode = new ConstExpNode();
        AddExpNode addExpNode = parseAddExp();
        constExpNode.addChild(addExpNode);
        return constExpNode;
    }

    public ConstInitValNode parseConstInitVal() {
        ConstInitValNode constInitValNode = new ConstInitValNode();
        if (curToken.getSyntaxType() == SyntaxType.LBRACE) {
            //{
            TerminalNode lbrace = new TerminalNode(curToken);
            constInitValNode.addChild(lbrace);
            nextToken();
            //ConstInitVal { ',' ConstInitVal }
            if (curToken.getSyntaxType() != SyntaxType.RBRACE) {
                ConstInitValNode constInitValNode1 = parseConstInitVal();
                constInitValNode.addChild(constInitValNode1);
                while (curToken.getSyntaxType() == SyntaxType.COMMA) {
                    TerminalNode comma = new TerminalNode(curToken);
                    constInitValNode.addChild(comma);
                    nextToken();
                    ConstInitValNode constInitValNode2 = parseConstInitVal();
                    constInitValNode.addChild(constInitValNode2);
                }
            }
            //}
            if (curToken.getSyntaxType() == SyntaxType.RBRACE) {
                TerminalNode rbrace = new TerminalNode(curToken);
                constInitValNode.addChild(rbrace);
                nextToken();
            } else {
                //TODO:缺少}
            }
        } else {
            //ConstExp
            ConstExpNode constExpNode = parseConstExp();
            constInitValNode.addChild(constExpNode);
        }
        return constInitValNode;
    }

    public VarDeclNode parseVarDecl() {
        VarDeclNode varDeclNode = new VarDeclNode();
        //BType
        BTypeNode bTypeNode = parseBType();
        varDeclNode.addChild(bTypeNode);
        //varDef
        VarDefNode varDefNode = parseVarDef();
        varDeclNode.addChild(varDefNode);
        // { ',' VarDef }
        while (curToken.getSyntaxType() == SyntaxType.COMMA) {
            TerminalNode comma = new TerminalNode(curToken);
            varDeclNode.addChild(comma);
            nextToken();
            VarDefNode varDefNode1 = parseVarDef();
            varDeclNode.addChild(varDefNode1);
        }
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            varDeclNode.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        return varDeclNode;
    }

    public VarDefNode parseVarDef() {
        VarDefNode varDefNode = new VarDefNode();
        //Ident
        TerminalNode ident = new TerminalNode(curToken);
        varDefNode.addChild(ident);
        nextToken();
        //{ '[' ConstExp ']' }
        while (curToken.getSyntaxType() == SyntaxType.LBRACK) {
            // [
            TerminalNode lbrack = new TerminalNode(curToken);
            varDefNode.addChild(lbrack);
            nextToken();
            // ConstExp
            ConstExpNode constExpNode = parseConstExp();
            varDefNode.addChild(constExpNode);
            // ]
            if (curToken.getSyntaxType() == SyntaxType.RBRACK) {
                TerminalNode rbrack = new TerminalNode(curToken);
                varDefNode.addChild(rbrack);
                nextToken();
            } else {
                //TODO:缺少]
            }
        }
        // =
        if (curToken.getSyntaxType() == SyntaxType.ASSIGN) {
            // =
            TerminalNode assign = new TerminalNode(curToken);
            varDefNode.addChild(assign);
            nextToken();
            // InitVal
            InitValNode initValNode = parseInitVal();
            varDefNode.addChild(initValNode);
        }
        return varDefNode;
    }

    public InitValNode parseInitVal() {
        InitValNode initValNode = new InitValNode();
        if (curToken.getSyntaxType() == SyntaxType.LBRACE) {
            // {
            TerminalNode lbrace = new TerminalNode(curToken);
            initValNode.addChild(lbrace);
            nextToken();
            // InitVal { ',' InitVal }
            if (curToken.getSyntaxType() != SyntaxType.RBRACE) {
                InitValNode initValNode1 = parseInitVal();
                initValNode.addChild(initValNode1);
                while (curToken.getSyntaxType() == SyntaxType.COMMA) {
                    TerminalNode comma = new TerminalNode(curToken);
                    initValNode.addChild(comma);
                    nextToken();
                    InitValNode initValNode2 = parseInitVal();
                    initValNode.addChild(initValNode2);
                }
            }
            // }
            if (curToken.getSyntaxType() == SyntaxType.RBRACE) {
                TerminalNode rbrace = new TerminalNode(curToken);
                initValNode.addChild(rbrace);
                nextToken();
            } else {
                //TODO:缺少}
            }
        } else {
            //Exp
            ExpNode expNode = parseExp();
            initValNode.addChild(expNode);
        }
        return initValNode;
    }

    public ExpNode parseExp() {
        //AddExp
        ExpNode expNode = new ExpNode();
        AddExpNode addExpNode = parseAddExp();
        expNode.addChild(addExpNode);
        return expNode;
    }

    public FuncDefNode parseFuncDef() {
        FuncDefNode funcDefNode = new FuncDefNode();
        //FuncType
        FuncTypeNode funcTypeNode = parseFuncType();
        funcDefNode.addChild(funcTypeNode);
        //ident
        TerminalNode ident = new TerminalNode(curToken);
        funcDefNode.addChild(ident);
        nextToken();
        // (
        TerminalNode lparent = new TerminalNode(curToken);
        funcDefNode.addChild(lparent);
        nextToken();
        // FuncFParams
        if (curToken.getSyntaxType() != SyntaxType.RPARENT) {
            FuncFParamsNode funcFParamsNode = parseFuncFParams();
            funcDefNode.addChild(funcFParamsNode);
        }
        // )
        if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
            TerminalNode rparent = new TerminalNode(curToken);
            funcDefNode.addChild(rparent);
            nextToken();
        } else {
            //TODO:缺少右括号
        }
        // Block
        BlockNode blockNode = parseBlock();
        funcDefNode.addChild(blockNode);
        return funcDefNode;
    }

    public FuncTypeNode parseFuncType() {
        FuncTypeNode funcTypeNode = null;
        // void | int
        if (curToken.getSyntaxType() == SyntaxType.VOIDTK ||
                curToken.getSyntaxType() == SyntaxType.INTTK) {
            funcTypeNode = new FuncTypeNode(curToken);
            nextToken();
        }
        return funcTypeNode;
    }

    public FuncFParamsNode parseFuncFParams() {
        FuncFParamsNode funcFParamsNode = new FuncFParamsNode();
        //FuncFParam
        FuncFParamNode funcFParamNode = parseFuncFParam();
        funcFParamsNode.addChild(funcFParamNode);
        // { ',' FuncFParam }
        while (curToken.getSyntaxType() == SyntaxType.COMMA) {
            TerminalNode comma = new TerminalNode(curToken);
            funcFParamsNode.addChild(comma);
            nextToken();
            FuncFParamNode funcFParamNode1 = parseFuncFParam();
            funcFParamsNode.addChild(funcFParamNode1);
        }
        return funcFParamsNode;
    }

    public FuncFParamNode parseFuncFParam() {
        FuncFParamNode funcFParamNode = new FuncFParamNode();
        //BType
        BTypeNode bTypeNode = parseBType();
        funcFParamNode.addChild(bTypeNode);
        //Ident
        TerminalNode ident = new TerminalNode(curToken);
        funcFParamNode.addChild(ident);
        nextToken();
        // '[' ']' { '[' ConstExp ']' }
        if (curToken.getSyntaxType() == SyntaxType.LBRACK) {
            // [
            TerminalNode lbrack = new TerminalNode(curToken);
            funcFParamNode.addChild(lbrack);
            nextToken();
            // ]
            if (curToken.getSyntaxType() == SyntaxType.RBRACK) {
                TerminalNode rbrack = new TerminalNode(curToken);
                funcFParamNode.addChild(rbrack);
                nextToken();
            } else {
                //TODO:缺少]
            }
            // { '[' ConstExp ']' }
            while (curToken.getSyntaxType() == SyntaxType.LBRACK) {
                // [
                TerminalNode lbrack1 = new TerminalNode(curToken);
                funcFParamNode.addChild(lbrack1);
                nextToken();
                // ConstExp
                ConstExpNode constExpNode = parseConstExp();
                funcFParamNode.addChild(constExpNode);
                // ]
                if (curToken.getSyntaxType() == SyntaxType.RBRACK) {
                    TerminalNode rbrack1 = new TerminalNode(curToken);
                    funcFParamNode.addChild(rbrack1);
                    nextToken();
                } else {
                    //TODO:缺少]
                }
            }
            return funcFParamNode;
        } else {
            return funcFParamNode;
        }
    }

    public BlockNode parseBlock() {
        BlockNode blockNode = new BlockNode();
        // {
        TerminalNode lbrace = new TerminalNode(curToken);
        blockNode.addChild(lbrace);
        nextToken();
        // { BlockItem }
        while (curToken.getSyntaxType() != SyntaxType.RBRACE) {
            BlockItemNode blockItemNode = parseBlockItem();
            blockNode.addChild(blockItemNode);
        }
        // }
        if (curToken.getSyntaxType() == SyntaxType.RBRACE) {
            TerminalNode rbrace = new TerminalNode(curToken);
            blockNode.addChild(rbrace);
            nextToken();
        } else {
            //TODO:缺少}
        }
        return blockNode;
    }

    public BlockItemNode parseBlockItem() {
        BlockItemNode blockItemNode = new BlockItemNode();
        if (curToken.getSyntaxType() == SyntaxType.CONSTTK ||
                curToken.getSyntaxType() == SyntaxType.INTTK) {
            DeclNode declNode = parseDecl();
            blockItemNode.addChild(declNode);
        } else {
            StmtNode stmtNode = parseStmt();
            blockItemNode.addChild(stmtNode);
        }
        return blockItemNode;
    }

    public MainFuncDefNode parseMainFuncDef() {
        MainFuncDefNode mainFuncDefNode = new MainFuncDefNode();
        //int
        TerminalNode intTk = new TerminalNode(curToken);
        mainFuncDefNode.addChild(intTk);
        nextToken();
        //main
        TerminalNode mainTk = new TerminalNode(curToken);
        mainFuncDefNode.addChild(mainTk);
        nextToken();
        //(
        TerminalNode lparent = new TerminalNode(curToken);
        mainFuncDefNode.addChild(lparent);
        nextToken();
        //)
        if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
            TerminalNode rparent = new TerminalNode(curToken);
            mainFuncDefNode.addChild(rparent);
            nextToken();
        } else {
            //TODO:缺少)
        }
        //Block
        BlockNode blockNode = parseBlock();
        mainFuncDefNode.addChild(blockNode);
        return mainFuncDefNode;
    }

    public StmtNode parseStmt() {
        StmtNode stmtNode = new StmtNode();
        Token preRead1 = preRead(1);
        if (curToken.getSyntaxType() == SyntaxType.IDENFR &&
                preRead1.getSyntaxType() == SyntaxType.ASSIGN ||
                curToken.getSyntaxType() == SyntaxType.IDENFR &&
                        preRead1.getSyntaxType() == SyntaxType.LBRACK //通过这个判断是否是Exp
        ) { //Assign 和 getint
            //LVal
            LValNode lValNode = parseLVal();
            // =
            TerminalNode assign = new TerminalNode(curToken);
            nextToken();
            if (curToken.getSyntaxType() == SyntaxType.GETINTTK) {
                StmtGetint stmtGetint = new StmtGetint();
                stmtGetint.addChild(lValNode);
                stmtGetint.addChild(assign);
                //getInt
                TerminalNode getIntTk = new TerminalNode(curToken);
                stmtGetint.addChild(getIntTk);
                nextToken();
                // (
                TerminalNode lparent = new TerminalNode(curToken);
                stmtGetint.addChild(lparent);
                nextToken();
                // )
                if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
                    TerminalNode rparent = new TerminalNode(curToken);
                    stmtGetint.addChild(rparent);
                    nextToken();
                } else {
                    //TODO:报错
                }
                // semicn
                if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
                    TerminalNode semicn = new TerminalNode(curToken);
                    stmtGetint.addChild(semicn);
                    nextToken();
                } else {
                    //TODO:缺少;
                }
                stmtNode.addChild(stmtGetint);
            } else {
                StmtAssign stmtAssign = new StmtAssign();
                stmtAssign.addChild(lValNode);
                stmtAssign.addChild(assign);
                //Exp
                ExpNode expNode = parseExp();
                stmtAssign.addChild(expNode);
                //semicn
                if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
                    TerminalNode semicn = new TerminalNode(curToken);
                    stmtAssign.addChild(semicn);
                    nextToken();
                } else {
                    //TODO:缺少;
                }
                stmtNode.addChild(stmtAssign);
            }
        } else if (curToken.getSyntaxType() == SyntaxType.LBRACE) { //Block
            StmtBlock stmtBlock = parseStmtBlock();
            stmtNode.addChild(stmtBlock);
        } else if (curToken.getSyntaxType() == SyntaxType.IFTK) {
            StmtIf stmtIf = parseStmtIf();
            stmtNode.addChild(stmtIf);
        } else if (curToken.getSyntaxType() == SyntaxType.FORTK) {
            StmtFor stmtFor = parseStmtFor();
            stmtNode.addChild(stmtFor);
        } else if (curToken.getSyntaxType() == SyntaxType.BREAKTK) {
            StmtBreak stmtBreak = parseStmtBreak();
            stmtNode.addChild(stmtBreak);
        } else if (curToken.getSyntaxType() == SyntaxType.CONTINUETK) {
            StmtContinue stmtContinue = parseStmtContinue();
            stmtNode.addChild(stmtContinue);
        } else if (curToken.getSyntaxType() == SyntaxType.RETURNTK) {
            StmtReturn stmtReturn = parseStmtReturn();
            stmtNode.addChild(stmtReturn);
        } else if (curToken.getSyntaxType() == SyntaxType.PRINTFTK) {
            StmtPrintf stmtPrintf = parseStmtPrintf();
            stmtNode.addChild(stmtPrintf);
        } else {
            StmtExp stmtExp = parseStmtExp();
            stmtNode.addChild(stmtExp);
        }
        return stmtNode;
    }

    public StmtExp parseStmtExp() {
        StmtExp stmtExp = new StmtExp();
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtExp.addChild(semicn);
            nextToken();
        } else {
            //Exp
            ExpNode expNode = parseExp();
            stmtExp.addChild(expNode);
            // ;
            TerminalNode semicn = new TerminalNode(curToken);
            stmtExp.addChild(semicn);
            nextToken();
        }
        return stmtExp;
    }

    public StmtBlock parseStmtBlock() {
        StmtBlock stmtBlock = new StmtBlock();
        BlockNode blockNode = parseBlock();
        stmtBlock.addChild(blockNode);
        return stmtBlock;
    }

    public StmtIf parseStmtIf() {
        StmtIf stmtIf = new StmtIf();
        // if
        TerminalNode ifTk = new TerminalNode(curToken);
        stmtIf.addChild(ifTk);
        nextToken();
        // (
        TerminalNode lparent = new TerminalNode(curToken);
        stmtIf.addChild(lparent);
        nextToken();
        //cond
        CondNode condNode = parseCond();
        stmtIf.addChild(condNode);
        // )
        if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
            TerminalNode rparent = new TerminalNode(curToken);
            stmtIf.addChild(rparent);
            nextToken();
        } else {
            //TODO:缺少）
        }
        //Stmt
        StmtNode stmtNode = parseStmt();
        stmtIf.addChild(stmtNode);
        // [else stmt]
        if (curToken.getSyntaxType() == SyntaxType.ELSETK) {
            TerminalNode elseTk = new TerminalNode(curToken);
            stmtIf.addChild(elseTk);
            nextToken();
            //Stmt
            stmtNode = parseStmt();
            stmtIf.addChild(stmtNode);
        }
        return stmtIf;
    }

    public StmtFor parseStmtFor() {
        StmtFor stmtFor = new StmtFor();
        //for
        TerminalNode forTk = new TerminalNode(curToken);
        stmtFor.addChild(forTk);
        nextToken();
        // (
        TerminalNode lparent = new TerminalNode(curToken);
        stmtFor.addChild(lparent);
        nextToken();
        // [ForStmt]
        if (curToken.getSyntaxType() != SyntaxType.SEMICN) {
            ForStmtNode forStmtNode = parseForStmt();
            stmtFor.setForStmt1(forStmtNode);
        }
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtFor.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        // [Cond]
        if (curToken.getSyntaxType() != SyntaxType.SEMICN) {
            CondNode condNode = parseCond();
            stmtFor.setCond(condNode);
        }
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtFor.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        // [ForStmt]
        if (curToken.getSyntaxType() != SyntaxType.RPARENT) {
            ForStmtNode forStmtNode = parseForStmt();
            stmtFor.setForStmt2(forStmtNode);
        }
        // )
        if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
            TerminalNode rparent = new TerminalNode(curToken);
            stmtFor.addChild(rparent);
            nextToken();
        } else {
            //TODO:缺少）
        }
        // Stmt
        StmtNode stmtNode = parseStmt();
        stmtFor.addChild(stmtNode);
        return stmtFor;
    }

    public StmtBreak parseStmtBreak() {
        StmtBreak stmtBreak = new StmtBreak();
        //break
        TerminalNode breakTk = new TerminalNode(curToken);
        stmtBreak.addChild(breakTk);
        nextToken();
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtBreak.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        return stmtBreak;
    }

    public StmtContinue parseStmtContinue() {
        StmtContinue stmtContinue = new StmtContinue();
        //continue
        TerminalNode continueTk = new TerminalNode(curToken);
        stmtContinue.addChild(continueTk);
        nextToken();
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtContinue.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        return stmtContinue;
    }

    public StmtReturn parseStmtReturn() {
        StmtReturn stmtReturn = new StmtReturn();
        //return
        TerminalNode returnTk = new TerminalNode(curToken);
        stmtReturn.addChild(returnTk);
        nextToken();
        //[Exp]
        if (curToken.getSyntaxType() != SyntaxType.SEMICN) {
            ExpNode expNode = parseExp();
            stmtReturn.addChild(expNode);
        }
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtReturn.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        return stmtReturn;
    }

    public StmtPrintf parseStmtPrintf() {
        StmtPrintf stmtPrintf = new StmtPrintf();
        //printf
        TerminalNode printfTk = new TerminalNode(curToken);
        stmtPrintf.addChild(printfTk);
        nextToken();
        // (
        TerminalNode lparent = new TerminalNode(curToken);
        stmtPrintf.addChild(lparent);
        nextToken();
        // FormatString
        TerminalNode strcon = new TerminalNode(curToken);
        stmtPrintf.addChild(strcon);
        nextToken();
        // {','Exp}
        while (curToken.getSyntaxType() == SyntaxType.COMMA) {
            TerminalNode comma = new TerminalNode(curToken);
            stmtPrintf.addChild(comma);
            nextToken();
            //Exp
            ExpNode expNode = parseExp();
            stmtPrintf.addChild(expNode);
        }
        // )
        if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
            TerminalNode rparent = new TerminalNode(curToken);
            stmtPrintf.addChild(rparent);
            nextToken();
        } else {
            //TODO:缺少）
        }
        // ;
        if (curToken.getSyntaxType() == SyntaxType.SEMICN) {
            TerminalNode semicn = new TerminalNode(curToken);
            stmtPrintf.addChild(semicn);
            nextToken();
        } else {
            //TODO:缺少;
        }
        return stmtPrintf;
    }


    public ForStmtNode parseForStmt() {
        ForStmtNode forStmtNode = new ForStmtNode();
        //LVal
        LValNode lValNode = parseLVal();
        forStmtNode.addChild(lValNode);
        // =
        TerminalNode assign = new TerminalNode(curToken);
        forStmtNode.addChild(assign);
        nextToken();
        //Exp
        ExpNode expNode = parseExp();
        forStmtNode.addChild(expNode);
        return forStmtNode;
    }

    public CondNode parseCond() {
        CondNode condNode = new CondNode();
        LOrExpNode lOrExpNode = parseLOrExp();
        condNode.addChild(lOrExpNode);
        return condNode;
    }

    public LValNode parseLVal() {
        LValNode lValNode = new LValNode();
        //Ident
        TerminalNode ident = new TerminalNode(curToken);
        lValNode.addChild(ident);
        nextToken();
        //{'[' Exp ']'}
        while (curToken.getSyntaxType() == SyntaxType.LBRACK) {
            TerminalNode lbrack = new TerminalNode(curToken);
            lValNode.addChild(lbrack);
            nextToken();
            // Exp
            ExpNode expNode = parseExp();
            lValNode.addChild(expNode);
            if (curToken.getSyntaxType() == SyntaxType.RBRACK) {
                TerminalNode rbrack = new TerminalNode(curToken);
                lValNode.addChild(rbrack);
                nextToken();
            } else {
                //TODO:缺少]
            }
        }
        return lValNode;
    }

    public PrimaryExpNode parsePrimaryExp() {
        PrimaryExpNode primaryExpNode = new PrimaryExpNode();
        if (curToken.getSyntaxType() == SyntaxType.LPARENT) {
            //(
            TerminalNode lparent = new TerminalNode(curToken);
            primaryExpNode.addChild(lparent);
            nextToken();
            //Exp
            ExpNode expNode = parseExp();
            primaryExpNode.addChild(expNode);
            //)
            if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
                TerminalNode rparent = new TerminalNode(curToken);
                primaryExpNode.addChild(rparent);
                nextToken();
            } else {
                //TODO:缺少）
            }
        } else if (curToken.getSyntaxType() == SyntaxType.IDENFR) {
            LValNode lValNode = parseLVal();
            primaryExpNode.addChild(lValNode);
        } else if (curToken.getSyntaxType() == SyntaxType.INTCON) {
            NumberNode numberNode = parseNumber();
            primaryExpNode.addChild(numberNode);
        }
        return primaryExpNode;
    }

    public NumberNode parseNumber() {
        NumberNode numberNode = null;
        if (curToken.getSyntaxType() == SyntaxType.INTCON) {
            numberNode = new NumberNode(curToken);
            nextToken();
        }
        return numberNode;
    }

    public UnaryExpNode parseUnaryExp() {
        UnaryExpNode unaryExpNode = new UnaryExpNode();
        //一定是PrimaryExp
        if (curToken.getSyntaxType() == SyntaxType.LPARENT ||
                curToken.getSyntaxType() == SyntaxType.INTCON) {
            PrimaryExpNode primaryExpNode = parsePrimaryExp();
            unaryExpNode.addChild(primaryExpNode);
        } else if (curToken.getSyntaxType() == SyntaxType.IDENFR) {
            Token preRead1 = preRead(1);
            if (preRead1.getSyntaxType() == SyntaxType.LPARENT) {
                //Ident
                TerminalNode ident = new TerminalNode(curToken);
                unaryExpNode.addChild(ident);
                nextToken();
                // (
                TerminalNode lparent = new TerminalNode(curToken);
                unaryExpNode.addChild(lparent);
                nextToken();
                // FuncRParams
                if (curToken.getSyntaxType() != SyntaxType.RPARENT) {
                    FuncRParamsNode funcRParamsNode = parseFuncRParams();
                    unaryExpNode.addChild(funcRParamsNode);
                }
                // )
                if (curToken.getSyntaxType() == SyntaxType.RPARENT) {
                    TerminalNode rparent = new TerminalNode(curToken);
                    unaryExpNode.addChild(rparent);
                    nextToken();
                } else {
                    //TODO:缺少）
                }
            } else {
                //LVal
                PrimaryExpNode primaryExpNode = parsePrimaryExp();
                unaryExpNode.addChild(primaryExpNode);
            }
        } else if (curToken.getSyntaxType() == SyntaxType.PLUS ||
                curToken.getSyntaxType() == SyntaxType.MINU ||
                curToken.getSyntaxType() == SyntaxType.NOT) {
            // UnaryOp
            UnaryOpNode unaryOpNode = parseUnaryOp();
            unaryExpNode.addChild(unaryOpNode);
            // UnaryExp
            UnaryExpNode unaryExpNode1 = parseUnaryExp();
            unaryExpNode.addChild(unaryExpNode1);
        }
        return unaryExpNode;
    }

    public UnaryOpNode parseUnaryOp() {
        UnaryOpNode unaryOpNode = null;
        if (curToken.getSyntaxType() == SyntaxType.PLUS ||
                curToken.getSyntaxType() == SyntaxType.MINU ||
                curToken.getSyntaxType() == SyntaxType.NOT) {
            unaryOpNode = new UnaryOpNode(curToken);
            nextToken();
        }
        return unaryOpNode;
    }

    public FuncRParamsNode parseFuncRParams() {
        FuncRParamsNode funcRParamsNode = new FuncRParamsNode();
        //Exp
        ExpNode expNode = parseExp();
        funcRParamsNode.addChild(expNode);
        //{ ',' Exp }
        while (curToken.getSyntaxType() == SyntaxType.COMMA) {
            // ,
            TerminalNode comma = new TerminalNode(curToken);
            funcRParamsNode.addChild(comma);
            nextToken();
            //Exp
            ExpNode expNode1 = parseExp();
            funcRParamsNode.addChild(expNode1);
        }
        return funcRParamsNode;
    }

    public MulExpNode parseMulExp() {
        MulExpNode mulExpNode = new MulExpNode();
        UnaryExpNode unaryExpNode = parseUnaryExp();
        mulExpNode.addChild(unaryExpNode);
        while (curToken.getSyntaxType() == SyntaxType.MULT ||
                curToken.getSyntaxType() == SyntaxType.DIV ||
                curToken.getSyntaxType() == SyntaxType.MOD) {
            TerminalNode operator = new TerminalNode(curToken);
            nextToken();
            unaryExpNode = parseUnaryExp();
            MulExpNode mulExpNode1 = new MulExpNode();
            //向上构建树
            mulExpNode1.addChild(mulExpNode);
            mulExpNode1.addChild(operator);
            mulExpNode1.addChild(unaryExpNode);
            //将mulExpNode1变为新的根节点
            mulExpNode = mulExpNode1;
        }
        return mulExpNode;
    }

    public AddExpNode parseAddExp() {
        AddExpNode addExpNode = new AddExpNode();
        MulExpNode mulExpNode = parseMulExp();
        addExpNode.addChild(mulExpNode);
        while (curToken.getSyntaxType() == SyntaxType.PLUS ||
                curToken.getSyntaxType() == SyntaxType.MINU) {
            TerminalNode operator = new TerminalNode(curToken);
            nextToken();
            mulExpNode = parseMulExp();
            AddExpNode addExpNode1 = new AddExpNode();
            //向上构建树
            addExpNode1.addChild(addExpNode);
            addExpNode1.addChild(operator);
            addExpNode1.addChild(mulExpNode);
            //将addExpNode1变为新的根节点
            addExpNode = addExpNode1;
        }
        return addExpNode;
    }

    public RelExpNode parseRelExp() {
        RelExpNode relExpNode = new RelExpNode();
        AddExpNode addExpNode = parseAddExp();
        relExpNode.addChild(addExpNode);
        while (curToken.getSyntaxType() == SyntaxType.LSS ||
                curToken.getSyntaxType() == SyntaxType.LEQ ||
                curToken.getSyntaxType() == SyntaxType.GRE ||
                curToken.getSyntaxType() == SyntaxType.GEQ) {
            TerminalNode operator = new TerminalNode(curToken);
            nextToken();
            addExpNode = parseAddExp();
            RelExpNode relExpNode1 = new RelExpNode();
            //向上构建树
            relExpNode1.addChild(relExpNode);
            relExpNode1.addChild(operator);
            relExpNode1.addChild(addExpNode);
            //将relExpNode1变为新的根节点
            relExpNode = relExpNode1;
        }
        return relExpNode;
    }

    public EqExpNode parseEqExp() {
        EqExpNode eqExpNode = new EqExpNode();
        RelExpNode relExpNode = parseRelExp();
        eqExpNode.addChild(relExpNode);
        while (curToken.getSyntaxType() == SyntaxType.EQL ||
                curToken.getSyntaxType() == SyntaxType.NEQ) {
            TerminalNode operator = new TerminalNode(curToken);
            nextToken();
            relExpNode = parseRelExp();
            EqExpNode eqExpNode1 = new EqExpNode();
            //向上构建树
            eqExpNode1.addChild(eqExpNode);
            eqExpNode1.addChild(operator);
            eqExpNode1.addChild(relExpNode);
            //将eqExpNode1变为新的根节点
            eqExpNode = eqExpNode1;
        }
        return eqExpNode;
    }

    public LAndExpNode parseLAndExp() {
        LAndExpNode lAndExpNode = new LAndExpNode();
        EqExpNode eqExp = parseEqExp();
        lAndExpNode.addChild(eqExp);
        while (curToken.getSyntaxType() == SyntaxType.AND) {
            TerminalNode operator = new TerminalNode(curToken);
            nextToken();
            eqExp = parseEqExp();
            LAndExpNode lAndExpNode1 = new LAndExpNode();
            //向上构建树
            lAndExpNode1.addChild(lAndExpNode);
            lAndExpNode1.addChild(operator);
            lAndExpNode1.addChild(eqExp);
            //将lAndExpNode1变为新的根节点
            lAndExpNode = lAndExpNode1;
        }
        return lAndExpNode;
    }

    public LOrExpNode parseLOrExp() {
        LOrExpNode lOrExpNode = new LOrExpNode();
        LAndExpNode lAndExpNode = parseLAndExp();
        lOrExpNode.addChild(lAndExpNode);
        while (curToken.getSyntaxType() == SyntaxType.OR) {
            TerminalNode operator = new TerminalNode(curToken);
            nextToken();
            lAndExpNode = parseLAndExp();
            LOrExpNode lOrExpNode1 = new LOrExpNode();
            //向上构建树
            lOrExpNode1.addChild(lOrExpNode);
            lOrExpNode1.addChild(operator);
            lOrExpNode1.addChild(lAndExpNode);
            //将lOrExpNode1变为新的根节点
            lOrExpNode = lOrExpNode1;
        }
        return lOrExpNode;
    }
}
