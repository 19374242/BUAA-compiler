package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Symbols.BlockTable;

import java.util.ArrayList;

public class CompUnit {
    public ArrayList<Decl> decls=new ArrayList<>();
    public ArrayList<FuncDef> funcDefs=new ArrayList<>();
    public MainFuncDef mainFuncDef=null;

    public CompUnit() {

    }
    public void generate() {
        for (Decl decl : this.decls) {
            decl.generate();
        }
        for (FuncDef funcDef : this.funcDefs) {
            funcDef.generate();
        }
        this.mainFuncDef.generate();

    }
}
