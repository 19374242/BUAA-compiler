package frontend.mcode.AST;

public class BlockItem {
    public boolean isDecl=false;
    public Decl decl=null;
    public Stmt stmt=null;


    public void generate() {
        if (isDecl) {
            this.decl.generate();
        } else {
            this.stmt.generate();
        }
    }
}
