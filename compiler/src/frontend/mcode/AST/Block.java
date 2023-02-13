package frontend.mcode.AST;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.BlockTable;
import frontend.mcode.Symbols.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class Block {
    public ArrayList<BlockItem> blockItems=new ArrayList<>();
    public void generate() {
        Quadruple quadruple1 = new Quadruple("","BLOCK_BEGIN", "main", "");
        GlobalVariable.quadruples.add(quadruple1);
        GlobalVariable.symbolTable.addBlockTable();
        for (BlockItem blockItem : this.blockItems) {
            blockItem.generate();
        }
        Quadruple quadruple2 = new Quadruple("","BLOCK_END", "", "");
        GlobalVariable.quadruples.add(quadruple2);
        //打印删除的符号表
//        System.out.println("------------------symbolTable-generate--------------------");
//        BlockTable blockTable=GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel());
//        for(String name:blockTable.getMap().keySet()){
//            System.out.println(blockTable.getMap().get(name).getName()+","+blockTable.getMap().get(name).getValue());
//        }
//        System.out.println("------------------symbolTable-generate--------------------");

        GlobalVariable.symbolTable.removeBlockTable();
    }
    public void generate(boolean isFunc) {
        for (BlockItem blockItem : this.blockItems) {
            blockItem.generate();
        }
    }
}
