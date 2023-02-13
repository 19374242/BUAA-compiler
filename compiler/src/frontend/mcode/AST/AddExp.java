package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddExp {
    public MulExp mulExp=null;
    public ArrayList<WordEntity> addOps=new ArrayList<>();
    public ArrayList<MulExp> mulExps=new ArrayList<>();

    public String generate() {
        String res = this.mulExp.generate();
        for (int i = 0; i < this.addOps.size(); i++) {
            String arg1 = res;
            String arg2 = this.mulExps.get(i).generate();
            if (this.addOps.get(i).getType().equals("PLUS")) {
                if (isImm(arg1) && isImm(arg2)) {
                    res = Integer.toString(Integer.parseInt(arg1) + Integer.parseInt(arg2));
                } else if (arg1.equals("0") || arg1.equals("-0")) {
                    res = arg2;
                } else if (arg2.equals("0") || arg2.equals("-0")) {
                    res = arg1;
                } else {
                    String temp = GlobalVariable.temp.generateTemp(
                            GlobalVariable.symbolTable.getSymbolValue(arg1) + GlobalVariable.symbolTable.getSymbolValue(arg2));
                    Quadruple quadruple = new Quadruple(temp,"ADD", arg1, arg2);
                    GlobalVariable.quadruples.add(quadruple);
                    res=temp;
                }
            } else if (this.addOps.get(i).getType().equals("MINU")) {
                if (isImm(arg1) && isImm(arg2)) {
                    res = Integer.toString(Integer.parseInt(arg1) - Integer.parseInt(arg2));
                } else if (arg2.equals("0") || arg2.equals("-0")) {
                    res = arg1;
                } else {
                    String temp = GlobalVariable.temp.generateTemp(
                            GlobalVariable.symbolTable.getSymbolValue(arg1) - GlobalVariable.symbolTable.getSymbolValue(arg2));
                    Quadruple quadruple = new Quadruple(temp,"SUB", arg1, arg2);
                    GlobalVariable.quadruples.add(quadruple);
                    res = temp;
                }
            }
        }
        return res;
    }
    //因为有int b[a[2]]={a[2],a[1]}存在，所以必须有可以直接获得数的函数(decl时)
    public int generateNumber(){
        int res = mulExp.generateNumber();
        for (int i = 0; i < addOps.size(); i++) {
            int arg1 = res;
            int arg2 = mulExps.get(i).generateNumber();
            if (this.addOps.get(i).getType().equals("PLUS")) {
                res = arg1 + arg2;
            } else if (this.addOps.get(i).getType().equals("MINU")) {
                res = arg1 - arg2;
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
        return this.mulExp.getDim();
    }
}
