package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.Symbol;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class VarDef {
    public int dim=0;
    public WordEntity ident=null;
    public ArrayList<ConstExp> constExps=new ArrayList<>();
    public InitVal initVal=null;

    public void generate() {
        if (this.dim == 0) {
            String arg = (this.initVal == null) ? "" : this.initVal.getExp().generate();
            Quadruple quadruple = new Quadruple(this.ident.getWord(), "DECL_VAR_IDENT", arg, "");
            GlobalVariable.quadruples.add(quadruple);
            Symbol symbol = new Symbol(this.ident.getWord(),"VAR");
            symbol.setDim(0);
            if (this.initVal != null) {
                symbol.setValue(GlobalVariable.symbolTable.getSymbolValue(arg));
            }
            GlobalVariable.symbolTable.addSymbol(symbol);
        } else if (this.dim == 1) {
            int dim1 = this.constExps.get(0).generate();
            Symbol symbol = new Symbol(this.ident.getWord(),"VAR");
            symbol.setDim(1);
            symbol.setDim1(dim1);
            Quadruple quadruple0 = new Quadruple(this.ident.getWord(),"DECL_ARR", "", "");
            quadruple0.setLen(dim1);
            GlobalVariable.quadruples.add(quadruple0);
            for (int i = 0; i < dim1; i++) {
                if (this.initVal == null) {
                    break;//不会出现部分初始化
                } else {
                    String arg ="";
                    if(i<initVal.expSize()) arg=initVal.getExp(i).generate();
                    else System.out.println("vardef error");
                    Quadruple quadruple = new Quadruple(this.ident.getWord(),"ASSIGNMENT_VAR_ARRAY", arg, "");
                    quadruple.setIndex(Integer.toString(i));
                    GlobalVariable.quadruples.add(quadruple);
                    symbol.getValueList().add(GlobalVariable.symbolTable.getSymbolValue(arg));
                }
            }
            GlobalVariable.symbolTable.addSymbol(symbol);
        } else if (this.dim == 2) {
            int dim1= this.constExps.get(0).generate();
            int dim2 = this.constExps.get(1).generate();
            Symbol symbol = new Symbol(this.ident.getWord(),"VAR");
            symbol.setDim(this.dim);
            symbol.setDim1(dim1);
            symbol.setDim2(dim2);
            Quadruple quadruple0 = new Quadruple(this.ident.getWord(),"DECL_ARR", "", "");
            quadruple0.setLen(dim1*dim2);
            quadruple0.setDim2(dim2);
            GlobalVariable.quadruples.add(quadruple0);
            for (int i = 0; i < dim1; i++) {
                for (int j = 0; j < dim2; j++) {
                    if (this.initVal == null) {
                        break;
                    } else {
                        String arg ="";
                        if(i<initVal.expSize()) arg=initVal.getExp(i,j).generate();
                        else System.out.println("vardef error");
                        Quadruple quadruple = new Quadruple(this.ident.getWord(),"ASSIGNMENT_VAR_ARRAY", arg, "");
                        quadruple.setIndex(Integer.toString(i * dim2 + j));
                        GlobalVariable.quadruples.add(quadruple);
                        symbol.getValueList().add(GlobalVariable.symbolTable.getSymbolValue(arg));
                    }
                }
            }
            GlobalVariable.symbolTable.addSymbol(symbol);
        }
    }
}
