package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Jump;
import frontend.mcode.Quadruple;

public class IfStmt {
    public Cond cond=null;
    public Stmt stmt=null;
    public Stmt elseStmt=null;

    public void generate() {
        if (this.elseStmt == null) {
            String ifBeginlabel ="if_begin" + Jump.ifBeginLabel++;
            String ifEndlabel = "if_end" + Jump.ifEndLabel++;
            cond.generate(ifBeginlabel,ifEndlabel);
            Quadruple quadruple1 = new Quadruple("","LABEL", ifBeginlabel, "");
            GlobalVariable.quadruples.add(quadruple1);
            stmt.generate();
            Quadruple quadruple2 = new Quadruple("","LABEL", ifEndlabel, "");
            GlobalVariable.quadruples.add(quadruple2);
        } else {
            String IfBeginlabel1 = "if_begin" + Jump.ifBeginLabel++;
            String IfBeginlabel2 = "if_begin" + Jump.ifBeginLabel++;
            String IfEndlabel = "if_end" + Jump.ifEndLabel++;
            this.cond.generate(IfBeginlabel1, IfBeginlabel2);//begin2作为begin1的结尾标签
            Quadruple quadruple1 = new Quadruple("","LABEL", IfBeginlabel1, "");
            GlobalVariable.quadruples.add(quadruple1);
            stmt.generate();
            Quadruple quadruple2 = new Quadruple("","GOTO", IfEndlabel, "");
            GlobalVariable.quadruples.add(quadruple2);
            Quadruple quadruple3 = new Quadruple("","LABEL", IfBeginlabel2, "");
            GlobalVariable.quadruples.add(quadruple3);
            this.elseStmt.generate();
            Quadruple quadruple4 = new Quadruple("","LABEL", IfEndlabel, "");
            GlobalVariable.quadruples.add(quadruple4);
        }
    }

}
