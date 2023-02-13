package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Jump;
import frontend.mcode.Quadruple;

public class BreakStmt {

    public void generate() {
        String lastLoopEnd = Jump.lastLoopEnd;
        Quadruple quadruple = new Quadruple("","GOTO", lastLoopEnd, "");
        GlobalVariable.quadruples.add(quadruple);
    }
}
