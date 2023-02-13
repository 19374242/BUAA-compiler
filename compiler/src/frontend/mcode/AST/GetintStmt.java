package frontend.mcode.AST;

import frontend.mcode.AST.LVal;
import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;

public class GetintStmt {
    public LVal lVal=null;

    public void generate() {
        Quadruple quadruple = new Quadruple(this.lVal.generate(),"GETINT", "", "");
        GlobalVariable.quadruples.add(quadruple);
    }
}
