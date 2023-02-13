package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Jump;
import frontend.mcode.Quadruple;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class EqExp {
    public RelExp relExp=null;
    public ArrayList<WordEntity> eqOps=new ArrayList<>();
    public ArrayList<RelExp> relExps=new ArrayList<>();

    public void generate(String ifBeginlabel,String ifEndlabel,Boolean isLast) {
        String res = this.relExp.generate();
        for (int i = 0; i < this.eqOps.size(); i++) {
            String arg1 = res;
            String arg2 = this.relExps.get(i).generate();
            String temp = GlobalVariable.temp.generateTemp(0);
            Quadruple quadruple = new Quadruple(temp,eqOps.get(i).getType(), arg1, arg2);
            GlobalVariable.quadruples.add(quadruple);
            res = temp;
        }
        if(!isLast){
            //如果结果为0则跳转到结尾标签  branch if_end0(if_begin1) on $T0 == 0
            Quadruple quadruple1 = new Quadruple(ifEndlabel,"BEQ", res, "0");
            GlobalVariable.quadruples.add(quadruple1);
        } else{
            if(Jump.isLoopIf){
                Quadruple quadruple1 = new Quadruple(ifBeginlabel,"BNE", res, "0");
                GlobalVariable.quadruples.add(quadruple1);
            } else {
                Quadruple quadruple1 = new Quadruple(ifEndlabel,"BEQ", res, "0");
                GlobalVariable.quadruples.add(quadruple1);
            }
        }

    }
}
