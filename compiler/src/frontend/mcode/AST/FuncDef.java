package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.Symbol;
import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class FuncDef {
    public int paramsNum=0;
    public ArrayList<Integer> paramsDimList=new ArrayList<>();
    public String funcType="";
    public WordEntity ident=null;
    public FuncFParams funcFParams;
    public Block block=null;

    public void generate() {
        Quadruple quadrupleBegin = new Quadruple("","FUNC_BEGIN", this.ident.getWord(), "");
        GlobalVariable.quadruples.add(quadrupleBegin);
        String type="";
        if(funcType.equals("int")){
            type="INT_FUNC";
        } else {
            type="VOID_FUNC";
        }
        Symbol symbol= new Symbol(this.ident.getWord(),type, this.paramsNum, this.paramsDimList);
        GlobalVariable.symbolTable.addSymbol(symbol);
        Quadruple quadrupleFunc = new Quadruple("",type, this.ident.getWord(), "");
        GlobalVariable.quadruples.add(quadrupleFunc);
        GlobalVariable.symbolTable.addBlockTable();
        if (this.funcFParams != null) {
            this.funcFParams.generate();
        }
        this.block.generate(true);

        GlobalVariable.symbolTable.removeBlockTable();
        Quadruple quadrupleEnd = new Quadruple("", "FUNC_END", this.ident.getWord(), "");
        GlobalVariable.quadruples.add(quadrupleEnd);
    }
}
