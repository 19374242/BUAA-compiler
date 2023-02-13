package frontend.grammar;

import frontend.error.entity.ErrorEntity;
import frontend.grammar.entity.GrammarTreeEntity;
import frontend.word.WordAnalysis;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class GrammarAnalysis {
    private ArrayList<WordEntity> wordEntities;//词法分析结果
    private ArrayList<String> res=new ArrayList<>();//存储输出
    private Integer size;
    private GrammarTreeEntity curGrammarTree;
    private ArrayList<ErrorEntity> errorEntities;
    int curPos=0;
    public static ArrayList<Integer> printfIgnoreLine=new ArrayList<>();
    public GrammarAnalysis(ArrayList<WordEntity> wordEntities){
        errorEntities=new ArrayList<>();
        this.size=wordEntities.size();
        this.wordEntities=wordEntities;
        curGrammarTree=getCompUnit();
    }
//    编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
    public GrammarTreeEntity getCompUnit(){
        GrammarTreeEntity subTree=addIntermediateNode("CompUnit");
        if (wordEntities.get(curPos).getType().equals("VOIDTK")) {
            while (curPos+2<size&&!(wordEntities.get(curPos).getType().equals("INTTK")&&wordEntities.get(curPos+1).getType().equals("MAINTK")&&wordEntities.get(curPos+2).getType().equals("LPARENT"))) {
                subTree.addChildNode(getFuncDef());
            }
        } else if (wordEntities.get(curPos).getType().equals("INTTK")) {
            if (wordEntities.get(curPos+1).getType().equals("IDENFR")&&wordEntities.get(curPos+2).getType().equals("LPARENT")) {
                while (curPos+2<size&&!(wordEntities.get(curPos).getType().equals("INTTK")&&wordEntities.get(curPos+1).getType().equals("MAINTK")&&wordEntities.get(curPos+2).getType().equals("LPARENT"))) {
                    subTree.addChildNode(getFuncDef());
                }
            } else {
                while (curPos+2<size&& !((wordEntities.get(curPos+1).getType().equals("MAINTK")
                        ||wordEntities.get(curPos+1).getType().equals("IDENFR"))
                        &&wordEntities.get(curPos+2).getType().equals("LPARENT"))) {
                    subTree.addChildNode(getDecl());
                }
                while (curPos+2<size&&!(wordEntities.get(curPos).getType().equals("INTTK")&&wordEntities.get(curPos+1).getType().equals("MAINTK")&&wordEntities.get(curPos+2).getType().equals("LPARENT"))) {
                    subTree.addChildNode(getFuncDef());
                }
            }
        } else {
            while (curPos+2<size&& !((wordEntities.get(curPos+1).getType().equals("MAINTK")
                    ||wordEntities.get(curPos+1).getType().equals("IDENFR"))
                    &&wordEntities.get(curPos+2).getType().equals("LPARENT"))) {
                subTree.addChildNode(getDecl());
            }
            while (curPos+2<size&&!(wordEntities.get(curPos).getType().equals("INTTK")&&wordEntities.get(curPos+1).getType().equals("MAINTK")&&wordEntities.get(curPos+2).getType().equals("LPARENT"))) {
                subTree.addChildNode(getFuncDef());
            }
        }
        subTree.addChildNode(getMainFuncDef());
        addGrammarAnalysisPrintf("CompUnit");
        return subTree;
    }
//    声明 Decl → ConstDecl | VarDecl
    GrammarTreeEntity getDecl() {
        GrammarTreeEntity subtree=addIntermediateNode("Decl");
        if (wordEntities.get(curPos).getType().equals("INTTK")) {
            subtree.addChildNode(getVarDecl());
        } else {
            subtree.addChildNode(getConstDecl());
        }
        return subtree;
    }
//    函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    GrammarTreeEntity getFuncDef() {
        GrammarTreeEntity subtree=addIntermediateNode("FuncDef");
        subtree.addChildNode(getFuncType());
        subtree.addChildNodes(addFinalNodes(2));//ident (
        if (wordEntities.get(curPos).getType().equals("INTTK")) subtree.addChildNode(getFuncFParams());
        if (wordEntities.get(curPos).getType().equals("RPARENT")) subtree.addChildNodes(addFinalNodes(1));
        else{
            errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"j"));//错误处理
            WordEntity wordEntity=new WordEntity("RPARENT",")",wordEntities.get(curPos-1).getLine());
            subtree.addChildNode(new GrammarTreeEntity("RPARENT",true,wordEntity));
        }
        subtree.addChildNode(getBlock());
        addGrammarAnalysisPrintf("FuncDef");
        return subtree;
    }
    //   函数类型 FuncType → 'void' | 'int'
    GrammarTreeEntity getFuncType() {
        GrammarTreeEntity subtree=addIntermediateNode("FuncType");
        subtree.addChildNodes(addFinalNodes(1));//int/void
        addGrammarAnalysisPrintf("FuncType");
        return subtree;
    }
