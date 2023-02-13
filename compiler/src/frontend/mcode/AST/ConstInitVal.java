package frontend.mcode.AST;

import java.util.ArrayList;

public class ConstInitVal {
    public int dim=0;
    public ArrayList<ConstInitVal> constInitVals=new ArrayList<>();
    public ConstExp constExp=null;
    public ConstInitVal constInitVal=null;

    public ConstExp getConstExp() {
        return this.constExp;
    }

    public ConstExp getConstExp(int i) {
        if (i == 0) {
            return this.constInitVal.getConstExp();
        } else {
            return this.constInitVals.get(i - 1).getConstExp();
        }
    }
    public ConstExp getConstExp(int i, int j) {
        if (i == 0) {
            return this.constInitVal.getConstExp(j);
        } else {
            return this.constInitVals.get(i - 1).getConstExp(j);
        }
    }
}
