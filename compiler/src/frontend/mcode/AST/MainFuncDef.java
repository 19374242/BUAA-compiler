package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.BlockTable;

public class MainFuncDef {
    public Block block=null;

    public void generate() {
        Quadruple quadruple1 = new Quadruple("", "MAIN", "", "");
        GlobalVariable.quadruples.add(quadruple1);
        this.block.generate();
        Quadruple quadruple2 = new Quadruple("","MAIN_END", "", "");
        GlobalVariable.quadruples.add(quadruple2);

//        BlockTable blockTable=GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel());
//        for(String name:blockTable.getMap().keySet()){
//            System.out.println(blockTable.getMap().get(name).getName()+","+blockTable.getMap().get(name).getValue());
//        }
    }
}
