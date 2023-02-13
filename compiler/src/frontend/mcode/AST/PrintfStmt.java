package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;

import java.util.ArrayList;

public class PrintfStmt {
    public String formatString="";
    public ArrayList<Exp> exps=new ArrayList<>();

    public void generate() {
        ArrayList<Quadruple> quadrupleList = new ArrayList<>();
        String string =formatString;
        String[] stringConstList = string.split("%d");
        stringConstList[0] = stringConstList[0].substring(1);//去掉第一个"
        //去掉最后一个"
        stringConstList[stringConstList.length - 1] = stringConstList[stringConstList.length - 1].substring(0, stringConstList[stringConstList.length - 1].length() - 1);
        if (!stringConstList[0].equals("")) {
            Quadruple quadruple = new Quadruple("","PRINT_STR", stringConstList[0], "");
            quadrupleList.add(quadruple);
            //Node.intermediate.addIntermediateCode(quadruple);
            GlobalVariable.symbolTable.addStringConst(stringConstList[0]);
        }
        for (int i = 0; i < this.exps.size(); i++) {
            String str=exps.get(i).generate();
            Quadruple quadruple1 = new Quadruple("", "PRINT_INT",str, "");
            quadrupleList.add(quadruple1);
            //Node.intermediate.addIntermediateCode(quadruple1);
            if (!stringConstList[i + 1].equals("")) {
                Quadruple quadruple2 = new Quadruple("","PRINT_STR", stringConstList[i + 1], "");
                quadrupleList.add(quadruple2);
                //Node.intermediate.addIntermediateCode(quadruple2);
                GlobalVariable.symbolTable.addStringConst(stringConstList[i+1]);
            }
        }
        for (Quadruple quadruple : quadrupleList) {
            GlobalVariable.quadruples.add(quadruple);
        }
    }
}
