package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.Symbol;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class FuncFParam {
    public int dim=0;
    public String btype="";
    public WordEntity ident=null;
    public ArrayList<ConstExp> constExpList=new ArrayList<>();

    public void generate() {
        if (this.dim == 0) {
            Symbol symbol = new Symbol(this.ident.getWord(), "PARA");
            GlobalVariable.symbolTable.addSymbol(symbol);
            Quadruple quadruple = new Quadruple("", "PARA", this.ident.getWord(), "");
            GlobalVariable.quadruples.add(quadruple);
        } else if (this.dim == 1) {
            Symbol symbol = new Symbol(this.ident.getWord(), "PARA");
            symbol.setDim(this.dim);
            GlobalVariable.symbolTable.addSymbol(symbol);
            Quadruple quadruple = new Quadruple("", "PARA", this.ident.getWord() + "[]", "");
            GlobalVariable.quadruples.add(quadruple);
        } else if (this.dim == 2) {
            int dim2 = this.constExpList.get(0).generate();
            Symbol symbol = new Symbol(this.ident.getWord(), "PARA");
            symbol.setDim(this.dim);
            symbol.setDim2(dim2);
            Quadruple quadruple = new Quadruple("", "PARA", this.ident.getWord() + "[]" + "[" + dim2 + "]", "");
            quadruple.setDim2(dim2);
            GlobalVariable.quadruples.add(quadruple);
            GlobalVariable.symbolTable.addSymbol(symbol);
        }
    }
}
