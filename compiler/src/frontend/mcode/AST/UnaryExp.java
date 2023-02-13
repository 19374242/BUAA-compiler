package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.Symbol;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnaryExp {
    public String unaryExpType=""; //IDENT/PRIMARYEXP/UNARYEXP
    //
    public PrimaryExp primaryExp=null;
    //
    public WordEntity ident=null;
    public FuncRParams funcRParams=null;
    public WordEntity unaryOp=null;
    public UnaryExp unaryExp=null;

    public String generate() {
        if (this.unaryExpType.equals("PRIMARYEXP")) {
            return this.primaryExp.generate();
        } else if (this.unaryExpType.equals("IDENT")) {
            ArrayList<String> sRegList = new ArrayList<>();
            ArrayList<String> tRegList = new ArrayList<>();
            String ret = "";
            Quadruple quadruple1 = new Quadruple("","CALL_BEGIN", this.ident.getWord(), "");
            quadruple1.setsRegList(sRegList);
            quadruple1.settRegList(tRegList);
            GlobalVariable.quadruples.add(quadruple1);
            if (funcRParams != null) {
                this.funcRParams.generate();
            }
            Quadruple quadruple2 = new Quadruple("","CALL", this.ident.getWord(), "");
            quadruple2.setsRegList(sRegList);//CALL和CALL_BEGIN的两个reglist一致
            quadruple2.settRegList(tRegList);
            GlobalVariable.quadruples.add(quadruple2);
            if (GlobalVariable.symbolTable.getFuncSymbol(this.ident.getWord()).getSymbolType().equals("INT_FUNC")) {
                String temp = GlobalVariable.temp.generateTemp(0);
                Quadruple quadruple3 = new Quadruple(temp,"ASSIGNMENT", "@RET", "");
                GlobalVariable.quadruples.add(quadruple3);
                ret = temp;
            }
            return ret;
        } else if (this.unaryExpType.equals("UNARYEXP")) {
            String ret = this.unaryExp.generate();
            if (this.unaryOp.getType().equals("PLUS")) {
                return ret;
            } else if (this.unaryOp.getType().equals("MINU")) {
                if (isImm(ret)) {
                    return Integer.toString(-Integer.parseInt(ret));
                } else {
                    String temp = GlobalVariable.temp.generateTemp(-GlobalVariable.symbolTable.getSymbolValue(ret));
                    Quadruple quadruple = new Quadruple(temp,"SUB", "0", ret);
                    GlobalVariable.quadruples.add(quadruple);
                    ret = temp;
                    return ret;
                }
            } else if (this.unaryOp.getType().equals("NOT")) {
                if (isImm(ret)) {
                    int judge = Integer.parseInt(ret);
                    if (judge != 0) return "0";
                    else return "1";
                } else {
                    String temp = GlobalVariable.temp.generateTemp(0);
                    Quadruple quadruple = new Quadruple(temp,"EQL", ret, "0");
                    GlobalVariable.quadruples.add(quadruple);
                    ret = temp;
                    return ret;
                }
            }
        }
        return "UnaryExp wrong";
    }
    public int generateNumber() {
        if (this.unaryExpType.equals("PRIMARYEXP")) {
            return this.primaryExp.generateNumber();
        } else if (this.unaryExpType.equals("IDENT")) {
                return 0;//不可以是函数，不然我自闭
        } else if (this.unaryExpType.equals("UNARYEXP")){
            int ret = this.unaryExp.generateNumber();
            if (this.unaryOp.getType().equals("PLUS")) {
                return ret;
            } else if (this.unaryOp.getType().equals("MINU")) {
                return -ret;
            } else if (this.unaryOp.getType().equals("NOT")) {
                return (ret == 0) ? 1:0;
            }
        }
        return 0;
    }
    public boolean isImm(String arg) {
        Pattern patternImm = Pattern.compile("-?[0-9]+");
        Matcher matcherImm = patternImm.matcher(arg);
        return matcherImm.matches();
    }
    public int getDim() {
        if (this.unaryExpType.equals("PRIMARYEXP")) {
            return this.primaryExp.getDim();
        } else if (this.unaryExpType.equals("UNARYEXP")) {
            return this.unaryExp.getDim();
        } else {
            if (GlobalVariable.symbolTable.containFuncSymbol(this.ident.getWord())) {
                Symbol symbol = GlobalVariable.symbolTable.getFuncSymbol(this.ident.getWord());
                if (symbol.getSymbolType().equals("INT_FUNC")) {
                    return 0;
                } else {
                    return -1;
                }
            }
            return -1;
        }
    }

}
