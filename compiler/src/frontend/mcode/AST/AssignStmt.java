package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssignStmt {
    public LVal lVal=null;
    public Exp exp=null;

    public void generate() {
        if (lVal.dim == 0) {
            String arg1 = this.exp.generate();
            String dst = this.lVal.generate();
            Quadruple quadruple = new Quadruple(dst,"ASSIGNMENT", arg1, "");
            GlobalVariable.quadruples.add(quadruple);
        } else {
            String arg1 = this.exp.generate();
            String string = this.lVal.generate();
            Pattern pattern = Pattern.compile("(.+?)\\[(.+)\\]");//匹配a[b]
            Matcher matcher = pattern.matcher(string);
            if (matcher.matches()) {
                String dst = matcher.group(1);//a
                String index = matcher.group(2);//b
                Quadruple quadruple = new Quadruple(dst,"ASSIGNMENT_VAR_ARRAY", arg1, "");
                quadruple.setIndex(index);
                GlobalVariable.quadruples.add(quadruple);
            } else {
                System.out.println("StmtLValAssign wrong");
            }
        }
    }
}
