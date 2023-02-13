package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;

import java.util.ArrayList;

public class FuncRParams {
    public Exp exp=null;
    public ArrayList<Exp> exps=new ArrayList<>();

    public void generate() {
        if (exps.size() >= 0) {
            Quadruple quadruple1 = new Quadruple("", "PUSH", this.exp.generate(), "");
            quadruple1.setPushDim(this.exp.getDim());//好像是定义的dim-现在的dim
            GlobalVariable.quadruples.add(quadruple1);
            for (int i = 0; i < exps.size(); i++) {
                Quadruple quadruple2 = new Quadruple("","PUSH", this.exps.get(i).generate(), "");
                quadruple2.setPushDim(this.exps.get(i).getDim());
                GlobalVariable.quadruples.add(quadruple2);
            }
        }
    }
}
