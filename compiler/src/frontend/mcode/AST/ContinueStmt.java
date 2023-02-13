package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Jump;
import frontend.mcode.Quadruple;

public class ContinueStmt {
    public void generate() {
        String lastLoop = Jump.lastLoop;
        Quadruple quadruple = new Quadruple("","GOTO", lastLoop, "");
        GlobalVariable.quadruples.add(quadruple);
    }
}
