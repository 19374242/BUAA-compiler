package frontend.error;

import frontend.error.entity.ErrorEntity;
import frontend.error.entity.SymbolTable;
import frontend.grammar.GrammarAnalysis;
import frontend.grammar.entity.GrammarTreeEntity;

import java.util.ArrayList;
import java.util.Collections;

public class Traversal {
    private GrammarTreeEntity grammarTreeEntity;
    private ArrayList<ErrorEntity> errorEntities;
    private ArrayList<SymbolTable> symbolTables=new ArrayList<>();
    private ArrayList<ArrayList<SymbolTable>> funcParams=new ArrayList<>();
    private Integer stackWhile=0;//块进入前是否有while，数字代表while个数
    private Integer stackStmtWhile=0;//while() continue/break数量
    public Traversal(ArrayList<ErrorEntity> errorEntities,GrammarTreeEntity grammarTreeEntity){
        this.grammarTreeEntity=grammarTreeEntity;
        this.errorEntities=errorEntities;
        SymbolTable symbolTable=new SymbolTable("global",0,"block");
        symbolTables.add(symbolTable);
        travel(grammarTreeEntity);
        Collections.sort(errorEntities);
    }
    public void travel(GrammarTreeEntity subtree){
        if(subtree.getType().equals("MainFuncDef")) judgeMainFuncDef(subtree);
        else if(subtree.getType().equals("LVal")) judgeLVal(subtree);
        else if(subtree.getType().equals("Decl")) judgeDecl(subtree);
        else if(subtree.getType().equals("FuncDef")) judgeFuncDef(subtree);
        else if(subtree.getType().equals("Stmt")) judgeStmt(subtree);
        else if(subtree.getType().equals("Block")) judgeBlock(subtree);
        else if(subtree.getType().equals("CONTINUETK")||subtree.getType().equals("BREAKTK")) judgeContinueOrBreak(subtree);
        else if(subtree.getType().equals("UnaryExp")&&subtree.getChildrenNodes().get(0).getType().equals("IDENFR")) judgeUnaryExp(subtree);

        for (int i=0;i<subtree.getChildrenNodes().size();i++){
            travel(subtree.getChildrenNodes().get(i));
        }
        if (subtree.getType().equals("Block")) {
            int size=symbolTables.size();
            symbolTables.remove(size-1);
        }
    }
//    一元表达式   UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' // c d e j | UnaryOp UnaryExp
    public void judgeUnaryExp(GrammarTreeEntity subtree){
        if(subtree.getChildrenNodes().size()>1&&subtree.getChildrenNodes().get(1).getType().equals("LPARENT")){
            String name=subtree.getChildrenNodes().get(0).getWordEntity().getWord();
            int line=subtree.getChildrenNodes().get(0).getWordLine();
            SymbolTable marchTable=findFuncInSymbolTable(name);
            if(marchTable==null) errorEntities.add(new ErrorEntity(line, "c"));
            else if(marchTable.getKind().equals("func")){
                int paramsnum=0;
                ArrayList<GrammarTreeEntity> exps=new ArrayList<>();
                for(int i=0;i<subtree.getChildrenNodes().get(2).getChildrenNodes().size();i++){
                    GrammarTreeEntity curNode=subtree.getChildrenNodes().get(2).getChildrenNodes().get(i);
                    if (curNode.getType().equals("Exp")) {
                        paramsnum++;
                        exps.add(curNode);
                    }
                }
                if (marchTable.getParams().size()==paramsnum) {
                    int res=marchParamsError(marchTable.getParams(),exps,paramsnum);
                    if(res==-1){
                        errorEntities.add(new ErrorEntity(line, "e"));
                    }
                } else {
                    errorEntities.add(new ErrorEntity(line, "d"));
                }

            }
        } else {
            SymbolTable matchSymbol = findIdentInSymbolTable(subtree.getChildrenNodes().get(0).getWordEntity().getWord());
            int line = subtree.getChildrenNodes().get(0).getWordLine();
            if (matchSymbol == null) {
                errorEntities.add(new ErrorEntity(line, "c"));
            }
        }
    }
    public void judgeContinueOrBreak(GrammarTreeEntity subtree){
        boolean flag = false;
        for (int i=0; i<symbolTables.size();i++) {
            if (symbolTables.get(i).getName().equals("while")) {
                flag = true;
                break;
            }
        }
        if (stackStmtWhile>0) {
            flag = true;
            stackStmtWhile--;
        }
        if (flag==false) {
            errorEntities.add(new ErrorEntity(subtree.getWordLine(), "m"));
        }
    }

