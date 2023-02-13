package frontend.mcode;

import frontend.grammar.entity.GrammarTreeEntity;
import frontend.mcode.AST.*;

import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class MCodeGenerator {
    private ArrayList<WordEntity> wordEntities;
    private int curpos=0;//当前位置
    private CompUnit root;//根节点


    public MCodeGenerator(ArrayList<WordEntity> wordEntities){
        this.wordEntities=wordEntities;
        root=getCompUnit();
    }
    //    编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
    private CompUnit getCompUnit() {
        CompUnit compUnit = new CompUnit();
        while (!(showNextWord(0).getType().equals("VOIDTK")||
                (showNextWord(0).getType().equals("INTTK")&&
                        (showNextWord(1).getType().equals("MAINTK") || showNextWord(1).getType().equals("IDENFR")&& showNextWord(2).getType().equals("LPARENT") )))) {
            compUnit.decls.add(getDecl());
        }
        while ((showNextWord(0).getType().equals("VOIDTK")||showNextWord(0).getType().equals("INTTK")) && showNextWord(1).getType().equals("IDENFR")){
            compUnit.funcDefs.add(getFuncDef());
        }
        compUnit.mainFuncDef=getMainFuncDef();
        return compUnit;
    }
    //    主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
    private MainFuncDef getMainFuncDef() {
        MainFuncDef mainFuncDef = new MainFuncDef();
        for (int i=0;i<4;i++) getNextWord();
        mainFuncDef.block=getBlock();
        return mainFuncDef;
    }
    //    Block → '{' { BlockItem } '}'
    private Block getBlock() {
        Block block=new Block();
        if(showNextWord(0).getWord().equals("{")){
            getNextWord();
            while(!showNextWord(0).getWord().equals("}")){
                block.blockItems.add(getBlockItem());
            }
            getNextWord();
        }
        return block;
    }

    //    BlockItem → Decl | Stmt
    private BlockItem getBlockItem() {
        BlockItem blockItem=new BlockItem();
        if (showNextWord(0).getType().equals("INTTK") || showNextWord(0).getType().equals("CONSTTK")) {
            blockItem.isDecl = true;
            blockItem.decl = getDecl();
        } else {
            blockItem.isDecl = false;
            blockItem.stmt = getStmt();
        }
        return blockItem;
    }
    //    语句 Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖  //    左值表达式 LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
    //      | [Exp] ';' //有无Exp两种情况
    //      | Block
    //      | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
    //      | 'while' '(' Cond ')' Stmt
    //      | 'break' ';' | 'continue' ';'
    //      | 'return' [Exp] ';' // 1.有Exp 2.无Exp
    //      | LVal '=' 'getint''('')'';'
    //      | 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
    private Stmt getStmt() {
        Stmt stmt=new Stmt();
        if (showNextWord(0).getType().equals("LBRACE")) {
            stmt.type="BLOCK";
            stmt.block=getBlock();
            return stmt;
        } else if (showNextWord(0).getType().equals("IFTK")) {
            stmt.type ="IF";
            IfStmt ifStmt=new IfStmt();
            stmt.ifStmt=ifStmt;
            getNextWord();
            getNextWord();
            ifStmt.cond=getCond();
            getNextWord();
            ifStmt.stmt=getStmt();
            if(showNextWord(0).getType().equals("ELSETK")){
                getNextWord();
                ifStmt.elseStmt=getStmt();
            }
        } else if (showNextWord(0).getType().equals("WHILETK")) {
            stmt.type="WHILE";
            WhileStmt whileStmt=new WhileStmt();
            getNextWord();
            getNextWord();
            whileStmt.cond=getCond();
            getNextWord();
            whileStmt.stmt=getStmt();
            stmt.whileStmt=whileStmt;
        } else if (showNextWord(0).getType().equals("BREAKTK")) {
            stmt.type="BREAK";
            BreakStmt breakStmt=new BreakStmt();
            stmt.breakStmt=breakStmt;
            getNextWord();
            getNextWord();
        } else if (showNextWord(0).getType().equals("CONTINUETK")) {
            stmt.type="CONTINUE";
            ContinueStmt continueStmt=new ContinueStmt();
            stmt.continueStmt=continueStmt;
            getNextWord();
            getNextWord();
        } else if (showNextWord(0).getType().equals("RETURNTK")) {
            stmt.type="RETURN";
            ReturnStmt returnStmt=new ReturnStmt();
            getNextWord();
            if (showNextWord(0).getType().equals("SEMICN")) {
                getNextWord();
            } else {
                returnStmt.exp=getExp();
                getNextWord();
            }
            stmt.returnStmt=returnStmt;
            return stmt;
        } else if (showNextWord(0).getType().equals("SEMICN")) {
            getNextWord();
        } else if (showNextWord(0).getType().equals("PRINTFTK")) {
            stmt.type="PRINTF";
            PrintfStmt printfStmt=new PrintfStmt();
            getNextWord();
            getNextWord();
            WordEntity formatString=getNextWord();
            printfStmt.formatString=formatString.getWord();
            while (showNextWord(0).getType().equals("COMMA")) {
                getNextWord();
                printfStmt.exps.add(getExp());
            }
            getNextWord();//)
            getNextWord();//;
            stmt.printfStmt=printfStmt;
            return stmt;
        } else if (showNextWord(0).getType().equals("IDENFR")) {
            int flag1 = 0;
            if (showNextWord( 1).getType().equals("ASSIGN")) {
                flag1 = 1;
            } else if (showNextWord( 1).getWord().equals("(")) {
                flag1 = 2;
            } else if (showNextWord( 1).getWord().equals("[")) {
                int k = 1;
                while (showNextWord(k).getWord().equals("[")) {
                    k++;
                    int level = 1;
                    while (level > 0) {
                        if (showNextWord(k).getWord().equals("[")) level++;
                        else if (showNextWord(k).getWord().equals("]")) level--;
                        k++;
                    }
                }
                if (showNextWord(k).getType().equals("ASSIGN")) flag1 = 1;
                else flag1 = 2;
            } else {
                flag1 = 2;
            }

            if (flag1 == 1) {
                LVal lval= (LVal) getLVal();
                getNextWord();//=
                if (showNextWord(0).getType().equals("GETINTTK")) {
                    stmt.type="GETINT";
                    GetintStmt getintStmt=new GetintStmt();
                    stmt.getintStmt=getintStmt;
                    getintStmt.lVal=lval;
                    getNextWord();
                    getNextWord();
                    getNextWord();
                    getNextWord();
                    return stmt;
                } else {
                    stmt.type="ASSIGN";
                    AssignStmt assignStmt=new AssignStmt();
                    assignStmt.lVal=lval;
                    stmt.assignStmt=assignStmt;
                    assignStmt.exp=getExp();
                    getNextWord();
                    return stmt;
                }
            } else {
                stmt.type="EXP";
                stmt.exp=getExp();
                getNextWord();
                return stmt;
            }
        } else {
            Exp exp=getExp();
            getNextWord();
            return stmt;
        }
        return stmt;
    }

    //    条件表达式 Cond → LOrExp
    public Cond getCond(){
        Cond cond = new Cond();
        cond.lOrExp = getLOrExp();
        return cond;
    }
    //    逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
    public LOrExp getLOrExp() {
        LOrExp lOrExp = new LOrExp();
        lOrExp.lAndExp = getLAndExp();
        while (showNextWord(0).getType().equals("OR")) {
            lOrExp.orOps.add(showNextWord(0));
            getNextWord();
            lOrExp.lAndExps.add(getLAndExp());
        }
        return lOrExp;
    }
    //    逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
    public LAndExp getLAndExp() {
        LAndExp lAndExp = new LAndExp();
        lAndExp.eqExp = getEqExp();
        while (showNextWord(0).getType().equals("AND")) {
            lAndExp.andOps.add(showNextWord(0));
            getNextWord();
            lAndExp.eqExps.add(getEqExp());
        }
        return lAndExp;
    }
    //    相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
    private EqExp getEqExp() {
        EqExp eqExp = new EqExp();
        eqExp.relExp = getRelExp();
        while (showNextWord(0).getType().equals("EQL")||showNextWord(0).getType().equals("NEQ")) {
            eqExp.eqOps.add(showNextWord(0));
            getNextWord();
            eqExp.relExps.add(getRelExp());
        }
        return eqExp;
    }
    //    关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private RelExp getRelExp() {
        RelExp relExp = new RelExp();
        relExp.addExp = getAddExp();
        while (showNextWord(0).getType().equals("LSS")||showNextWord(0).getType().equals("GRE")||showNextWord(0).getType().equals("LEQ")||showNextWord(0).getType().equals("GEQ")) {
            relExp.relOps.add(showNextWord(0));
            getNextWord();
            relExp.addExps.add(getAddExp());
        }
        return relExp;
    }

    //    函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    private FuncDef getFuncDef() {
        FuncDef funcDef = new FuncDef();
        funcDef.funcType = showNextWord(0).getWord();
        getNextWord();
        funcDef.ident=getNextWord();
        getNextWord();//(
        if(!showNextWord(0).getType().equals("RPARENT")){
            FuncFParams funcFParams = getFuncFParams();
            funcDef.funcFParams = funcFParams;
            funcDef.paramsNum = funcFParams.paramsNum;
            funcDef.paramsDimList = funcFParams.paramsDimList;
        }
        getNextWord();//)
        funcDef.block=getBlock();
        return funcDef;
    }
    //函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
    private FuncFParams getFuncFParams() {
        FuncFParams funcFParams = new FuncFParams();
        FuncFParam funcFParam0 = getFuncFParam();
        funcFParams.funcFParam = funcFParam0;
        funcFParams.paramsNum = 1;
        funcFParams.paramsDimList.add(funcFParam0.dim);
        while (showNextWord(0).getType().equals("COMMA")) {
            getNextWord();
            FuncFParam funcFParam = getFuncFParam();
            funcFParams.funcFParamList.add(funcFParam);
            funcFParams.paramsNum++;
            funcFParams.paramsDimList.add(funcFParam.dim);
        }
        return funcFParams;
    }
    //    函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    private FuncFParam getFuncFParam() {
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.btype = getNextWord().getWord();
        funcFParam.ident = getNextWord();
        if (showNextWord(0).getType().equals("LBRACK")) {
            funcFParam.dim++;
            getNextWord();
            getNextWord();//]
            while (showNextWord(0).getType().equals("LBRACK")) {
                funcFParam.dim++;
                getNextWord();
                funcFParam.constExpList.add(getConstExp());
                getNextWord();
            }
        }
        return funcFParam;
    }

    //    声明 Decl → ConstDecl | VarDecl
    private Decl getDecl() {
        Decl decl = new Decl();
        if(showNextWord(0).getType().equals("CONSTTK")){
            decl.isConst = true;
            decl.constDecl = getConstDecl();
        } else {
            decl.isConst = false;
            decl.varDecl = getVarDecl();
        }
        return decl;
    }

    //    常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // 1.花括号内重复0次 2.花括号内重复多次
    private ConstDecl getConstDecl() {
        ConstDecl constDecl = new ConstDecl();
        getNextWord();

        String btype=showNextWord(0).getWord();
        getNextWord();

        constDecl.constDefs.add(getConstDef());
        while (showNextWord(0).getType().equals("COMMA")) {
            getNextWord();
            constDecl.constDefs.add(getConstDef());
        }
        getNextWord();
        return constDecl;
    }
    //    常数定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal // 包含普通变量、一维数组、二维数组共三种情况
    private ConstDef getConstDef() {
        ConstDef constDef = new ConstDef();
        constDef.ident=getNextWord();
        while (showNextWord(0).getType().equals("LBRACK")) {
            constDef.dim++;
            getNextWord();
            constDef.constExps.add(getConstExp());
            getNextWord();                    //]读取
        }
        getNextWord();
        constDef.constInitVal = getConstInitVal();
        return constDef;
    }
    //    常量初值 ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}' // 1.常表达式初值 2.一维数组初值3.二维数组初值
    private ConstInitVal getConstInitVal() {
        ConstInitVal constInitVal = new ConstInitVal();
        if (!showNextWord(0).getType().equals("LBRACE")) {
            constInitVal.constExp=getConstExp();
        } else {
            constInitVal.dim++;
            getNextWord();
            if (showNextWord(0).getType().equals("RBRACE")) {
                getNextWord();
            } else {
                constInitVal.constInitVal=getConstInitVal();
                constInitVal.dim += constInitVal.constInitVal.dim;
                while (showNextWord(0).getType().equals("COMMA")) {
                    getNextWord();
                    constInitVal.constInitVals.add(getConstInitVal());
                }
                getNextWord();
            }
        }
        return constInitVal;
    }
    //变量声明  VarDecl → BType VarDef { ',' VarDef } ';'
    private VarDecl getVarDecl() {
        VarDecl varDecl = new VarDecl();

        String btype=showNextWord(0).getWord();
        getNextWord();

        varDecl.varDefs.add(getVarDef());
        while (showNextWord(0).getType().equals("COMMA")) {
            getNextWord();
            varDecl.varDefs.add(getVarDef());
        }
        getNextWord();
        return varDecl;
    }
    //    变量定义 VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义 | Ident { '[' ConstExp ']' } '=' InitVal
    private VarDef getVarDef() {
        VarDef varDef = new VarDef();
        varDef.ident=getNextWord();
        while (showNextWord(0).getType().equals("LBRACK")) {
            varDef.dim++;
            getNextWord();
            varDef.constExps.add(getConstExp());
            getNextWord();                    //]读取
        }
        if(showNextWord(0).getWord().equals("=")){
            getNextWord();
            varDef.initVal = getInitVal();
        }

        return varDef;
    }
    //    变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'// 1.表达式初值 2.一维数组初值 3.二维数组初值
    private InitVal getInitVal() {
        InitVal initVal = new InitVal();
        if (showNextWord(0).getType().equals("LBRACE")) {
            initVal.dim++;
            getNextWord();
            if (showNextWord(0).getType().equals("RBRACE")) {
                getNextWord();
            } else {
                initVal.initVal=getInitVal();
                initVal.dim += initVal.initVal.dim;
                while (showNextWord(0).getType().equals("COMMA")) {
                    getNextWord();
                    initVal.initVals.add(getInitVal());
                }
                getNextWord();
            }
        } else {
            initVal.exp=getExp();
        }
        return initVal;
    }
    //  常量表达式 ConstExp → AddExp 注：使用的Ident 必须是常量
    private ConstExp getConstExp() {
        ConstExp constExp=new ConstExp();
        constExp.addExp=getAddExp();
        return constExp;
    }
    //    表达式 Exp → AddExp 注：SysY 表达式是int 型表达式
    private Exp getExp() {
        Exp exp=new Exp();
        exp.addExp=getAddExp();
        return exp;
    }
    //    加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp == AddExp → MulExp {('+' | '−') MulExp}
    private AddExp getAddExp() {
        AddExp addExp = new AddExp();
        addExp.mulExp = getMulExp();
        while (showNextWord(0).getType().equals("PLUS")||showNextWord(0).getType().equals("MINU")) {
            WordEntity op = getNextWord();
            addExp.addOps.add(op);
            addExp.mulExps.add(getMulExp());
        }
        return addExp;
    }
    //乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp == MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
    private MulExp getMulExp() {
        MulExp mulExp = new MulExp();
        mulExp.unaryExp = getUnaryExp();
        while (showNextWord(0).getType().equals("MULT")||showNextWord(0).getType().equals("DIV")||showNextWord(0).getType().equals("MOD")) {
            WordEntity op = getNextWord();
            mulExp.mulOps.add(op);
            mulExp.unaryExps.add(getUnaryExp());
        }
        return mulExp;
    }
    //    一元表达式 Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp |UnaryExp → PrimaryExp
    private UnaryExp getUnaryExp() {
        UnaryExp unaryExp=new UnaryExp();
        if (showNextWord(0).getType().equals("IDENFR") &&showNextWord(1).getType().equals("LPARENT")) {
            unaryExp.unaryExpType="IDENT";
            unaryExp.ident=getNextWord();
            getNextWord();
            if (!showNextWord(0).getType().equals("RPARENT")) {
                unaryExp.funcRParams = getFuncRParams();
            }
            getNextWord();//)
        } else if (showNextWord(0).getType().equals("PLUS")||showNextWord(0).getType().equals("MINU")||showNextWord(0).getType().equals("NOT")) {
            unaryExp.unaryExpType="UNARYEXP";
            WordEntity op=getUnaryOp();
            unaryExp.unaryOp = op;
            unaryExp.unaryExp = getUnaryExp();
        } else {
            unaryExp.unaryExpType="PRIMARYEXP";
            unaryExp.primaryExp=getPrimaryExp();
        }
        return unaryExp;
    }
    //    基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number // 三种情况均需覆盖,数值 Number → IntConst // 存在即可
    private PrimaryExp getPrimaryExp() {
        PrimaryExp primaryExp = new PrimaryExp();
        if (showNextWord(0).getType().equals("LPARENT")) {
            primaryExp.primaryExpType="EXP";
            getNextWord();
            primaryExp.exp = getExp();
            getNextWord();
        } else if (showNextWord(0).getType().equals("IDENFR")) {
            primaryExp.primaryExpType="LVal";
            primaryExp.lVal=getLVal();
        } else {
            primaryExp.primaryExpType="NUMBER";
           primaryExp.pNumber=getNumber();
        }
        return primaryExp;
    }
    //    左值表达式 LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
    private LVal getLVal() {
        LVal lVal = new LVal();
        WordEntity ident = getNextWord();
        lVal.ident=ident;
        while (showNextWord(0).getType().equals("LBRACK")) {
            lVal.dim++;
            getNextWord();
            lVal.exps.add(getExp());
            getNextWord();
        }
        return lVal;
    }
    //    数值 Number → IntConst
    private PNumber getNumber() {
        PNumber pNumber = new PNumber();
        pNumber.number=getNextWord();
        return  pNumber;
    }
    //    函数实参表 FuncRParams → Exp { ',' Exp }
    private FuncRParams getFuncRParams() {
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.exp = getExp();
        while (showNextWord(0).getType().equals("COMMA")) {
            getNextWord();
            funcRParams.exps.add(getExp());
        }
        return funcRParams;
    }
    //    单目运算符 UnaryOp → '+' | '−' | '!'
    private WordEntity getUnaryOp() {
        return getNextWord();
    }

    private WordEntity getNextWord() {
        if(curpos>=wordEntities.size()) return new WordEntity("EOF","eof",-1);
        return wordEntities.get(curpos++);
    }
    private WordEntity showNextWord(int i) {
        int next=curpos+i;
        if(next>=wordEntities.size()) return new WordEntity("EOF","eof",-1);
        return wordEntities.get(next);
    }
    public CompUnit getRoot() {
        return root;
    }
}
