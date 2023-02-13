package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LVal {
    public int dim=0;
    public WordEntity ident=null;
    public ArrayList<Exp> exps=new ArrayList<>();


    public int getDim() {
        int identDim =  GlobalVariable.symbolTable.getSymbol(this.ident.getWord()).getDim();
        if (this.dim <= identDim) {
            return identDim - this.dim;
        }
        return 0;
    }

    public String generate() {
        if (this.dim == 0) {
            return this.ident.getWord();
        } else if (this.dim == 1) {
            String dim1 = this.exps.get(0).generate();
            if (isImm(dim1)) {
                return this.ident.getWord()+ "[" + dim1 + "]";
            } else {
                String temp = GlobalVariable.temp.generateTemp(0);
                Quadruple quadruple = new Quadruple(temp,"ASSIGNMENT",dim1, "");
                GlobalVariable.quadruples.add(quadruple);
                return this.ident.getWord() + "[" + temp + "]";
            }
        } else if (this.dim == 2) {
            String dim1 = this.exps.get(0).generate();
            String dim2 = this.exps.get(1).generate();
            String temp1 = GlobalVariable.temp.generateTemp(
                    GlobalVariable.symbolTable.getSymbolValue(dim1) * GlobalVariable.symbolTable.getSymbol(this.ident.getWord()).getDim2());
            Quadruple quadruple1 = new Quadruple(temp1,"MUL",
                    dim1, Integer.toString(GlobalVariable.symbolTable.getSymbol(this.ident.getWord()).getDim2()));
            GlobalVariable.quadruples.add(quadruple1);
            String temp2 = GlobalVariable.temp.generateTemp(
                    GlobalVariable.symbolTable.getSymbolValue(dim2) + GlobalVariable.symbolTable.getSymbolValue(temp1));
            Quadruple quadruple2 = new Quadruple(temp2,"ADD", dim2, temp1);
            GlobalVariable.quadruples.add(quadruple2);
            return this.ident.getWord() + "[" + temp2 + "]";
        }
        return "LVal wrong";
    }
    public int generateNumber() {
        if (this.dim == 0) {
            return GlobalVariable.symbolTable.getSymbolValue(this.ident.getWord());
        } else if (this.dim == 1) {
            int dim1 = this.exps.get(0).generateNumber();
            return GlobalVariable.symbolTable.getSymbol(ident.getWord()).getValueList().get(dim1);
        } else if (this.dim == 2) {
            int dim1 = this.exps.get(0).generateNumber();
            int dim2 = this.exps.get(1).generateNumber();
            int index = dim1 * GlobalVariable.symbolTable.getSymbol(this.ident.getWord()).getDim2() + dim2;
            return GlobalVariable.symbolTable.getSymbol(ident.getWord()).getValueList().get(index);
        }
        return 0;
    }

    public boolean isImm(String arg) {
        Pattern patternImm = Pattern.compile("-?[0-9]+");
        Matcher matcherImm = patternImm.matcher(arg);
        return matcherImm.matches();
    }

}
