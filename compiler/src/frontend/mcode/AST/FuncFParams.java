package frontend.mcode.AST;

import java.util.ArrayList;

public class FuncFParams {
    public int paramsNum=0;
    public ArrayList<Integer> paramsDimList=new ArrayList<>();
    public FuncFParam funcFParam=null;
    public ArrayList<FuncFParam> funcFParamList=new ArrayList<>();

    public void generate() {
        if (this.paramsNum > 0) {
            this.funcFParam.generate();
            for (int i = 0; i < this.paramsNum - 1; i++) {
                this.funcFParamList.get(i).generate();
            }
        }
    }
}
