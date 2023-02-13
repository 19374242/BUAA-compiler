package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Jump;
import frontend.mcode.Quadruple;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class LOrExp {
    public LAndExp lAndExp=null;
    public ArrayList<WordEntity> orOps=new ArrayList<>();
    public ArrayList<LAndExp> lAndExps=new ArrayList<>();

    public void generate(String ifBeginlabel, String ifEndlabel) {
        if (this.orOps.size() == 0) {
            this.lAndExp.generate(ifBeginlabel,ifEndlabel);
        } else {
            String label1 = "label" + Jump.label++;
            this.lAndExp.generate(ifBeginlabel,label1);//这里显示如果失败，跳转的是下一个判断（label）
            //set $T0 on a == 1
            //branch label0 on $T0 == 0
            //else 下面的 goto if_begin0（只要一个为true就跳转）
            if(Jump.isLoopIf==false){//while里面没有是因为它不是那么表达的（两种方式）
                //set $T1 on a < 5
                //branch loop_begin0 on $T1 != 0
                //else 继续执行label label0
                Quadruple quadruple1 = new Quadruple("","GOTO", ifBeginlabel, "");
                GlobalVariable.quadruples.add(quadruple1);
            }
            Quadruple quadruple2 = new Quadruple("","LABEL", label1, "");
            GlobalVariable.quadruples.add(quadruple2);
            for (int i = 0; i < this.orOps.size(); i++) {
                LAndExp lAndExp = this.lAndExps.get(i);
                if (i == this.orOps.size() - 1) {
                    lAndExp.generate(ifBeginlabel,ifEndlabel);
                } else {
                    String label2 = "label" + Jump.label++;
                    lAndExp.generate(ifBeginlabel,label2);
                    if(Jump.isLoopIf==false){
                        Quadruple quadruple3 = new Quadruple("","GOTO",ifBeginlabel, "");
                        GlobalVariable.quadruples.add(quadruple3);
                    }
                    Quadruple quadruple4 = new Quadruple("","LABEL", label2, "");
                    GlobalVariable.quadruples.add(quadruple4);
                }
            }
        }
    }
}