//    函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
    GrammarTreeEntity getFuncFParams() {
        GrammarTreeEntity subtree=addIntermediateNode("FuncFParams");
        subtree.addChildNode(getFuncFParam());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("COMMA")) {
            subtree.addChildNodes(addFinalNodes(1));//,
            subtree.addChildNode(getFuncFParam());
        }
        addGrammarAnalysisPrintf("FuncFParams");
        return subtree;
    }
//    函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    GrammarTreeEntity getFuncFParam() {
        GrammarTreeEntity subtree=addIntermediateNode("FuncFParam");
        subtree.addChildNodes(addFinalNodes(2));//BType Ident
        if (wordEntities.get(curPos).getType().equals("LBRACK")) {
            subtree.addChildNodes(addFinalNodes(1));//[
            if (wordEntities.get(curPos).getType().equals("RBRACK")) subtree.addChildNodes(addFinalNodes(1));//]
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"k"));//错误处理
                WordEntity wordEntity=new WordEntity("RBRACK","]",wordEntities.get(curPos-1).getLine());
                subtree.addChildNode(new GrammarTreeEntity("RBRACK",true,wordEntity));
            }
            if (wordEntities.get(curPos).getType().equals("LBRACK")) {
                subtree.addChildNodes(addFinalNodes(1));//[
                subtree.addChildNode(getConstExp());
                if (wordEntities.get(curPos).getType().equals("RBRACK")) subtree.addChildNodes(addFinalNodes(1));//]
                else {
                    errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"k"));//错误处理
                    WordEntity wordEntity=new WordEntity("RBRACK","]",wordEntities.get(curPos-1).getLine());
                    subtree.addChildNode(new GrammarTreeEntity("RBRACK",true,wordEntity));
                }
            }
        }
        addGrammarAnalysisPrintf("FuncFParam");
        return subtree;
    }
//    主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
    public GrammarTreeEntity getMainFuncDef(){
        GrammarTreeEntity subTree=addIntermediateNode("MainFuncDef");
        subTree.addChildNodes(addFinalNodes(4));//int main ()
        subTree.addChildNode(getBlock());
        addGrammarAnalysisPrintf("MainFuncDef");
        return subTree;
    }
//    Block → '{' { BlockItem } '}'
//    BlockItem → Decl | Stmt
    public GrammarTreeEntity getBlock(){
        GrammarTreeEntity subTree=addIntermediateNode("Block");
        subTree.addChildNodes(addFinalNodes(1));//{
        while (!wordEntities.get(curPos).getType().equals("RBRACE")&&curPos<size) {
            if (wordEntities.get(curPos).getType().equals("CONSTTK")) {
                subTree.addChildNode(getDecl());
            } else if(wordEntities.get(curPos).getType().equals("INTTK")){
                subTree.addChildNode(getDecl());
            } else {
                subTree.addChildNode(getStmt());
            }
        }
        subTree.addChildNodes(addFinalNodes(1));//}
        addGrammarAnalysisPrintf("Block");
        return subTree;
    }
