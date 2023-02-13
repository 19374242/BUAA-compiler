package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstExp {
    public AddExp addExp=null;

    public int generate() {
       return addExp.generateNumber();
//        String str= addExp.generate();
//        if(isImm(str)) return Integer.parseInt(str);
//        else {
//            return GlobalVariable.symbolTable.getSymbolValue(str);
//        }
    }
    public boolean isImm(String arg) {
        Pattern patternImm = Pattern.compile("-?[0-9]+");
        Matcher matcherImm = patternImm.matcher(arg);
        return matcherImm.matches();
    }
}