    public void judgeBlock(GrammarTreeEntity subtree){
        String name = "block";
        if (stackWhile>0) {
            stackWhile--;
            name = "while";
        }
        SymbolTable block = new SymbolTable(name,symbolTables.size(),"block");
        if (funcParams.size() > 0) {
            for (SymbolTable symbolTable : funcParams.get(funcParams.size() - 1)) {
                ErrorEntity res = block.addSymbol(symbolTable);
                if (res != null) errorEntities.add(res);
            }
            funcParams.remove(funcParams.size() - 1);
        }
        symbolTables.add(block);
    }
    //    Stmt → LVal '=' Exp ';' | [Exp] ';' | Block // h i
    //    | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    //    | 'while' '(' Cond ')' Stmt // j
    //    | 'break' ';' | 'continue' ';' // i m
    //    | 'return' [Exp] ';' // f i
    //    | LVal '=' 'getint''('')'';' // h i j
    //    | 'printf''('FormatString{,Exp}')'';' // i j l
    public void judgeStmt(GrammarTreeEntity subtree){
        if(subtree.getChildrenNodes().get(0).getType().equals("LVal")&&subtree.getChildrenNodes().get(1).getType().equals("ASSIGN")){
            String name = subtree.getChildrenNodes().get(0).getWordEntity().getWord();
            SymbolTable symbolTable = findIdentInSymbolTable(name);
            if (symbolTable!=null&&symbolTable.getConst()) {
                errorEntities.add(new ErrorEntity(subtree.getChildrenNodes().get(0).getWordLine(), "h"));
            }
        } else if (subtree.getChildrenNodes().get(0).getType().equals("WHILETK")) {
            if (subtree.getChildrenNodes().get(4).getChildrenNodes().get(0).getType().equals("Block")) {
                stackWhile++;
            } else if (subtree.getChildrenNodes().get(4).getChildrenNodes().size()>0) {
                if ((subtree.getChildrenNodes().get(4).getChildrenNodes().get(0).getType().equals("CONTINUETK")
                        || subtree.getChildrenNodes().get(4).getChildrenNodes().get(0).getType().equals("BREAKTK"))) {
                    stackStmtWhile++;
                }
            }
        } else if (subtree.getChildrenNodes().get(0).getType().equals("PRINTFTK")) {
            int line=subtree.getChildrenNodes().get(0).getWordLine();
            for (int i=0;i<GrammarAnalysis.printfIgnoreLine.size();i++){
                if(GrammarAnalysis.printfIgnoreLine.get(i)==line) return;
            }
            int num1=0,num2=0;
            String str= subtree.getChildrenNodes().get(2).getWordEntity().getWord();
            for (int i=0;i<str.length();i++) {
                if (str.charAt(i)=='%') {
                    num1++;
                }
            }
            for(int i=0;i<subtree.getChildrenNodes().size();i++){
                if(subtree.getChildrenNodes().get(i).getType().equals("Exp")){
                    num2++;
                }
            }
            if(num1!=num2) {
                errorEntities.add(new ErrorEntity(subtree.getChildrenNodes().get(0).getWordLine(), "l"));
            }
        }
    }
    //    FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // b g j
    public void judgeFuncDef(GrammarTreeEntity subtree){
        ArrayList<SymbolTable> params = new ArrayList<>();
        String type="",curName="";
        Integer curLine=0;
        for(int i=0;i<subtree.getChildrenNodes().size();i++){
            GrammarTreeEntity curNode=subtree.getChildrenNodes().get(i);
            if(curNode.getType().equals("IDENFR")){
                curName=curNode.getWordEntity().getWord();
                curLine=curNode.getWordLine();
            } else if(curNode.getType().equals("FuncType")){
                type=curNode.getChildrenNodes().get(0).getWordEntity().getWord();
            } else if(curNode.getType().equals("FuncFParams")){
                //FuncFParams → FuncFParam { ',' FuncFParam }
                //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
                int line=0,num=0;
                String name="";
                for(GrammarTreeEntity curChildrenNode:curNode.getChildrenNodes()){
                    if(curChildrenNode.getType().equals("FuncFParam")){
                        for(GrammarTreeEntity curNextChildrenNode:curChildrenNode.getChildrenNodes()){
                            if(curNextChildrenNode.getType().equals("IDENFR")){
                                name=curNextChildrenNode.getWordEntity().getWord();
                                line=curNextChildrenNode.getWordLine();
                            } else if (curNextChildrenNode.getType().equals("LBRACK")) {
                                num++;
                            }
                        }
                    } else {
                        SymbolTable tmpTable=new SymbolTable(name,symbolTables.size()-1,"IDENFR",false,num,"int",line);
                        params.add(tmpTable);
                        num=0;
                    }
                }
                SymbolTable tmpTable=new SymbolTable(name,symbolTables.size()-1,"IDENFR",false,num,"int",line);
                params.add(tmpTable);
            }
        }
        funcParams.add(params);
        SymbolTable tmpTable=new SymbolTable(curName,symbolTables.size()-1,"func",type,curLine,params);
        addSymbolIntoSymbolTable(tmpTable);
        //Block → '{' { BlockItem } '}'  我并没有
        //BlockItem → Decl | Stmt
        GrammarTreeEntity block=subtree.getChildrenNodes().get(subtree.getChildrenNodes().size()-1);
        int size=block.getChildrenNodes().size();
        if (size==2) {
            if (type.equals("int")) {
                errorEntities.add(new ErrorEntity(block.getChildrenNodes().get(size-1).getWordLine(), "g"));
            }
            return;
        }
        GrammarTreeEntity lastBlock=block.getChildrenNodes().get(size-2);
        if(type.equals("int")){
            if(!lastBlock.getChildrenNodes().get(0).getType().equals("RETURNTK")){
                errorEntities.add(new ErrorEntity(block.getChildrenNodes().get(size-1).getWordLine(),"g"));
            }
        } else {
            for(int i=0;i<block.getChildrenNodes().size();i++){
                GrammarTreeEntity curNode=block.getChildrenNodes().get(i);
                if(curNode.getChildrenNodes().size()==0) continue;
                if(curNode.getChildrenNodes().get(0).getType().equals("RETURNTK")){
                    if (!curNode.getChildrenNodes().get(1).getType().equals("SEMICN")) {
                        errorEntities.add(new ErrorEntity(curNode.getChildrenNodes().get(0).getWordLine(), "f"));
                    }
                }
            }
        }

    }
    //    Decl → ConstDecl | VarDecl
    public void judgeDecl(GrammarTreeEntity subtree){
        GrammarTreeEntity firstParm=subtree.getChildrenNodes().get(0);
        if(firstParm.getType().equals("VarDecl")){
            //    VarDecl → BType VarDef { ',' VarDef } ';' // i
            for(int i=0;i<firstParm.getChildrenNodes().size();i++){
                GrammarTreeEntity curNode=firstParm.getChildrenNodes().get(i);
                if (curNode.getType().equals("VarDef")) {
                    Integer num = 0,line=0;
                    String name="";
                    //    VarDef → Ident { '[' ConstExp ']' } // b   | Ident { '[' ConstExp ']' } '=' InitVal // k
                    for (GrammarTreeEntity curChildrenNode : curNode.getChildrenNodes()) {
                        if (curChildrenNode.getType().equals("IDENFR")) {
                            name = curChildrenNode.getWordEntity().getWord();
                            line = curChildrenNode.getWordLine();
                        } else if (curChildrenNode.getType().equals("LBRACK")){
                            num++;
                        }
                    }
                    SymbolTable tmpTable=new SymbolTable(name,symbolTables.size()-1,"IDENFR",false,num,"int",line);
                    addSymbolIntoSymbolTable(tmpTable);
                }
            }
        } else {
            //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // i
            for(int i=0;i<firstParm.getChildrenNodes().size();i++){
                GrammarTreeEntity curNode=firstParm.getChildrenNodes().get(i);
                if (curNode.getType().equals("ConstDef")) {
                    Integer num = 0,line=0;
                    String name="";
                    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal  // b k
                    for (GrammarTreeEntity curChildrenNode : curNode.getChildrenNodes()) {
                        if (curChildrenNode.getType().equals("IDENFR")) {
                            name = curChildrenNode.getWordEntity().getWord();
                            line = curChildrenNode.getWordLine();
                        } else if (curChildrenNode.getType().equals("LBRACK")){
                            num++;
                        }
                    }
                    SymbolTable tmpTable=new SymbolTable(name,symbolTables.size()-1,"IDENFR",true,num,"int",line);
                    addSymbolIntoSymbolTable(tmpTable);
                }
            }
        }
    }
//    LVal → Ident {'[' Exp ']'}
    public void judgeLVal(GrammarTreeEntity subtree){
        GrammarTreeEntity firstIdent=subtree.getChildrenNodes().get(0);
        if(findIdentInSymbolTable(firstIdent.getWordEntity().getWord())==null){
            errorEntities.add(new ErrorEntity(firstIdent.getWordLine(),"c"));
        }
    }
    //int main ( ) block
    public void judgeMainFuncDef(GrammarTreeEntity subtree){
        GrammarTreeEntity block=subtree.getChildrenNodes().get(4);
        int size=block.getChildrenNodes().size();//block结点数
        GrammarTreeEntity lastBlock=block.getChildrenNodes().get(size-2);//最后一个语句
        if(size==2){
            errorEntities.add(new ErrorEntity(block.getChildrenNodes().get(size-1).getWordLine(),"g"));
        }else if(!lastBlock.getChildrenNodes().get(0).getType().equals("RETURNTK")){
            errorEntities.add(new ErrorEntity(block.getChildrenNodes().get(size-1).getWordLine(),"g"));
        }
    }
    public SymbolTable findIdentInSymbolTable(String name){
        for (int i=symbolTables.size()-1; i>= 0; i--) {
            SymbolTable res = symbolTables.get(i).findIdent(name);
            if(res!=null) return res;
        }
        return null;
    }
    public SymbolTable findFuncInSymbolTable(String name) {
        for (int i=symbolTables.size()-1; i >= 0; i--) {
            SymbolTable res = symbolTables.get(i).findFunc(name);
            if (res!=null) return res;
        }
        return null;
    }
    public void addSymbolIntoSymbolTable(SymbolTable symbolTable) {
        ErrorEntity res = this.symbolTables.get(symbolTables.size()-1).addSymbol(symbolTable);
        if (res != null) this.errorEntities.add(res);
    }
    public Integer marchParamsError(ArrayList<SymbolTable> params,ArrayList<GrammarTreeEntity> exps,int len){
        for(int i=0;i<len;i++){
            SymbolTable param = params.get(i);//符号表中函数相应参数
            GrammarTreeEntity exp = exps.get(i);//调用函数时相应参数
            //Exp → AddExp
            GrammarTreeEntity addExp = exp.getChildrenNodes().get(0);
            //AddExp → MulExp | AddExp ('+' | '−') MulExp
            if(addExp.getChildrenNodes().size()==1){ //因为参数是一个变量，所以必然不可能加减
                GrammarTreeEntity mulExp=addExp.getChildrenNodes().get(0);
                //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
                if(mulExp.getChildrenNodes().size()==1){
                    GrammarTreeEntity unaryExp = mulExp.getChildrenNodes().get(0);
                    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' || UnaryOp UnaryExp  // c d e j
                    while (unaryExp.getChildrenNodes().get(0).getType().equals("UnaryOp")) {
                        unaryExp=unaryExp.getChildrenNodes().get(1);
                    }
                    if (unaryExp.getChildrenNodes().get(0).getType().equals("PrimaryExp")){
                        GrammarTreeEntity primaryExp=unaryExp.getChildrenNodes().get(0);
                        //PrimaryExp → '(' Exp ')' | LVal | Number
                        if (primaryExp.getChildrenNodes().get(0).getType().equals("LVal")){
                            //LVal → Ident {'[' Exp ']'} // c k
                            GrammarTreeEntity lval=primaryExp.getChildrenNodes().get(0);
                            int num = 0;
                            //符号表中寻找调用函数时某一参数是否存在
                            SymbolTable marchIdent = findIdentInSymbolTable(lval.getChildrenNodes().get(0).getWordEntity().getWord());
                            for (GrammarTreeEntity curNode : lval.getChildrenNodes()) {
                                if (curNode.getType().equals("LBRACK")) num++;
                            }
                            if(marchIdent!=null){
                                //int f(a[])  int b[][][]   f(b[][])才是可行的
                                int tmpParam=marchIdent.getDimension()-num;
                                if (tmpParam!=param.getDimension()) {
                                    return -1;
                                }
                            }
                        } else if (primaryExp.getChildrenNodes().get(0).getType().equals("Number")){
                            if (param.getDimension()!=0) {
                                return -1;
                            }
                        }
                    } else if (unaryExp.getChildrenNodes().get(0).getType().equals("IDENFR")) {
                        String name = unaryExp.getChildrenNodes().get(0).getWordEntity().getWord();
                        SymbolTable marchFunc = findFuncInSymbolTable(name);
                        //参数是函数
                        if (marchFunc != null && marchFunc.getType().equals("void")) {
                            return -1;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public String getErrorRes(){
        StringBuffer ans=new StringBuffer("");
        for(int i=0;i<errorEntities.size();i++){
            ans.append(errorEntities.get(i).getLine()+" "+errorEntities.get(i).getCode()+"\n");
        }
        return ans.toString();
    }

    public void put(){
        for (int i=0;i<errorEntities.size();i++){
            System.out.println(errorEntities.get(i).getLine()+" "+errorEntities.get(i).getCode());
        }

    }



}
