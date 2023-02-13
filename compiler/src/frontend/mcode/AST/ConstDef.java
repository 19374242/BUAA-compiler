package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.Symbol;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class ConstDef {
    public int dim=0;
    public WordEntity ident=null;
    public ArrayList<ConstExp> constExps=new ArrayList<>();
    public ConstInitVal constInitVal=null;

    public void generate() {
        if(dim==0){
            int arg = this.constInitVal.constExp.generate();//定义的值
            Quadruple quadruple = new Quadruple(this.ident.getWord(),"DECL_CONST_IDENT", Integer.toString(arg), "");
            GlobalVariable.quadruples.add(quadruple);
            Symbol symbol = new Symbol(ident.getWord(),"CONST");
            symbol.setDim(dim);
            symbol.setValue(arg);
            GlobalVariable.symbolTable.addSymbol(symbol);
        } else if (dim==1) {
            int dim1= this.constExps.get(0).generate();//一维数组长度
            Symbol symbol = new Symbol(this.ident.getWord(),"CONST");
            symbol.setDim(this.dim);
            symbol.setDim1(dim1);
            Quadruple quadruple0 = new Quadruple(this.ident.getWord(),"DECL_ARR", "", "");
            quadruple0.setLen(dim1);        //数组长度
            GlobalVariable.quadruples.add(quadruple0);
            for (int i = 0; i < dim1; i++) {
                int arg = this.constInitVal.getConstExp(i).generate();//定义的值
                Quadruple quadruple1 = new Quadruple(this.ident.getWord(),"ASSIGNMENT_CONST_ARRAY", Integer.toString(arg), "");
                quadruple1.setIndex(Integer.toString(i));//设值
                GlobalVariable.quadruples.add(quadruple1);
                symbol.addValueList(arg);        //初始值
            }
            GlobalVariable.symbolTable.addSymbol(symbol);
        } else if (dim==2) {
            int dim1 = this.constExps.get(0).generate();
            int dim2 = this.constExps.get(1).generate();
            Symbol symbol = new Symbol(this.ident.getWord(),"CONST");
            symbol.setDim(this.dim);
            symbol.setDim1(dim1);
            symbol.setDim2(dim2);
            Quadruple quadruple0 = new Quadruple(this.ident.getWord(),"DECL_ARR", "", "");
            quadruple0.setLen(dim1*dim2);
            quadruple0.setDim2(dim2);
            GlobalVariable.quadruples.add(quadruple0);
            for (int i = 0; i < dim1; i++) {
                for (int j = 0; j < dim2; j++) {
                    int arg = this.constInitVal.getConstExp(i, j).generate();
                    Quadruple quadruple1 = new Quadruple(this.ident.getWord(),"ASSIGNMENT_CONST_ARRAY", Integer.toString(arg), "");
                    quadruple1.setIndex(Integer.toString(i * dim2 + j));
                    GlobalVariable.quadruples.add(quadruple1);
                    symbol.addValueList(arg);
                }
            }
            GlobalVariable.symbolTable.addSymbol(symbol);
        }
    }
}
