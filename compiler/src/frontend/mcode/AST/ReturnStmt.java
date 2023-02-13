package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;

public class ReturnStmt {
    public Exp exp=null;

    public void generate() {
        Quadruple quadruple;
        if (exp!=null) {
            quadruple = new Quadruple("","RETURN", this.exp.generate(), "");
        } else {
            quadruple = new Quadruple("","RETURN", "", "");
        }
        GlobalVariable.quadruples.add(quadruple);
    }
}