//    语句 Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
//      | [Exp] ';' //有无Exp两种情况
//      | Block
//      | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
//      | 'while' '(' Cond ')' Stmt
//      | 'break' ';' | 'continue' ';'
//      | 'return' [Exp] ';' // 1.有Exp 2.无Exp
//      | LVal '=' 'getint''('')'';'
//      | 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
    GrammarTreeEntity getStmt() {
        GrammarTreeEntity subTree=addIntermediateNode("Stmt");
        if (wordEntities.get(curPos).getType().equals("LBRACE")) {
            subTree.addChildNode(getBlock());
        } else if (wordEntities.get(curPos).getType().equals("IFTK")) {
            subTree.addChildNodes(addFinalNodes(2));//if (
            subTree.addChildNode(getCond());
            if (wordEntities.get(curPos).getType().equals("RPARENT")) subTree.addChildNodes(addFinalNodes(1));
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"j"));//错误处理
                WordEntity wordEntity=new WordEntity("RPARENT",")",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RPARENT",true,wordEntity));
            }
            subTree.addChildNode(getStmt());
            if (wordEntities.get(curPos).getType().equals("ELSETK")) {
                subTree.addChildNodes(addFinalNodes(1));//else
                subTree.addChildNode(getStmt());
            }
        } else if (wordEntities.get(curPos).getType().equals("WHILETK")) {
            subTree.addChildNodes(addFinalNodes(2));//while (
            subTree.addChildNode(getCond());
            if (wordEntities.get(curPos).getType().equals("RPARENT")) subTree.addChildNodes(addFinalNodes(1));
            else{
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"j"));//错误处理
                WordEntity wordEntity=new WordEntity("RPARENT",")",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RPARENT",true,wordEntity));
            }
            subTree.addChildNode(getStmt());
        } else if (wordEntities.get(curPos).getType().equals("BREAKTK")||wordEntities.get(curPos).getType().equals("CONTINUETK")) {
            subTree.addChildNodes(addFinalNodes(1)); //break and continue
            if(wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1)); //;
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
                WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
            }
        } else if (wordEntities.get(curPos).getType().equals("RETURNTK")) {
            subTree.addChildNodes(addFinalNodes(1));//return
            if (wordEntities.get(curPos).getType().equals("PLUS") || wordEntities.get(curPos).getType().equals("MINU") || wordEntities.get(curPos).getType().equals("NOT") || wordEntities.get(curPos).getType().equals("INTCON") || wordEntities.get(curPos).getType().equals("LPARENT") || wordEntities.get(curPos).getType().equals("IDENFR")) {
                subTree.addChildNode(getExp());
            }
            if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1)); //;
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
                WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
            }
        } else if (wordEntities.get(curPos).getType().equals("PRINTFTK")) {
            subTree.addChildNodes(addFinalNodes(2));//printf (
            subTree.addChildNodes(addFinalNodes(1));//string
            while (wordEntities.get(curPos).getType().equals("COMMA")) {
                subTree.addChildNodes(addFinalNodes(1));//;
                subTree.addChildNode(getExp());
            }
            if (wordEntities.get(curPos).getType().equals("RPARENT")) subTree.addChildNodes(addFinalNodes(1)); //)
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"j"));//错误处理
                WordEntity wordEntity=new WordEntity("RPARENT",")",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RPARENT",true,wordEntity));
            }
            if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1)); //;
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
                WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
            }
        } else if (wordEntities.get(curPos).getType().equals("SEMICN")) {
            subTree.addChildNodes(addFinalNodes(1));//;
        } else {
            int preSize = res.size(),prePos = curPos;
            GrammarTreeEntity lval = getLVal();
            if (wordEntities.get(curPos).getType().equals("ASSIGN")) {
                subTree.addChildNode(lval);
                subTree.addChildNodes(addFinalNodes(1));//=
                if (wordEntities.get(curPos).getType().equals("GETINTTK")) {
                    subTree.addChildNodes(addFinalNodes(2));//getint (
                    if (wordEntities.get(curPos).getType().equals("RPARENT")) subTree.addChildNodes(addFinalNodes(1));//)
                    else {
                        errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"j"));//错误处理
                        WordEntity wordEntity=new WordEntity("RPARENT",")",wordEntities.get(curPos-1).getLine());
                        subTree.addChildNode(new GrammarTreeEntity("RPARENT",true,wordEntity));
                    }
                    if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1));//;
                    else {
                        errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
                        WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
                        subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
                    }
                } else {
                    subTree.addChildNode(getExp());
                    if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1));//;
                    else {
                        errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
                        WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
                        subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
                    }
                }
            } else {
                if (res.size()>preSize) {
                    int nowSize=res.size();
                    res.subList(preSize,nowSize).clear();
                }
                curPos=prePos;
                subTree.addChildNode(getExp());
                if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1));//;
                else {
                    errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
                    WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
                    subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
                }
            }
        }
        addGrammarAnalysisPrintf("Stmt");
        return subTree;
    }
