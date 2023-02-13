package frontend.mcode.AST;

public class Cond {
    public LOrExp lOrExp=null;


    public void generate(String ifBeginlabel, String ifEndlabel) {
        lOrExp.generate(ifBeginlabel,ifEndlabel);
    }
}
