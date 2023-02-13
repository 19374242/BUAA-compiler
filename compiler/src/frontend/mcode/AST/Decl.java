package frontend.mcode.AST;

public class Decl {
    public boolean isConst;
    public ConstDecl constDecl;
    public VarDecl varDecl;

    public void generate() {
        if (isConst) {
            this.constDecl.generate();
        } else {
            this.varDecl.generate();
        }
    }
}
