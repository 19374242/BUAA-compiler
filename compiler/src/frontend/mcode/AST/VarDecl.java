package frontend.mcode.AST;

import java.util.ArrayList;

public class VarDecl {
    public ArrayList<VarDef> varDefs=new ArrayList<>();

    public void generate() {
        for (VarDef varDef : varDefs) {
            varDef.generate();
        }
    }
}