//    条件表达式 Cond → LOrExp
    public GrammarTreeEntity getCond(){
        GrammarTreeEntity subTree=addIntermediateNode("Cond");
        subTree.addChildNode(getLOrExp());
        addGrammarAnalysisPrintf("Cond");
        return subTree;
    }
    //    逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
    public GrammarTreeEntity getLOrExp(){
        GrammarTreeEntity subTree=addIntermediateNode("LOrExp");
        subTree.addChildNode(getLAndExp());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("OR")) {
            addGrammarAnalysisPrintf("LOrExp");
            subTree.addChildNodes(addFinalNodes(1));//||
            subTree.addChildNode(getLAndExp());
        }
        addGrammarAnalysisPrintf("LOrExp");
        return subTree;
    }
    //    逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
    public GrammarTreeEntity getLAndExp(){
        GrammarTreeEntity subTree=addIntermediateNode("LAndExp");
        subTree.addChildNode(getEqExp());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("AND")) {
            addGrammarAnalysisPrintf("LAndExp");
            subTree.addChildNodes(addFinalNodes(1));//||
            subTree.addChildNode(getEqExp());
        }
        addGrammarAnalysisPrintf("LAndExp");
        return subTree;
    }
    //    相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
    public GrammarTreeEntity getEqExp(){
        GrammarTreeEntity subTree=addIntermediateNode("EqExp");
        subTree.addChildNode(getRelExp());
        while (curPos<size&&(wordEntities.get(curPos).getType().equals("EQL")||wordEntities.get(curPos).getType().equals("NEQ"))) {
            addGrammarAnalysisPrintf("EqExp");
            subTree.addChildNodes(addFinalNodes(1));//==或!=
            subTree.addChildNode(getRelExp());
        }
        addGrammarAnalysisPrintf("EqExp");
        return subTree;
    }
    //    关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    public GrammarTreeEntity getRelExp(){
        GrammarTreeEntity subTree=addIntermediateNode("RelExp");
        subTree.addChildNode(getAddExp());
        while (curPos<size&&(wordEntities.get(curPos).getType().equals("LSS")||wordEntities.get(curPos).getType().equals("LEQ")||wordEntities.get(curPos).getType().equals("GEQ")||wordEntities.get(curPos).getType().equals("GRE"))) {
            addGrammarAnalysisPrintf("RelExp");
            subTree.addChildNodes(addFinalNodes(1));//</>/<=/>=
            subTree.addChildNode(getAddExp());
        }
        addGrammarAnalysisPrintf("RelExp");
        return subTree;
    }
//    常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // 1.花括号内重复0次 2.花括号内重复多次
    public GrammarTreeEntity getConstDecl(){
        GrammarTreeEntity subTree=addIntermediateNode("ConstDecl");
        subTree.addChildNodes(addFinalNodes(2));//const BType(int)
        subTree.addChildNode(getConstDef());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("COMMA")) {
            subTree.addChildNodes(addFinalNodes(1));
            subTree.addChildNode(getConstDef());
        }
        if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1));//读取;
        else {
            errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
            WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
            subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
        }
        addGrammarAnalysisPrintf("ConstDecl");
        return subTree;
    }
//    常数定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal // 包含普通变量、一维数组、二维数组共三种情况
    public GrammarTreeEntity getConstDef(){
        GrammarTreeEntity subTree=addIntermediateNode("ConstDef");
        subTree.addChildNodes(addFinalNodes(1));//ident
        while (curPos<size&&!wordEntities.get(curPos).getType().equals("ASSIGN")) {
            subTree.addChildNodes(addFinalNodes(1));//[
            subTree.addChildNode(getConstExp());
            if (wordEntities.get(curPos).getType().equals("RBRACK")) subTree.addChildNodes(addFinalNodes(1));
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"k"));//错误处理
                WordEntity wordEntity=new WordEntity("RBRACK","]",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RBRACK",true,wordEntity));
            }
        }
        subTree.addChildNodes(addFinalNodes(1));//=
        subTree.addChildNode(getConstInitVal());
        addGrammarAnalysisPrintf("ConstDef");
        return subTree;
    }
