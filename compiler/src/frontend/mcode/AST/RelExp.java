package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class RelExp {
    public AddExp addExp=null;
    public ArrayList<WordEntity> relOps=new ArrayList<>();
    public ArrayList<AddExp> addExps=new ArrayList<>();

    public String generate() {
        String res = this.addExp.generate();
        for (int i = 0; i < this.relOps.size(); i++) {
            String arg1 = res;
            String arg2 = this.addExps.get(i).generate();
            String temp = GlobalVariable.temp.generateTemp(0);
            Quadruple quadruple = new Quadruple(temp,relOps.get(i).getType(), arg1, arg2);
            GlobalVariable.quadruples.add(quadruple);
            res = temp;
        }
        return res;
    }
}
