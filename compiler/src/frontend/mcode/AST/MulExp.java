package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MulExp {
    public UnaryExp unaryExp=null;
    public ArrayList<WordEntity> mulOps=new ArrayList<>();
    public ArrayList<UnaryExp> unaryExps=new ArrayList<>();

    public String generate() {
        String res = this.unaryExp.generate();
        for (int i = 0; i < this.mulOps.size(); i++) {
            String arg1 = res;
            String arg2 = this.unaryExps.get(i).generate();
            if (this.mulOps.get(i).getType().equals("MULT")) {
                if (isImm(arg1) && isImm(arg2)) {
                    res = Integer.toString(Integer.parseInt(arg1) * Integer.parseInt(arg2));
                } else if (arg1.equals("0") || arg1.equals("-0")) {
                    res = "0";
                } else if (arg2.equals("0") || arg2.equals("-0")) {
                    res = "0";
                } else if (arg1.equals("1")) {
                    res = arg2;
                } else if (arg2.equals("1")) {
                    res = arg1;
                } else {
                    String temp = GlobalVariable.temp.generateTemp(
                            GlobalVariable.symbolTable.getSymbolValue(arg1) * GlobalVariable.symbolTable.getSymbolValue(arg2));
                    Quadruple quadruple = new Quadruple(temp,"MUL", arg1, arg2);
                    GlobalVariable.quadruples.add(quadruple);
                    res = temp;
                }
            } else if (this.mulOps.get(i).getType().equals("DIV")) {
                if (isImm(arg1) && isImm(arg2)) {
                    if (Integer.parseInt(arg2) != 0) {
                        res = Integer.toString(Integer.parseInt(arg1) / Integer.parseInt(arg2));
                    } else {
                        res = "0";
                    }
                } else if (arg1.equals("0") || arg1.equals("-0")){
                    res = "0";
                } else if (arg2.equals("1")) {
                    res = arg1;
                } else {
                    String temp;
                    if (GlobalVariable.symbolTable.getSymbolValue(arg2) != 0) {
                        temp = GlobalVariable.temp.generateTemp(
                                GlobalVariable.symbolTable.getSymbolValue(arg1) / GlobalVariable.symbolTable.getSymbolValue(arg2));
                    } else {
                        temp = GlobalVariable.temp.generateTemp(0);
                    }
                    Quadruple quadruple = new Quadruple(temp,"DIV", arg1, arg2);
                    GlobalVariable.quadruples.add(quadruple);
                    res = temp;
                }
            } else if (this.mulOps.get(i).getType().equals("MOD")) {
                if (isImm(arg1) && isImm(arg2)) {
                    if (Integer.parseInt(arg2) != 0) {
                        res = Integer.toString(Integer.parseInt(arg1) % Integer.parseInt(arg2));
                    } else {
                        res = "0";
                    }
                } else if (arg1.equals("0") || arg1.equals("-0")){
                    res = "0";
                } else if (arg2.equals("1") || arg2.equals("-1")) {
                    res = "0";
                } else {
                    String temp;
                    if (GlobalVariable.symbolTable.getSymbolValue(arg2) != 0) {
                        temp = GlobalVariable.temp.generateTemp(
                                GlobalVariable.symbolTable.getSymbolValue(arg1) % GlobalVariable.symbolTable.getSymbolValue(arg2));
                    } else {
                        temp = GlobalVariable.temp.generateTemp(0);
                    }
                    Quadruple quadruple = new Quadruple(temp,"MOD", arg1, arg2);
                    GlobalVariable.quadruples.add(quadruple);
                    res = temp;
                }
            }
        }
        return res;
    }
    public int generateNumber() {
        int res = this.unaryExp.generateNumber();
        for (int i = 0; i < this.mulOps.size(); i++) {
            int arg1 = res;
            int arg2 = this.unaryExps.get(i).generateNumber();
            if (this.mulOps.get(i).getType().equals("MULT")) {
                res = arg1 * arg2;
            } else if (this.mulOps.get(i).getType().equals("DIV")) {
                res = (arg2 != 0) ? arg1 / arg2 : 0;
            } else if (this.mulOps.get(i).getType().equals("MOD")) {
                res = (arg2 != 0) ? arg1 % arg2 : 0;
            }
        }
        return res;
    }
    public boolean isImm(String arg) {
        Pattern patternImm = Pattern.compile("-?[0-9]+");
        Matcher matcherImm = patternImm.matcher(arg);
        return matcherImm.matches();
    }

    public int getDim() {
        return this.unaryExp.getDim();
    }

}