//    常量初值 ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}' // 1.常表达式初值 2.一维数组初值3.二维数组初值
    public GrammarTreeEntity getConstInitVal(){
        GrammarTreeEntity subTree=addIntermediateNode("ConstInitVal");
        if(!(curPos<size&&wordEntities.get(curPos).getType().equals("LBRACE"))){
            subTree.addChildNode(getConstExp());
        } else {
            subTree.addChildNodes(addFinalNodes(1));//{
            if (!wordEntities.get(curPos).getType().equals("RBRACE")) {
                subTree.addChildNode(getConstInitVal());
                while (wordEntities.get(curPos).getType().equals("COMMA")) {
                    subTree.addChildNodes(addFinalNodes(1));//,
                    subTree.addChildNode(getConstInitVal());
                }
            }
            subTree.addChildNodes(addFinalNodes(1));//}
        }
        addGrammarAnalysisPrintf("ConstInitVal");
        return subTree;
    }
//  变量声明  VarDecl → BType VarDef { ',' VarDef } ';'
    public GrammarTreeEntity getVarDecl(){
        GrammarTreeEntity subTree=addIntermediateNode("VarDecl");
        subTree.addChildNodes(addFinalNodes(1));//int(BType)
        subTree.addChildNode(getVarDef());
        //        遇到,
        while (curPos<size&&wordEntities.get(curPos).getType().equals("COMMA")) {
            subTree.addChildNodes(addFinalNodes(1));
            subTree.addChildNode(getVarDef());
        }
        if (wordEntities.get(curPos).getType().equals("SEMICN")) subTree.addChildNodes(addFinalNodes(1));//读取;
        else {
            errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"i"));//错误处理
            WordEntity wordEntity=new WordEntity("SEMICN",";",wordEntities.get(curPos-1).getLine());
            subTree.addChildNode(new GrammarTreeEntity("SEMICN",true,wordEntity));
        }
        addGrammarAnalysisPrintf("VarDecl");
        return subTree;
    }
//    变量定义 VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义 | Ident { '[' ConstExp ']' } '=' InitVal
    public GrammarTreeEntity getVarDef(){
        GrammarTreeEntity subTree=addIntermediateNode("VarDef");
        subTree.addChildNodes(addFinalNodes(1));//int a的a
//        当不是=,;就是数组a[x]
        while (curPos<size&&!wordEntities.get(curPos).getType().equals("ASSIGN")&&!wordEntities.get(curPos).getType().equals("SEMICN")&&!wordEntities.get(curPos).getType().equals("COMMA")) {
            subTree.addChildNodes(addFinalNodes(1));//[
            subTree.addChildNode(getConstExp());//数字
            if (wordEntities.get(curPos).getType().equals("RBRACK")) subTree.addChildNodes(addFinalNodes(1));//读取]
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"k"));//错误处理
                WordEntity wordEntity=new WordEntity("RBRACK","]",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RBRACK",true,wordEntity));
            }
        }
        if (!wordEntities.get(curPos).getType().equals("ASSIGN")) {
            addGrammarAnalysisPrintf("VarDef");
            return subTree;
        } else {
            subTree.addChildNodes(addFinalNodes(1));//=
            subTree.addChildNode(getInitVal());
        }
        addGrammarAnalysisPrintf("VarDef");
        return subTree;
    }
//    变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'// 1.表达式初值 2.一维数组初值 3.二维数组初值
    public GrammarTreeEntity getInitVal(){
        GrammarTreeEntity subTree=addIntermediateNode("InitVal");
        if(!wordEntities.get(curPos).getType().equals("LBRACE")){
            subTree.addChildNode(getExp());
        } else {
            subTree.addChildNodes(addFinalNodes(1));//{
            if (!wordEntities.get(curPos).getType().equals("RBRACE")) {
                subTree.addChildNode(getInitVal());
                while (wordEntities.get(curPos).getType().equals("COMMA")) {
                    subTree.addChildNodes(addFinalNodes(1));//,
                    subTree.addChildNode(getInitVal());
                }
            }
            subTree.addChildNodes(addFinalNodes(1));//}
        }
        addGrammarAnalysisPrintf("InitVal");
        return subTree;
    }
//    表达式 Exp → AddExp 注：SysY 表达式是int 型表达式
    public GrammarTreeEntity getExp(){
        GrammarTreeEntity subTree=addIntermediateNode("Exp");
        subTree.addChildNode(getAddExp());
        addGrammarAnalysisPrintf("Exp");
        return subTree;
    }
