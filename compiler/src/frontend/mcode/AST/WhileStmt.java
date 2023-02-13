package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Jump;
import frontend.mcode.Quadruple;

public class WhileStmt {
    public Cond cond=null;
    public Stmt stmt=null;
    /*
            while begin
            goto loop
            loopBegin:
            <stmt>
            loop:
            if cond goto loopBegin
            loopEnd:
            while end
            ...
    */
    public void generate() {
        Quadruple quadruple0 = new Quadruple("","WHILE_BEGIN", "", "");
        GlobalVariable.quadruples.add(quadruple0);
        // goto loop
        String labelLoop = "loop" + Jump.labelLoop++;
        Quadruple quadruple1 = new Quadruple("","GOTO", labelLoop, "");
        GlobalVariable.quadruples.add(quadruple1);
        // loopBegin:
        String labelLoopBegin = "loop_begin" + Jump.labelLoopBegin++;
        Quadruple quadruple2 = new Quadruple("","LABEL", labelLoopBegin, "");
        GlobalVariable.quadruples.add(quadruple2);
        String labelLoopEnd = "loop_end" + Jump.labelLoopEnd++;
        // stmt
        String lastLoop = Jump.lastLoop;
        String lastLoopEnd = Jump.lastLoopEnd;
        Jump.lastLoop=labelLoop;
        Jump.lastLoopEnd=labelLoopEnd;//进入新循环
        this.stmt.generate();
        Jump.lastLoop=lastLoop;//结束该循环
        Jump.lastLoopEnd=lastLoopEnd;
        // loop
        Quadruple quadruple3 = new Quadruple("","LABEL", labelLoop, "");
        GlobalVariable.quadruples.add(quadruple3);
        // if
        Jump.isLoopIf=true;
        this.cond.generate(labelLoopBegin, labelLoopEnd);
        Jump.isLoopIf=false;
        // loopEnd:
        Quadruple quadruple4 = new Quadruple("","LABEL", labelLoopEnd, "");
        GlobalVariable.quadruples.add(quadruple4);

        Quadruple quadruple5 = new Quadruple("","WHILE_END", "", "");
        GlobalVariable.quadruples.add(quadruple5);

    }
}
