package frontend.mcode.AST;

import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class LAndExp {
    public EqExp eqExp=null;
    public ArrayList<WordEntity> andOps=new ArrayList<>();
    public ArrayList<EqExp> eqExps=new ArrayList<>();

    public void generate(String ifBeginlabel,String ifEndlabel) {
        if(eqExps.size()==0) eqExp.generate(ifBeginlabel,ifEndlabel,true);
        else{
            int num=eqExps.size();
            eqExp.generate(ifBeginlabel,ifEndlabel,false);
            for (EqExp eqExp : eqExps) {
                num--;
                if(num==0) eqExp.generate(ifBeginlabel,ifEndlabel,true);
                else eqExp.generate(ifBeginlabel,ifEndlabel,false);
            }
        }
    }
}