//  常量表达式 ConstExp → AddExp 注：使用的Ident 必须是常量
    public GrammarTreeEntity getConstExp(){
        GrammarTreeEntity subTree=addIntermediateNode("ConstExp");
        subTree.addChildNode(getAddExp());
        addGrammarAnalysisPrintf("ConstExp");
        return subTree;
    }
//    加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp == AddExp → MulExp {('+' | '−') MulExp}
    public GrammarTreeEntity getAddExp(){
        GrammarTreeEntity subTree=addIntermediateNode("AddExp");
        subTree.addChildNode(getMulExp());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("PLUS")||wordEntities.get(curPos).getType().equals("MINU")) {
            addGrammarAnalysisPrintf("AddExp");
            subTree.addChildNodes(addFinalNodes(1));//读取3+2的+
            subTree.addChildNode(getMulExp());
        }
        addGrammarAnalysisPrintf("AddExp");
        return subTree;
    }
//    乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp == MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
    public GrammarTreeEntity getMulExp(){
        GrammarTreeEntity subTree=addIntermediateNode("MulExp");
        subTree.addChildNode(getUnaryExp());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("MULT")||wordEntities.get(curPos).getType().equals("DIV")||wordEntities.get(curPos).getType().equals("MOD")) {
            addGrammarAnalysisPrintf("MulExp");
            subTree.addChildNodes(addFinalNodes(1));//读取3+2的+
            subTree.addChildNode(getUnaryExp());
        }
        addGrammarAnalysisPrintf("MulExp");
        return subTree;
    }
//    一元表达式 Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp |UnaryExp → PrimaryExp
    public GrammarTreeEntity getUnaryExp(){
        GrammarTreeEntity subTree=addIntermediateNode("UnaryExp");
        if (curPos<size&&wordEntities.get(curPos).getType().equals("IDENFR")&&wordEntities.get(curPos+1).getType().equals("LPARENT")) {
            subTree.addChildNodes(addFinalNodes(2));//ident (
            if (wordEntities.get(curPos).getType().equals("IDENFR")||wordEntities.get(curPos).getType().equals("INTCON")||wordEntities.get(curPos).getType().equals("PLUS")||wordEntities.get(curPos).getType().equals("MINU")) subTree.addChildNode(getFuncRParams());
            if (wordEntities.get(curPos).getType().equals("RPARENT")) subTree.addChildNodes(addFinalNodes(1));
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"j"));//错误处理
                WordEntity wordEntity=new WordEntity("RPARENT",")",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RPARENT",true,wordEntity));
            }
        } else if(curPos<size&&(wordEntities.get(curPos).getType().equals("PLUS")||wordEntities.get(curPos).getType().equals("MINU")||wordEntities.get(curPos).getType().equals("NOT"))){
            subTree.addChildNode(getUnaryOp());
            subTree.addChildNode(getUnaryExp());
        } else {
            subTree.addChildNode(getPrimaryExp());
        }
        addGrammarAnalysisPrintf("UnaryExp");
        return subTree;
    }
//    单目运算符 UnaryOp → '+' | '−' | '!'
    public GrammarTreeEntity getUnaryOp(){
        GrammarTreeEntity subTree=addIntermediateNode("UnaryOp");
        subTree.addChildNodes(addFinalNodes(1));
        addGrammarAnalysisPrintf("UnaryOp");
        return subTree;
    }
//    基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number // 三种情况均需覆盖,数值 Number → IntConst // 存在即可
    public GrammarTreeEntity getPrimaryExp(){
        GrammarTreeEntity subTree=addIntermediateNode("PrimaryExp");
        if (wordEntities.get(curPos).getType().equals("LPARENT")) {
            subTree.addChildNodes(addFinalNodes(1));//(
            subTree.addChildNode(getExp());
            subTree.addChildNodes(addFinalNodes(1));//(
        } else if (wordEntities.get(curPos).getType().equals("IDENFR")) {
            subTree.addChildNode(getLVal());
        } else {
            subTree.addChildNode(getNumber());
        }
        addGrammarAnalysisPrintf("PrimaryExp");
        return subTree;
    }
