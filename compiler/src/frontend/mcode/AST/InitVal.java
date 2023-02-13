package frontend.mcode.AST;

import java.util.ArrayList;

public class InitVal {
    public int dim=0;
    public ArrayList<InitVal> initVals=new ArrayList<>();
    public Exp exp=null;
    public InitVal initVal=null;


    public Exp getExp() {
        return this.exp;
    }
    public Integer expSize(){
        if (initVal==null) return 0;
        else {
            return initVals.size()+1;
        }
    }
    public Exp getExp(int i) {
        if (i == 0) {
            return this.initVal.getExp();
        } else {
            return this.initVals.get(i - 1).getExp();
        }
    }

    public Exp getExp(int i, int j) {
        if (i == 0) {
            return this.initVal.getExp(j);
        } else {
            return this.initVals.get(i - 1).getExp(j);
        }
    }
}
