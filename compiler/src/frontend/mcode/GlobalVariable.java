package frontend.mcode;


import frontend.mcode.Symbols.Symbol;
import frontend.mcode.Symbols.SymbolTable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalVariable {
    public static ArrayList<Quadruple> quadruples=new ArrayList<>();
    public static SymbolTable symbolTable=new SymbolTable();
    public static Temp temp=new Temp(symbolTable);


    public static String getQuadruple(){
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=0;i<quadruples.size();i++){
            stringBuffer.append(quadruples.get(i).toString()+"\n");
        }
        return stringBuffer.toString();
    }

    public static void MCodeOpt(){
        int declArrNum=-1;
        for (int i = 1; i < GlobalVariable.quadruples.size(); i++) {
            Quadruple curMCode = GlobalVariable.quadruples.get(i);
            Quadruple preMCode = GlobalVariable.quadruples.get(i - 1);
            // 跳转指令的标签是下一条语句
            if (preMCode.getOp().equals("GOTO")&&curMCode.getOp().equals("LABEL")&&preMCode.getArg1().equals(curMCode.getArg1())) {
                i--;
                GlobalVariable.quadruples.remove(i);// 删除跳转
            }
            if(declArrNum!=-1){//定义时按下面优化会出错
                declArrNum--;
            }
            if(curMCode.getOp().equals("DECL_ARR")){
                declArrNum=curMCode.getLen();
            }
            // 消除表达式赋值产生的冗余变量
            //$T0=i*2
            //$T1=$T0
            //合并为$T1=i*2
            if ((preMCode.getOp().equals("ADD")||preMCode.getOp().equals("SUB")
                    ||preMCode.getOp().equals("MUL")||preMCode.getOp().equals("DIV")
                    ||preMCode.getOp().equals("MOD"))
                    && (curMCode.getOp().equals("ASSIGNMENT"))
                    && (preMCode.getDst().equals(curMCode.getArg1()))) {//计算结果(左)等于等号右边
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(curMCode.getArg1());
                if(matcherTemp.matches()){
                    preMCode.setDst(curMCode.getDst());//计算结果(左)等于等号左边
                    GlobalVariable.quadruples.remove(i);
                    i--;
                }
            }
            // 消除表达式赋值产生的冗余变量(左边是数组)
            //$T0=a+1
            //b[0]=$T0
            //合并为b[0]=a+1
            if ((preMCode.getOp().equals("ADD")||preMCode.getOp().equals("SUB")
                    ||preMCode.getOp().equals("MUL")||preMCode.getOp().equals("DIV")
                    ||preMCode.getOp().equals("MOD"))
                    && (curMCode.getOp().equals("ASSIGNMENT_VAR_ARRAY"))
                    && (preMCode.getDst().equals(curMCode.getArg1()))) {
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(curMCode.getArg1());
                if(matcherTemp.matches()&&(declArrNum==-1||(preMCode.getOp().equals("ASSIGNMENT_VAR_ARRAY")))){

                    preMCode.setDst(curMCode.getDst() + "[" + curMCode.getIndex() + "]");
                    GlobalVariable.quadruples.remove(i);
                    i--;
                }
            }
        }
        //全局寄存器分配
        HashMap<Symbol, Integer> countMap = new HashMap<>();
        HashMap<Symbol, Integer> defMap = new HashMap<>(); // 记录定义变量的四元式
        int whileCount = 0;
        for (int i = 0; i < GlobalVariable.quadruples.size(); i++) {
            Quadruple quadruple = GlobalVariable.quadruples.get(i);
            if (quadruple.getOp().equals("MAIN")) {
                countMap = new HashMap<>();
                defMap = new HashMap<>();
                whileCount = 0;
            } else if (quadruple.getOp().equals("MAIN_END")) {
                // Map.Entry指该类型，.entrySet是输出所有的该类型
                List<Map.Entry<Symbol, Integer>> list = new ArrayList<Map.Entry<Symbol, Integer>>(countMap.entrySet());
                //按数字大小排序(从大到小)
                list.sort(new Comparator<Map.Entry<Symbol, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Symbol, Integer> o1, Map.Entry<Symbol, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                for (int j = 0; j < Math.min(list.size(), 8); j++) {
                    if (list.get(j).getValue() == 0) {
                        break;
                    } else {
                        //可以分配s寄存器
                        Symbol symbol=list.get(j).getKey();
                        Integer num=defMap.get(symbol);
                        GlobalVariable.quadruples.get(num).setsRegFlag(1);
                    }
                }
                countMap = new HashMap<>();
                defMap = new HashMap<>();
                whileCount = 0;
            } else if (quadruple.getOp().equals("FUNC_BEGIN")) {
                symbolTable.addBlockTable();
                countMap = new HashMap<>();
                defMap = new HashMap<>();
                whileCount = 0;
            } else if (quadruple.getOp().equals("FUNC_END")) {
                List<Map.Entry<Symbol, Integer>> list = new ArrayList<Map.Entry<Symbol, Integer>>(countMap.entrySet());
                list.sort(new Comparator<Map.Entry<Symbol, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Symbol, Integer> o1, Map.Entry<Symbol, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                for (int j = 0; j < Math.min(list.size(), 8); j++) {
                    if (list.get(j).getValue() == 0) {
                        break;
                    } else {
                        //可以分配s寄存器
                        Symbol symbol=list.get(j).getKey();
                        Integer num=defMap.get(symbol);
                        GlobalVariable.quadruples.get(num).setsRegFlag(1);
                    }
                }
                countMap = new HashMap<>();
                defMap = new HashMap<>();
                whileCount = 0;
                symbolTable.removeBlockTable();
            } else if (quadruple.getOp().equals("WHILE_BEGIN")) {
                whileCount++;
            } else if (quadruple.getOp().equals("WHILE_END")) {
                whileCount--;
            } else if (quadruple.getOp().equals("BLOCK_BEGIN")) {
                symbolTable.addBlockTable();
            } else if (quadruple.getOp().equals("BLOCK_END")) {
                symbolTable.removeBlockTable();
            } else if (quadruple.getOp().equals("DECL_VAR_IDENT") || quadruple.getOp().equals("DECL_CONST_IDENT")) {
                if (symbolTable.getLevel() != 0) {//不是全局变量
                    String dst = quadruple.getDst();//右值
                    Symbol symbol = new Symbol(dst,"VAR");
                    symbolTable.addSymbol(symbol);
                    defMap.put(symbol, i);
                    countMap.put(symbol, 0);
                }
            } else if (quadruple.getOp().equals("PARA")) {
                String arg = quadruple.getArg1();
                Symbol symbol = new Symbol(arg,"VAR");
                symbolTable.addSymbol(symbol);
                defMap.put(symbol, i);//i为第几个四元式
                countMap.put(symbol, 0);
            }

            String dst = quadruple.getDst();
            String arg1 = quadruple.getArg1();
            String arg2 = quadruple.getArg2();
            if (symbolTable.containSymbol(dst)) {
                Symbol symbol = symbolTable.getSymbol(dst);
                if (countMap.containsKey(symbol)) {
                    int originCount = countMap.get(symbol);
                    countMap.put(symbol, originCount + 10 * whileCount + 1);
                }
            }
            if (symbolTable.containSymbol(arg1)) {
                Symbol symbol = symbolTable.getSymbol(arg1);
                if (countMap.containsKey(symbol)) {
                    int originCount = countMap.get(symbol);
                    countMap.put(symbol, originCount + 10 * whileCount + 1);
                }
            }
            if (symbolTable.containSymbol(arg2)) {
                Symbol symbol = symbolTable.getSymbol(arg2);
                if (countMap.containsKey(symbol)) {
                    int originCount = countMap.get(symbol);
                    countMap.put(symbol, originCount + 10 * whileCount + 1);
                }
            }
        }
    }
}