//    左值表达式 LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
    public GrammarTreeEntity getLVal(){
        GrammarTreeEntity subTree=addIntermediateNode("LVal");
        subTree.addChildNodes(addFinalNodes(1));//ident
        while (curPos<size&&wordEntities.get(curPos).getType().equals("LBRACK")) {
            subTree.addChildNodes(addFinalNodes(1));
            subTree.addChildNode(getExp());
            if (wordEntities.get(curPos).getType().equals("RBRACK")) subTree.addChildNodes(addFinalNodes(1));//]
            else {
                errorEntities.add(new ErrorEntity(wordEntities.get(curPos-1).getLine(),"k"));//错误处理
                WordEntity wordEntity=new WordEntity("RBRACK","]",wordEntities.get(curPos-1).getLine());
                subTree.addChildNode(new GrammarTreeEntity("RBRACK",true,wordEntity));
            }
        }
        addGrammarAnalysisPrintf("LVal");
        return subTree;
    }
//    数值 Number → IntConst
    public GrammarTreeEntity getNumber(){
        GrammarTreeEntity subTree=addIntermediateNode("Number");
        subTree.addChildNodes(addFinalNodes(1));//IntConst
        addGrammarAnalysisPrintf("Number");
        return subTree;
    }
    //    函数实参表 FuncRParams → Exp { ',' Exp }
    public GrammarTreeEntity getFuncRParams(){
        GrammarTreeEntity subTree=addIntermediateNode("FuncRParams");
        subTree.addChildNode(getExp());
        while (curPos<size&&wordEntities.get(curPos).getType().equals("COMMA")) {
            subTree.addChildNodes(addFinalNodes(1));//,
            subTree.addChildNode(getExp());
        }
        addGrammarAnalysisPrintf("FuncRParams");
        return subTree;
    }




//    增加中间节点
    public GrammarTreeEntity addIntermediateNode(String type){
        return new GrammarTreeEntity(type,false,wordEntities.get(curPos));
    }
//    增加多个末尾节点
    public ArrayList<GrammarTreeEntity> addFinalNodes(int x) {
        ArrayList<GrammarTreeEntity> ans = new ArrayList<>();
        for (int i=0;i<x;i++) {
            ans.add(addFinalNode());
            nextSym();
        }
        return ans;
    }
//    增加末尾节点
    public GrammarTreeEntity addFinalNode(){
        //formatString错误处理
        if (wordEntities.get(curPos).getType().equals("STRCON")) {
            String str=wordEntities.get(curPos).getWord();
            int len = str.length();
            for (int i = 0; i < len; i++) {
                //去掉""
                if (i == 0 || i == len - 1) continue;
                if (str.charAt(i) == '%') {
                    if (i + 1 >= len || str.charAt(i + 1) != 'd') {
                        errorEntities.add(new ErrorEntity(wordEntities.get(curPos).getLine(),"a"));
                        printfIgnoreLine.add(wordEntities.get(curPos).getLine());
                        break;
                    }
                } else if (str.charAt(i) == '\\') {
                    if (i + 1 >= len || str.charAt(i + 1) != 'n') {
                        errorEntities.add(new ErrorEntity(wordEntities.get(curPos).getLine(),"a"));
                        printfIgnoreLine.add(wordEntities.get(curPos).getLine());
                        break;
                    }
                } else if (!(str.charAt(i) >= 32 && str.charAt(i) <= 33) && !(str.charAt(i) >= 40 && str.charAt(i) <= 126)) {
                    errorEntities.add(new ErrorEntity(wordEntities.get(curPos).getLine(),"a"));
                    printfIgnoreLine.add(wordEntities.get(curPos).getLine());
                    break;
                }
            }
        }
        return new GrammarTreeEntity(wordEntities.get(curPos).getType(),true,wordEntities.get(curPos));
    }

    //    语法分析多输出的句子
    public void addGrammarAnalysisPrintf(String str) {
        res.add("<"+str+">"+"\n");
    }
//    读取下一个词法
    public void nextSym() {
        if (curPos >= size) return;
        res.add(wordEntities.get(curPos).getType()+" "+wordEntities.get(curPos).getWord()+"\n");
        curPos++;
    }
    public String getRes(){
        StringBuffer ans=new StringBuffer("");
        for(int i=0;i<res.size();i++){
            ans.append(res.get(i));
        }
        return ans.toString();
    }

    public GrammarTreeEntity getCurGrammarTree() {
        return curGrammarTree;
    }

    public ArrayList<ErrorEntity> getErrorEntities() {
        return errorEntities;
    }
}
