package frontend.mcode.AST;

import java.util.ArrayList;

public class ConstDecl {

    public ArrayList<ConstDef> constDefs;

    public ConstDecl() {
        this.constDefs = new ArrayList<>();
    }

    public void generate() {
        for (ConstDef constDef : this.constDefs) {
            constDef.generate();
        }
    }
}
