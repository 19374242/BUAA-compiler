package frontend.mcode.Symbols;

import backend.RegisterPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymbolTable {
    private int level=0;//block表层次（数量）
    private ArrayList<BlockTable> tableList=new ArrayList<>();//block表
    private RegisterPool registerPool=new RegisterPool();//寄存器池
    private HashMap<String, Integer> stringConstMap=new HashMap<>();//print常量
    private int stringCount=0;//print常量数量
    private ArrayList<Integer> stackPointerLevelList=new ArrayList<>();//记录栈的目前层次
    private int paraNum=0;//参数数量
    public SymbolTable() {
        this.tableList.add(new BlockTable(this.registerPool, 0));
        stackPointerLevelList.add(0);
    }
    //在相应层次的块表中加入的symbol（函数或变量）
    public void addSymbol(Symbol symbol) {
        tableList.get(level).addSymbol(symbol);
    }
    //在相应层次的块表中删除的symbol（函数和变量）
    public void removeSymbol(String name) {
        tableList.get(level).getMap().remove(name);
        tableList.get(level).getFuncMap().remove(name);
    }
    //添加新的块表
    public void addBlockTable() {
        level++;
        tableList.add(new BlockTable(registerPool, tableList.get(level - 1).getStackOffset()));
    }
    //删除块表
    public void removeBlockTable() {
        tableList.remove(level);
        level--;
    }
    //删除最后一个栈
    public void removeLastStack() {
        int index = stackPointerLevelList.size() - 1;
        stackPointerLevelList.remove(index);
    }
    public void addStringConst(String string) {
        stringConstMap.put(string, stringCount++);
    }
    //得到符号表内相应变量的值
    public int getSymbolValue(String name) {
        Pattern patternArr = Pattern.compile("(.+?)\\[(.+)\\]");//a[b]
        Matcher matcherArr = patternArr.matcher(name);
        if (isImm(name)) {//如果是数字直接返回
            return Integer.parseInt(name);
        } else if (matcherArr.matches()) { //如果是数组
            String ident = matcherArr.group(1);//a
            String index = matcherArr.group(2);//b
            if (containSymbol(ident)) { //符号表内包含该变量
                int num=getSymbolValue(index);//可能为q[i]，因此要再找一遍
                if (num>=0&&num<getSymbol(ident).getValueList().size()) { //下标在数组定义的范围内
                    return getSymbol(ident).getValueList().get(num);
                } else {
                    return 0;
                }
            } else {
                System.out.println("getSymbolValue wrong1: " + name + " not exists!");
                return 0;
            }
        } else if (containSymbol(name)){ //如果不是数组且包含变量
            return getSymbol(name).getValue();
        } else {
            if(name.equals("")) return 0;
            System.out.println("getSymbolValue wrong: " + name + " not exists!");
            return 0;
        }
    }
    public boolean isImm(String arg) {
        Pattern patternImm = Pattern.compile("-?[0-9]+");
        Matcher matcherImm = patternImm.matcher(arg);
        return matcherImm.matches();
    }
    //所有非函数变量
    public boolean containSymbol(String name) {
        for (BlockTable blockTable : tableList) {
            if (blockTable.contain(name)) {
                return true;
            }
        }
        return false;
    }
    //非全局变量的非函数变量
    public boolean containLocalSymbol(String name) {
        for (int i = 1; i < tableList.size(); i++) {
            if (tableList.get(i).contain(name)) {
                return true;
            }
        }
        return false;
    }
    //得到非函数变量
    public Symbol getSymbol(String name) {
        Symbol symbol = null;
        for (BlockTable blockTable : tableList) {
            if (blockTable.contain(name)) {
                symbol = blockTable.getMap().get(name);
            }
        }
        return symbol;
    }
    //包含函数
    public boolean containFuncSymbol(String name) {
        return tableList.get(0).containFunc(name);
    }//函数必然在第一层
    //得到函数
    public Symbol getFuncSymbol(String name) {
        Symbol symbol = null;
        if (tableList.get(0).containFunc(name)) {
            symbol = tableList.get(0).getFuncMap().get(name);
        }
        return symbol;
    }
    //设置sp偏移,非函数(函数不需要)
    public void setSymbolOffset(String name) {
        if (tableList.get(level).contain(name)) {
            tableList.get(level).setOffset(name);
        } else {
            System.out.println("Something wrong with setSymbolOffset");
        }
    }
    //设置数组的sp偏移（stackOffset += len * 4）
    public void setArrOffset(String name, int len) {
        if (tableList.get(level).contain(name)) {
            tableList.get(level).setArrOffset(name, len);
        } else {
            System.out.println("Something wrong with setArrOffset");
        }
    }
    public int getSymbolOffset(String name) {
        int symbolLevel = 0;
        //第几个block
        for (int i = 0; i < tableList.size(); i++) {          //参数层次
            if (tableList.get(i).contain(name)) {
                symbolLevel = i;
            }
        }
        if (symbolLevel >= stackPointerLevelList.get(stackPointerLevelList.size() - 1)) {  //参数层次大于等于目前层次（大于可能是因为1>0）
            // 在现在栈上
            return -tableList.get(symbolLevel).getMap().get(name).getOffset();
        } else {                                                                             //参数层次小于目前层次，需要把每次sp减的数加上去
            // 在过去的栈中，不一定是上一层，例如 fun(f1(f2(a)));
            int symbolStackPointLevelIndex = 0;
            for (int i = 1; i < stackPointerLevelList.size(); i++) {
                if (symbolLevel < stackPointerLevelList.get(i)) {           //找到该变量所在的栈层
                    symbolStackPointLevelIndex = i - 1;
                    break;
                }
            }
            int allStackOffset = 0;
            for (int i = stackPointerLevelList.size() - 1; i > symbolStackPointLevelIndex; i--) {
                allStackOffset += tableList.get(stackPointerLevelList.get(i) - 1).getStackOffset();
            }
            return allStackOffset - tableList.get(symbolLevel).getMap().get(name).getOffset();
        }
    }
    //函数跳转,清空栈
    public void createNewStack() {
        tableList.get(level).setStackOffset(0);
        stackPointerLevelList.add(level);
        //System.out.println(level+","+stackPointerLevelList.size());
    }
    public int getLevel() {
        return level;
    }
    public ArrayList<BlockTable> getTableList() {
        return tableList;
    }

    public HashMap<String, Integer> getStringConstMap() {
        return stringConstMap;
    }
    public String generateParaName() {
        return "@para" + this.paraNum++;
    }

    public RegisterPool getRegisterPool() {
        return registerPool;
    }


}
