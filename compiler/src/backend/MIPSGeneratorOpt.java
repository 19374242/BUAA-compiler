package backend;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MIPSGeneratorOpt {
    RegisterPool registerPool= GlobalVariable.symbolTable.getRegisterPool();
    private ArrayList<String> mips=new ArrayList<>();
    public MIPSGeneratorOpt(){
        registerPool.clearAll();
        getData();
        getText();
    }
    private void getData(){
        HashMap<String, Symbol> GlobalVar = GlobalVariable.symbolTable.getTableList().get(0).getMap();
        mips.add(".data");
        for (String name : GlobalVar.keySet()) { //全局变量
            Symbol symbol = GlobalVar.get(name);
            symbol.setOffset(0); // 防止被误判未入栈
            if (symbol.getDim() == 0) {
                mips.add(name + ": .word " + symbol.getValue());
            } else if (symbol.getDim() == 1) {
                if(symbol.getValueList().size()==0) mips.add(name + ": .word 0:" + symbol.getDim1());//未赋初值全为0
                else{//完全赋初值了就赋初值（不可能不完全赋初值）
                    String code = name + ": .word ";
                    for (int i = symbol.getDim1() - 1; i >= 0; i--) {
                        code=code+symbol.getValueList().get(i) + ",";
                    }
                    mips.add(code);
                }
            } else if (symbol.getDim() == 2) {
                if(symbol.getValueList().size()==0) mips.add(name + ": .word 0:" + symbol.getDim1()*symbol.getDim2());
                else{
                    String code = name + ": .word ";
                    for (int i = symbol.getDim1() * symbol.getDim2() - 1; i >= 0; i--) {
                        code=code+symbol.getValueList().get(i) + ",";
                    }
                    mips.add(code);
                }
            }
        }
        HashMap<String,Integer> printStr=GlobalVariable.symbolTable.getStringConstMap();
        for (String str : printStr.keySet()) { //print中字符串
            int index = printStr.get(str);
            mips.add("str" + index + ": .asciiz \"" + str + "\"");
        }
    }
    private void getText(){
        mips.add(".text");
        mips.add("move $fp, $sp");
        mips.add("jal main");
        mips.add("li $v0, 10");
        mips.add("syscall");
        for (Quadruple quadruple:GlobalVariable.quadruples){
            mips.add("# " + quadruple.getOp() + ":" + quadruple);
            if (quadruple.getOp().equals("MAIN")) {
                getMAIN(quadruple);
            } else if (quadruple.getOp().equals("MAIN_END")) {
                continue;
            } else if (quadruple.getOp().equals("BLOCK_BEGIN")) {
                getBLOCK_BEGIN(quadruple);
            } else if (quadruple.getOp().equals("BLOCK_END")) {
                getBLOCK_END(quadruple);
            } else if (quadruple.getOp().equals("DECL_CONST_IDENT")) {              //定义   const 非数组
                // 非全局变量
                if (GlobalVariable.symbolTable.getLevel() != 0) {
                    getDECL_CONST_IDENT(quadruple);
                }
            } else if (quadruple.getOp().equals("DECL_VAR_IDENT")) {         //定义   非const 非数组
                // 非全局变量
                if (GlobalVariable.symbolTable.getLevel() != 0) {
                    getDECL_VAR_IDENT(quadruple);
                }
            } else if (quadruple.getOp().equals("ASSIGNMENT_CONST_ARRAY")) {
                // 非全局变量
                if (GlobalVariable.symbolTable.getLevel() != 0) {
                    getASSIGNMENT_CONST_ARRAY(quadruple);
                }
            } else if (quadruple.getOp().equals("DECL_ARR")) {
                // 非全局变量
                if (GlobalVariable.symbolTable.getLevel() != 0) {
                    getDECL_ARR(quadruple);
                }
            } else if (quadruple.getOp().equals("ASSIGNMENT_VAR_ARRAY")) {
                // 非全局变量
                if (GlobalVariable.symbolTable.getLevel() != 0) {
                    getASSIGNMENT_VAR_ARRAY(quadruple);
                }
            } else if (quadruple.getOp().equals("ADD")) {
                getADD(quadruple);
            } else if (quadruple.getOp().equals("SUB")) {
                getSUB(quadruple);
            } else if (quadruple.getOp().equals("MUL")) {
                getMUL(quadruple);
            } else if (quadruple.getOp().equals("DIV")) {
                getDIV(quadruple);
            } else if (quadruple.getOp().equals("MOD")) {
                getMOD(quadruple);
            } else if (quadruple.getOp().equals("PRINT_STR")) {
                getPRINT_STR(quadruple);
            } else if (quadruple.getOp().equals("PRINT_INT")) {
                getPRINT_INT(quadruple);
            } else if (quadruple.getOp().equals("GETINT")) {
                getGETINT(quadruple);
            } else if (quadruple.getOp().equals("ASSIGNMENT")) {  //赋值（等于）
                getASSIGNMENT(quadruple);
            } else if (quadruple.getOp().equals("INT_FUNC")) {
                getINT_FUNC(quadruple);
            } else if(quadruple.getOp().equals("VOID_FUNC")){
                getVOID_FUNC(quadruple);
            } else if (quadruple.getOp().equals("FUNC_BEGIN")) {
                getFUNC_BEGIN(quadruple);
            } else if (quadruple.getOp().equals("PARA")) {  //函数参数
                getPARA(quadruple);
            } else if (quadruple.getOp().equals("FUNC_END")) {
                getFUNC_END();
            } else if (quadruple.getOp().equals("CALL_BEGIN")) {
                getCALL_BEGIN(quadruple);
            } else if (quadruple.getOp().equals("PUSH")) {
                getPUSH(quadruple);
            } else if (quadruple.getOp().equals("CALL")) {
                getCALL(quadruple);
            } else if (quadruple.getOp().equals("RETURN")) {
                getRET(quadruple);
            } else if (quadruple.getOp().equals("BEQ")) {
                getBEQ(quadruple);
            } else if (quadruple.getOp().equals("BNE")) {
                getBNE(quadruple);
            } else if (quadruple.getOp().equals("EQL")) {//set $T1 on b == 1
                getEQL(quadruple);
            } else if (quadruple.getOp().equals("NEQ")) {
                getNEQ(quadruple);
            } else if (quadruple.getOp().equals("GRE")) {//>
                getGRE(quadruple);
            } else if (quadruple.getOp().equals("LSS")) {//<
                getLSS(quadruple);
            } else if (quadruple.getOp().equals("GEQ")) {//>=
                getGEQ(quadruple);
            } else if (quadruple.getOp().equals("LEQ")) {//<=
                getLEQ(quadruple);
            } else if (quadruple.getOp().equals("LABEL")) {
                getLABEL(quadruple);
            } else if (quadruple.getOp().equals("GOTO")) {
                getGOTO(quadruple);
            } else if (quadruple.getOp().equals("WHILE_BEGIN")||quadruple.getOp().equals("WHILE_END")) {
                continue;
            } else {
                mips.add("9876");
            }
        }
    }
    public void getINT_FUNC(Quadruple quadruple){
        mips.add(quadruple.getArg1() + ":");
    }
    public void getVOID_FUNC(Quadruple quadruple){
        mips.add(quadruple.getArg1() + ":");
    }
    public void getFUNC_BEGIN(Quadruple quadruple){
        GlobalVariable.symbolTable.addBlockTable();
    }
    public void getPARA(Quadruple quadruple) {
        String arg = quadruple.getArg1();
        Pattern pattern1 = Pattern.compile("(.+?)\\[\\]");//a[]
        Pattern pattern2 = Pattern.compile("(.+?)\\[\\]\\[(.+)\\]");//a[][b]
        Matcher matcher1 = pattern1.matcher(arg);
        Matcher matcher2 = pattern2.matcher(arg);
        // 数组
        if (matcher1.matches()) {//a[]
            String ident = matcher1.group(1);//a
            Symbol symbol = new Symbol(ident, "VAR");
            symbol.setDim(1);
            symbol.setArrAddr(true);//是地址
            GlobalVariable.symbolTable.addSymbol(symbol);//加在该层符号表
            GlobalVariable.symbolTable.setSymbolOffset(ident);//给这个变量存在一个sp偏移量中
        } else if (matcher2.matches()) {
            String ident = matcher2.group(1);
            Symbol symbol = new Symbol(ident,"VAR");
            symbol.setDim(2);
            symbol.setArrAddr(true);
            int dim2 = quadruple.getDim2();
            symbol.setDim2(dim2);
            GlobalVariable.symbolTable.addSymbol(symbol);
            GlobalVariable.symbolTable.setSymbolOffset(ident);
        } else {
            Symbol symbol = new Symbol(arg,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            GlobalVariable.symbolTable.setSymbolOffset(arg);
            if (quadruple.getsRegFlag() == 1 && registerPool.isSRegAvailable()) {
                registerPool.mapSRegister(symbol);
                load(arg, registerPool.getSRegister(symbol));
            }
        }
    }

    public void getFUNC_END() {
        mips.add("jr $ra");
        //打印删除的符号表
//        System.out.println("------------------symbolTable-mips--------------------");
//        BlockTable blockTable=GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel());
//        for(String name:blockTable.getMap().keySet()){
//            System.out.println(blockTable.getMap().get(name).getName()+","+blockTable.getMap().get(name).getValue());
//        }
//        System.out.println("------------------symbolTable-mips--------------------");
        GlobalVariable.symbolTable.removeBlockTable();
        registerPool.clearAll();
    }
    public void getASSIGNMENT(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String dst = quadruple.getDst();
        if (!GlobalVariable.symbolTable.containSymbol(dst)) {
            Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
            Matcher matcherTemp = patternTemp.matcher(dst);
            if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                String tmp = registerPool.allocTRegister(dst);
                load(arg1, tmp);
            } else {//原方法
                Symbol symbol = new Symbol(dst,"VAR");
                GlobalVariable.symbolTable.addSymbol(symbol);
                String valueReg = load(arg1);//这句话不能放在前面，因为使用寄存器时不存符号表，无法load
                store(dst, valueReg);
            }
        } else {
            String valueReg = load(arg1);
            store(dst, valueReg);
        }
    }
    public void getMAIN(Quadruple quadruple){
        mips.add("main:");
    }
    public void getBLOCK_BEGIN(Quadruple quadruple){
        GlobalVariable.symbolTable.addBlockTable();
    }
    public void getBLOCK_END(Quadruple quadruple){
        GlobalVariable.symbolTable.removeBlockTable();
    }
    public void getDECL_CONST_IDENT(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg = quadruple.getArg1();
        Symbol symbol = new Symbol(dst,"CONST");
        symbol.setValue(Integer.parseInt(arg));
        GlobalVariable.symbolTable.addSymbol(symbol);
        GlobalVariable.symbolTable.setSymbolOffset(dst);
        if (quadruple.getsRegFlag() == 1 && registerPool.isSRegAvailable()) { //如果可以分配
            registerPool.mapSRegister(symbol);
            load(arg, registerPool.getSRegister(symbol));
        } else { //不能只能按老样子
            String argReg = load(arg);
            store(dst, argReg);
        }
    }
    public void getDECL_VAR_IDENT(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg = quadruple.getArg1();
        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        GlobalVariable.symbolTable.setSymbolOffset(dst);
        if (quadruple.getsRegFlag() == 1 && registerPool.isSRegAvailable()) {
            registerPool.mapSRegister(symbol);
            if (!arg.equals("")) {
                load(arg, registerPool.getSRegister(symbol));
            }
        } else {
            if (!arg.equals("")) {
                String argReg = load(arg);
                store(dst, argReg);
            }
        }
    }
    public void getDECL_ARR(Quadruple quadruple) {
        String dst = quadruple.getDst();
        int len = quadruple.getLen();
        int dim2 = quadruple.getDim2();
        Symbol symbol = new Symbol(dst,"VAR");
        symbol.setDim2(dim2);
        for (int i = 0; i < len; i++) {
            symbol.getValueList().add(0);
        }
        GlobalVariable.symbolTable.addSymbol(symbol);
        GlobalVariable.symbolTable.setArrOffset(dst, len);
    }
    public void getASSIGNMENT_CONST_ARRAY(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String index = quadruple.getIndex();
        String arg = quadruple.getArg1();
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            Symbol arr = GlobalVariable.symbolTable.getSymbol(dst);
            arr.getValueList().set(Integer.parseInt(index), Integer.parseInt(arg));
            String valueReg = load(arg);
            String dstAddr = loadArrayAddress(dst, index);
            mips.add("sw " + valueReg + ", " + dstAddr);
        } else {
            System.out.println("getASSIGNMENT_CONST_ARRAY wrong");
        }
    }
    public void getASSIGNMENT_VAR_ARRAY(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String index = quadruple.getIndex();
        String arg = quadruple.getArg1();
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String valueReg = load(arg);
            String dstAddr = loadArrayAddress(dst, index);
            mips.add("sw " + valueReg + ", " + dstAddr);
        } else {
            System.out.println("getASSIGNMENT_VAR_ARRAY wrong");
        }
    }
    public void getGETINT(Quadruple quadruple) {
        String dst = quadruple.getDst();
        mips.add("li $v0, 5");
        mips.add("syscall");
        store(dst, "$v0");
    }
    public void getPRINT_STR(Quadruple quadruple) {
        String str=quadruple.getArg1();
        mips.add("la $a0, str" + GlobalVariable.symbolTable.getStringConstMap().get(str));
        mips.add("li $v0, 4");
        mips.add("syscall");
    }

    public void getPRINT_INT(Quadruple quadruple) {
        String arg = quadruple.getArg1();
        String valueReg = load(arg);
        mips.add("move $a0, " + valueReg);
        mips.add("li $v0, 1");
        mips.add("syscall");
    }
    private void getLABEL(Quadruple quadruple) {
        String label=quadruple.getArg1();
        mips.add(label + ":");
    }
    private void getGOTO(Quadruple quadruple) {
        String label=quadruple.getArg1();
        mips.add("j " + label);
    }
    public void getRET(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        if (!arg1.equals("")) {
            String tmp = load(arg1);
            mips.add("move $v0, " + tmp);
        }
        mips.add("jr $ra");
    }
    public void getCALL(Quadruple quadruple) {
        mips.add("move $fp, $sp");
        mips.add("jal " + quadruple.getArg1());
        //返回后
        GlobalVariable.symbolTable.removeBlockTable();
        GlobalVariable.symbolTable.removeLastStack();
        mips.add("addiu $sp, $sp, " + GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset());
        mips.add("move $fp, $sp");
        //registerPool.clearTRegister();
        int blockOffset;
        //函数调用完毕，把s寄存器加载出来
        for (int i = quadruple.getsRegList().size() - 1; i >= 0; i--) {
            String name = quadruple.getsRegList().get(i);
            load(name, "$s" + i);//因为寄存器没有被清除，所以改变值就行，不用重新激活寄存器（mapsReglist）
            GlobalVariable.symbolTable.removeSymbol(name);
            //获取原来的栈偏移量（去掉ra后的）
            blockOffset = GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset();
            GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).setStackOffset(blockOffset - 4);
        }
        //函数调用完毕，把t寄存器加载出来
        for (int i = quadruple.gettRegList().size() - 1; i >= 0; i--) {
            String name = quadruple.gettRegList().get(i);
            char[] str=name.toCharArray();
            int num=Integer.parseInt(str[str.length-1]+"");
            load(name, "$t" + num);
            GlobalVariable.symbolTable.removeSymbol(name);
            //获取原来的栈偏移量（去掉ra后的）
            blockOffset = GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset();
            GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).setStackOffset(blockOffset - 4);
        }
        String ra = "$RA";
        load(ra, "$ra");
        GlobalVariable.symbolTable.removeSymbol(ra);
        //获取原来的栈偏移量（去掉ra后的）
        blockOffset = GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset();
        GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).setStackOffset(blockOffset - 4);
//        //函数调用完毕，把t寄存器加载出来
//        for (int i = 4; i < 10; i++) {
//            String name=registerPool.gettRegMap().get(i);
//            int level=GlobalVariable.symbolTable.getLevel();
//            if(GlobalVariable.symbolTable.getTableList().get(level).contain(name)){
//                String argAddr = loadIdentAddress(name);    //非全局变量-sp具体位置 全局变量-名称（起始地址）
//                String reg=registerPool.allocTRegister(name);
//                mips.add("lw "+reg+", "+argAddr);
//            }
//        }

    }
    public void getCALL_BEGIN(Quadruple quadruple) {
        // 把t寄存器中的值存入符号表
        for (int i = 4; i < registerPool.gettRegList().size(); i++) {
            if (registerPool.gettRegList().get(i) == 1) {//该寄存器被使用
//                Symbol symbolTemp = new Symbol(registerPool.gettRegMap().get(i), "VAR");
//                GlobalVariable.symbolTable.addSymbol(symbolTemp);
//                store(registerPool.gettRegMap().get(i), "$t" + i);
                Symbol symbol1 = new Symbol("$TRegister" + i, "VAR");
                GlobalVariable.symbolTable.addSymbol(symbol1);
                store("$TRegister" + i, "$t" + i);
                quadruple.gettRegList().add("$TRegister" + i);
            }
        }
        // 把s寄存器中的值存入符号表
        for (int i = 0; i < registerPool.getsRegList().size(); i++) {
            if (registerPool.getsRegList().get(i) == 1) {
                Symbol symbol1 = new Symbol("$SRegister" + i, "VAR");
                GlobalVariable.symbolTable.addSymbol(symbol1);
                store("$SRegister" + i, "$s" + i);
                quadruple.getsRegList().add("$SRegister" + i);
            }
        }
        //ra存入符号表
        Symbol symbol = new Symbol("$RA","VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        store("$RA", "$ra");


        mips.add("subu $sp, $sp, " + GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset());
        //创建新的符号表，栈
        GlobalVariable.symbolTable.addBlockTable();
        GlobalVariable.symbolTable.createNewStack();
    }
    public void getPUSH(Quadruple quadruple) {
        String dst = GlobalVariable.symbolTable.generateParaName();
        //在push中，arg值f(a[1])中的a[1],而pushdim是a[1]定义时维度减去现在使用维度
        String arg = quadruple.getArg1();//现在变量名称
        int pushDim = quadruple.getPushDim();//变量定义时维度-使用时维度
        Symbol symbol = new Symbol(dst,"PARA");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String valueReg = "";
        if (pushDim == 0) { // 维度一致
            valueReg = load(arg);
        } else { //维度不一致，只有可能定义时2，使用1/0或者定义时1，使用0
            Pattern pattern = Pattern.compile("(.+?)\\[(.+)\\]");//使用时是不是数组（二维数组也是用一维数组表示）
            Matcher matcher = pattern.matcher(arg);
            if (matcher.matches()) { // 数组 a[b]，现在1，原变量为二维数组
                String ident = matcher.group(1);//a
                String i = matcher.group(2);//b
                Symbol symbol1 = GlobalVariable.symbolTable.getSymbol(ident);//定义时数组
                int dim2 = symbol1.getDim2();
                if (isImm(i)) {
                    int indexImm = Integer.parseInt(i);
                    int index4Imm = indexImm * dim2 * 4;
                    int negIndex4Imm = -index4Imm;
                    if (GlobalVariable.symbolTable.containLocalSymbol(ident)) { // 局部数组 a[]
                        if (symbol1.isArrAddr()) {
                            String arrOffsetReg = load(ident);
                            valueReg = registerPool.allocTRegister();
                            mips.add("addiu " + valueReg + ", " + arrOffsetReg + ", " + negIndex4Imm);
                        } else {
                            // 存绝对地址
                            int offset = index4Imm + symbol1.getArrOffset();
                            int negOffset = -offset;
                            valueReg = registerPool.allocTRegister();
                            mips.add("addiu " + valueReg + ", $fp, " + negOffset);
                        }
                    } else { // 全局数组 a[]
                        // 首位反转 + 下标偏移
                        int len4Imm;
                        if (symbol1.getDim() == 1) {
                            len4Imm = (symbol1.getDim1() - 1) * 4;
                        } else {
                            len4Imm = (symbol1.getDim1() * symbol1.getDim2() - 1) * 4;
                        }
                        int offset = len4Imm - index4Imm;
                        valueReg = registerPool.allocTRegister();
                        mips.add("la " + valueReg + ", " + ident + " + " + offset);
                    }
                } else {
                    String iReg = load(i);
                    String index4Reg = registerPool.allocTRegister();
                    mulOpt(index4Reg, iReg, Integer.toString(dim2));
                    mips.add("sll " + index4Reg + ", " + index4Reg + ", " + 2);
                    if (GlobalVariable.symbolTable.containLocalSymbol(ident)) { // 局部数组 a[]
                        if (symbol1.isArrAddr()) {
                            String arrOffsetReg = load(ident);
                            valueReg = registerPool.allocTRegister();
                            mips.add("subu " + valueReg + ", " + arrOffsetReg + ", " + index4Reg);
                        } else {
                            // 存绝对地址
                            valueReg = registerPool.allocTRegister();
                            mips.add("addiu " + valueReg + ", " + index4Reg + ", " + symbol1.getArrOffset());
                            mips.add("subu " + valueReg + ", $fp, " + valueReg);
                        }
                    } else { // 全局数组 a[]
                        // 首位反转 + 下标偏移
                        String len4Reg = registerPool.allocTRegister();
                        if (symbol1.getDim() == 1) {
                            mips.add("li " + len4Reg + ", " + (symbol1.getDim1() - 1) * 4);
                        } else {
                            mips.add("li " + len4Reg + ", " + (symbol1.getDim1() * symbol1.getDim2() - 1) * 4);
                        }
                        String offsetReg = registerPool.allocTRegister();
                        mips.add("subu " + offsetReg + ", " + len4Reg + ", " + index4Reg);
                        // 数组首地址偏移
                        // 存绝对地址
                        valueReg = registerPool.allocTRegister();
                        mips.add("la " + valueReg + ", " + ident + "(" + offsetReg + ")");
                    }
                }
            } else { // 数组 a
                Symbol symbol1 = GlobalVariable.symbolTable.getSymbol(arg);
                if (GlobalVariable.symbolTable.containLocalSymbol(arg)) { // 局部数组 a
                    if (symbol1.isArrAddr()) {
                        valueReg = load(arg);
                    } else {
                        // 存绝对地址
                        valueReg = registerPool.allocTRegister();
                        int negArrOffset = -symbol1.getArrOffset();
                        mips.add("addiu " + valueReg + ", $fp, " + negArrOffset);
                    }
                } else { // 全局数组 a
                    // 首尾反转
                    int len4Imm;
                    if (symbol1.getDim() == 1) {
                        len4Imm = (symbol1.getDim1() - 1) * 4;
                    } else {
                        len4Imm = (symbol1.getDim1() * symbol1.getDim2() - 1) * 4;
                    }
                    valueReg = registerPool.allocTRegister();
                    mips.add("la " + valueReg + ", " + arg + " + " + len4Imm);
                }
            }
        }
        GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).clearVar();
        store(dst, valueReg);
    }


    public void getADD(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        if (isImm(arg2)) {
            String t1 = load(arg1);
            int imm = Integer.parseInt(arg2);
            if (!GlobalVariable.symbolTable.containSymbol(dst)) {//左边的数不存在
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(dst);
                if (matcherTemp.matches() && registerPool.isTRegAvailable()) {//是中间变量且T寄存器有剩余
                    String tmp = registerPool.allocTRegister(dst);//给tmp分配T寄存器（$t4-$t9）
                    mips.add("addiu " + tmp + ", " + t1 + ", " + imm);
                } else { //否则就按原来的方式
                    Symbol symbol = new Symbol(dst,"VAR");
                    GlobalVariable.symbolTable.addSymbol(symbol);
                    String tmp = registerPool.allocTRegister();
                    mips.add("addiu " + tmp + ", " + t1 + ", " + imm);
                    store(dst, tmp);
                }
            } else {//左边的数存在，没办法只能更新，不然会出错
                String tmp = registerPool.allocTRegister();
                mips.add("addiu " + tmp + ", " + t1 + ", " + imm);
                store(dst, tmp);
            }
        } else if (isImm(arg1)) {//同理
            int imm = Integer.parseInt(arg1);
            String t2 = load(arg2);
            if (!GlobalVariable.symbolTable.containSymbol(dst)) {
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(dst);
                if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                    String tmp = registerPool.allocTRegister(dst);
                    mips.add("addiu " + tmp + ", " + t2 + ", " + imm);
                } else {
                    Symbol symbol = new Symbol(dst,"VAR");
                    GlobalVariable.symbolTable.addSymbol(symbol);
                    String tmp = registerPool.allocTRegister();
                    mips.add("addiu " + tmp + ", " + t2 + ", " + imm);
                    store(dst, tmp);
                }
            } else {
                String tmp = registerPool.allocTRegister();
                mips.add("addiu " + tmp + ", " + t2 + ", " + imm);
                store(dst, tmp);
            }
        } else {//都不是数字
            String t1 = load(arg1);
            String t2 = load(arg2);
            if (!GlobalVariable.symbolTable.containSymbol(dst)) {
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(dst);
                if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                    String t = registerPool.allocTRegister(dst);
                    mips.add("addu " + t + ", " + t1 + ", " + t2);
                } else {
                    Symbol symbol = new Symbol(dst,"VAR");
                    GlobalVariable.symbolTable.addSymbol(symbol);
                    String tmp = registerPool.allocTRegister();
                    mips.add("addu " + tmp + ", " + t1 + ", " + t2);
                    store(dst, tmp);
                }
            } else {
                String tmp = registerPool.allocTRegister();
                mips.add("addu " + tmp + ", " + t1 + ", " + t2);
                store(dst, tmp);
            }
        }
    }
    public void getSUB(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        if (isImm(arg2)) {
            String t1 = load(arg1);
            int imm = Integer.parseInt(arg2);
            if (!GlobalVariable.symbolTable.containSymbol(dst)) {
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(dst);
                if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                    String tmp = registerPool.allocTRegister(dst);
                    mips.add("subiu " + tmp + ", " + t1 + ", " + imm);
                } else {
                    Symbol symbol = new Symbol(dst,"VAR");
                    GlobalVariable.symbolTable.addSymbol(symbol);
                    String tmp = registerPool.allocTRegister();
                    mips.add("subiu " + tmp + ", " + t1 + ", " + imm);
                    store(dst, tmp);
                }
            } else {
                String tmp = registerPool.allocTRegister();
                mips.add("subiu " + tmp + ", " + t1 + ", " + imm);
                store(dst, tmp);
            }
        } else {
            String t1 = load(arg1);
            String t2 = load(arg2);
            if (!GlobalVariable.symbolTable.containSymbol(dst)) {
                Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
                Matcher matcherTemp = patternTemp.matcher(dst);
                if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                    String t = registerPool.allocTRegister(dst);
                    mips.add("subu " + t + ", " + t1 + ", " + t2);
                } else {
                    Symbol symbol = new Symbol(dst,"VAR");
                    GlobalVariable.symbolTable.addSymbol(symbol);
                    String t = registerPool.allocTRegister();
                    mips.add("subu " + t + ", " + t1 + ", " + t2);
                    store(dst, t);
                }
            } else {
                String t = registerPool.allocTRegister();
                mips.add("subu " + t + ", " + t1 + ", " + t2);
                store(dst, t);
            }
        }
    }
    public void getMUL(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        if (!GlobalVariable.symbolTable.containSymbol(dst)) {
            Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
            Matcher matcherTemp = patternTemp.matcher(dst);
            if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                if (isImm(arg2)) {
                    String t1 = load(arg1);
                    String t = registerPool.allocTRegister(dst);
                    mulOpt(t, t1, arg2);//2的幂次优化
                } else if (isImm(arg1)) {
                    String t2 = load(arg2);
                    String t = registerPool.allocTRegister(dst);
                    mulOpt(t, t2, arg1);
                } else {
                    String t1 = load(arg1);
                    String t2 = load(arg2);
                    String t = registerPool.allocTRegister(dst);
                    mips.add("mul " + t + ", " + t1 + ", " + t2);
                }
            } else {//不是中间数据或无可用寄存器，则只能按照原方法
                Symbol symbol = new Symbol(dst,"VAR");
                GlobalVariable.symbolTable.addSymbol(symbol);
                if (isImm(arg2)) {
                    String t1 = load(arg1);
                    String t = registerPool.allocTRegister();
                    mulOpt(t, t1, arg2);
                    store(dst, t);
                } else if (isImm(arg1)) {
                    String t2 = load(arg2);
                    String t = registerPool.allocTRegister();
                    mulOpt(t, t2, arg1);
                    store(dst, t);
                } else {
                    String t1 = load(arg1);
                    String t2 = load(arg2);
                    String t = registerPool.allocTRegister();
                    mips.add("mul " + t + ", " + t1 + ", " + t2);
                    store(dst, t);
                }
            }
        } else { //该T已存在，则只能按照原方法
            if (isImm(arg2)) {
                String t1 = load(arg1);
                String t = registerPool.allocTRegister();
                mulOpt(t, t1, arg2);
                store(dst, t);
            } else if (isImm(arg1)) {
                String t2 = load(arg2);
                String t = registerPool.allocTRegister();
                mulOpt(t, t2, arg1);
                store(dst, t);
            } else {
                String t1 = load(arg1);
                String t2 = load(arg2);
                String t = registerPool.allocTRegister();
                mips.add("mul " + t + ", " + t1 + ", " + t2);
                store(dst, t);
            }
        }
    }
    public void mulOpt(String t, String t1, String arg2) {//乘法2的幂次优化(arg2为立即数)
        int imm = Integer.parseInt(arg2);
        int absImm = Math.abs(imm);
        if ((absImm & (absImm - 1)) == 0) { //是不是2的n次
            int leftShift = (int) (Math.log(absImm) / Math.log(2));
            mips.add("sll " + t + ", " + t1 + ", " + leftShift);
            if (imm < 0) {
                mips.add("subu " + t + ", $0, " + t);
            }
        } else {
            String t2 = load(arg2);
            mips.add("mul " + t + ", " + t1 + ", " + t2);
        }
    }
    public void getDIV(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        if (!GlobalVariable.symbolTable.containSymbol(dst)) {
            Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
            Matcher matcherTemp = patternTemp.matcher(dst);
            if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                if (isImm(arg2)) {
                    String t0 = registerPool.allocTRegister();
                    divOpt(t0, t1, arg2);
                    String t = registerPool.allocTRegister(dst);
                    mips.add("move " + t + ", " + t0);
                } else {
                    String t2 = load(arg2);
                    mips.add("div " + t1 + ", " + t2);
                    String t = registerPool.allocTRegister(dst);
                    mips.add("mflo " + t);
                }
            } else {
                Symbol symbol = new Symbol(dst,"VAR");
                GlobalVariable.symbolTable.addSymbol(symbol);
                if (isImm(arg2)) {
                    String t = registerPool.allocTRegister();
                    divOpt(t, t1, arg2);
                    store(dst, t);
                } else {
                    String t2 = load(arg2);
                    String t = registerPool.allocTRegister();
                    mips.add("div " + t1 + ", " + t2);
                    mips.add("mflo " + t);
                    store(dst, t);
                }
            }
        } else {
            if (isImm(arg2)) {
                String t = registerPool.allocTRegister();
                divOpt(t, t1, arg2);
                store(dst, t);
            } else {
                String t2 = load(arg2);
                String t = registerPool.allocTRegister();
                mips.add("div " + t1 + ", " + t2);
                mips.add("mflo " + t);
                store(dst, t);
            }
        }
    }
    public void getMOD(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        if (!GlobalVariable.symbolTable.containSymbol(dst)) {
            Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
            Matcher matcherTemp = patternTemp.matcher(dst);
            if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
                if (isImm(arg2)) {
                    String t10 = registerPool.allocTRegister();
                    mips.add("move " + t10 + ", " + t1);
                    String t0 = registerPool.allocTRegister();
                    divOpt(t0, t1, arg2);
                    mulOpt(t0, t0, arg2);
                    String t = registerPool.allocTRegister(dst);
                    mips.add("subu " + t + ", " + t10 + ", " + t0);
                } else {
                    String t2 = load(arg2);
                    mips.add("div " + t1 + ", " + t2);
                    String t = registerPool.allocTRegister(dst);
                    mips.add("mfhi " + t);
                }
            } else {
                Symbol symbol = new Symbol(dst,"VAR");
                GlobalVariable.symbolTable.addSymbol(symbol);
                if (isImm(arg2)) {
                    String t = registerPool.allocTRegister();
                    divOpt(t, t1, arg2);
                    mulOpt(t, t, arg2);
                    mips.add("subu " + t + ", " + t1 + ", " + t);
                    store(dst, t);
                } else {
                    String t2 = load(arg2);
                    String t = registerPool.allocTRegister();
                    mips.add("div " + t1 + ", " + t2);
                    mips.add("mfhi " + t);
                    store(dst, t);
                }
            }
        } else {
            if (isImm(arg2)) {
                String t = registerPool.allocTRegister();
                divOpt(t, t1, arg2);
                mulOpt(t, t, arg2);
                mips.add("subu " + t + ", " + t1 + ", " + t);
                store(dst, t);
            } else {
                String t2 = load(arg2);
                String t = registerPool.allocTRegister();
                mips.add("div " + t1 + ", " + t2);
                mips.add("mfhi " + t);
                store(dst, t);
            }
        }
    }
    public void divOpt(String t, String n, String arg2) {
        int d = Integer.parseInt(arg2);
        int N = 32;
        long m;
        int l, sh_post;
        int prec = N - 1;
        l = (int) Math.ceil(Math.log(Math.abs(d)) / Math.log(2));
        sh_post = l;
        double mlow = Math.floor(Math.pow(2, N + l) / Math.abs(d));
        double mhigh = Math.floor((Math.pow(2, N + l) + Math.pow(2, N + l - prec)) / Math.abs(d));
        while (Math.floor(mlow / 2.0) < Math.floor(mhigh / 2.0) && sh_post > 0) {
            mlow = Math.floor(mlow / 2.0);
            mhigh = Math.floor(mhigh / 2.0);
            sh_post = sh_post - 1;
        }
        m = (long) mhigh;
        if (Math.abs(d) == 1) {
            t = n;
        } else if (Math.abs(d) == Math.pow(2, l)) {
            mips.add("sra " + t + ", " + n + ", " + (l - 1));
            mips.add("srl " + t + ", " + t + ", " + (N - l));
            mips.add("add " + t + ", " + n + ", " + t);
            mips.add("sra " + t + ", " + t + ", " + l);
        } else if (m < Math.pow(2, N - 1)) {
            mips.add("li " + t + ", " + m);
            mips.add("mult " + t + ", " + n);
            mips.add("mfhi " + t);
            mips.add("sra " + t + ", " + t + ", " + sh_post);
            String temp = registerPool.allocTRegister();
            mips.add("sra " + temp + ", " + n + ", 31");
            mips.add("sub " + t + ", " + t + ", " + temp);
        } else {
            long m1 = m - (long) Math.pow(2, N);
            mips.add("li " + t + ", " + m1);
            mips.add("mult " + t + ", " + n);
            mips.add("mfhi " + t);
            mips.add("add " + t + ", " + n + ", " + t);
            mips.add("sra " + t + ", " + t + ", " + sh_post);
            String temp = registerPool.allocTRegister();
            mips.add("sra " + temp + ", " + n + ", 31");
            mips.add("sub " + t + ", " + t + ", " + temp);
        }
        if (d < 0) {
            mips.add("sub " + t + ", " + "$0, " + t);
        }
    }
    public void getBEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String label = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        mips.add("beq " + t1 + ", " + t2 + ", " + label);
    }
    public void getBNE(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String label = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        mips.add("bne " + t1 + ", " + t2 + ", " + label);
    }
    public void getEQL(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");//是中间值
        Matcher matcherTemp = patternTemp.matcher(dst);
        if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
            String tmp = registerPool.allocTRegister(dst);
            mips.add("seq " + tmp + ", " + t1 + ", " + t2);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("seq " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getNEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
        Matcher matcherTemp = patternTemp.matcher(dst);
        if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
            String tmp = registerPool.allocTRegister(dst);
            mips.add("sne " + tmp + ", " + t1 + ", " + t2);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("sne " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getGRE(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
        Matcher matcherTemp = patternTemp.matcher(dst);
        if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
            String tmp = registerPool.allocTRegister(dst);
            mips.add("sgt " + tmp + ", " + t1 + ", " + t2);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("sgt " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getLSS(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        String t2 = load(arg2);
        Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
        Matcher matcherTemp = patternTemp.matcher(dst);
        if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
            String tmp = registerPool.allocTRegister(dst);
            mips.add("slt " + tmp + ", " + t1 + ", " + t2);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("slt " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getGEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
        Matcher matcherTemp = patternTemp.matcher(dst);
        if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
            String tmp = registerPool.allocTRegister(dst);
            mips.add("sge " + tmp + ", " + t1 + ", " + t2);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("sge " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getLEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);
        Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
        Matcher matcherTemp = patternTemp.matcher(dst);
        if (matcherTemp.matches() && registerPool.isTRegAvailable()) {
            String tmp = registerPool.allocTRegister(dst);
            mips.add("sle " + tmp + ", " + t1 + ", " + t2);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("sle " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }




    //把寄存器arg的值转移到dstReg中
    public void load(String arg, String dstReg) {
        Pattern patternArr = Pattern.compile("(.+?)\\[(.+)\\]");//a[b]二维数组也是用一维数组表示
        Matcher matcherArr = patternArr.matcher(arg);
        if (matcherArr.matches()) {//数组
            String ident = matcherArr.group(1);//a
            String index = matcherArr.group(2);//b
            String arrAddr = loadArrayAddress(ident, index);
            mips.add("lw " + dstReg + ", " + arrAddr);
        } else {
            Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
            Matcher matcherTemp = patternTemp.matcher(arg);
            if (GlobalVariable.symbolTable.containSymbol(arg)) {
                Symbol symbol = GlobalVariable.symbolTable.getSymbol(arg);
                if (registerPool.containSRegister(symbol)) {
                    String sReg = registerPool.getSRegister(symbol);
                    if (!sReg.equals(dstReg)) {
                        mips.add("move " + dstReg + ", " + sReg);
                    } else {
                        String argAddr = loadIdentAddress(arg);
                        mips.add("lw " + dstReg + ", " + argAddr);
                    }
                } else {
                    String argAddr = loadIdentAddress(arg);
                    mips.add("lw " + dstReg + ", " + argAddr);
                }
            } else if (matcherTemp.matches()&&registerPool.isTRegister(arg)) {
                String valueReg = registerPool.getTRegister(arg);
                registerPool.freeTRegister(valueReg);
                mips.add("move " + dstReg + ", " + valueReg);
            } else if (arg.equals("@RET")) {
                mips.add("move " + dstReg + ", $v0");
            } else if (isImm(arg)) {
                mips.add("li " + dstReg + ", " + arg);
            } else {
                System.out.println("load wrong " + dstReg + " " + arg);
            }
        }
    }
    // 传入数组名称和下标，返回数组的该下标元素地址（可以直接用来存取）($t1)
    public String loadArrayAddress(String arg, String index) {
        Symbol symbol = GlobalVariable.symbolTable.getSymbol(arg);
        // index
        String index4Reg = load(index);//index值第几个元素
        mips.add("sll " + index4Reg + ", " + index4Reg + ", " + 2);//正式偏移量
        if (GlobalVariable.symbolTable.containLocalSymbol(arg)) { // a 为局部数组
            if (symbol.isArrAddr()) { // 数组传过来的是地址 a[],a[][b]
                //这个的不同点是，已经有了相对地址，只要减去偏移量即可
                String addrReg = load(arg);
                mips.add("subu " + addrReg + ", " + addrReg + ", " + index4Reg);
                return "(" + addrReg + ")";
            } else { // 数组传过来的不是地址，因此要先找到数组绝对地址
                String addrReg = registerPool.allocTRegister();
                mips.add("subu " + addrReg + ", $fp, " + symbol.getArrOffset());//找到数组起始地址偏移量
                mips.add("subu " + addrReg + ", " + addrReg + ", " + index4Reg);
                return "(" + addrReg + ")";
            }
        } else { // a 为全局数组 全局数组的 label 指向数组尾
            String labelOffsetReg = registerPool.allocTRegister();
            int last=0;
            if(symbol.getDim()==1) last=(symbol.getDim1() - 1) * 4;
            else if(symbol.getDim()==2) last=(symbol.getDim1() * symbol.getDim2()- 1) * 4;
            else System.out.println("loadArrAddr2 Wrong");
            mips.add("li " + labelOffsetReg + ", " + last);
            mips.add("subu " + labelOffsetReg + ", " + labelOffsetReg + ", " + index4Reg);
            return symbol.getName() + "(" + labelOffsetReg + ")";
        }
    }
    public String load(String arg) {
        String valueReg = "";
        Pattern pattern = Pattern.compile("(.+?)\\[(.+)\\]");//a[b]二维数组也是用一维数组表示
        Matcher matcher = pattern.matcher(arg);
        if (matcher.matches()) {//数组
            String ident = matcher.group(1);//a
            String index = matcher.group(2);//b
            String arrAddr = loadArrayAddress(ident, index);
            valueReg = registerPool.allocTRegister();
            mips.add("lw " + valueReg + ", " + arrAddr);
        } else {//非数组,是数组也是起始地址a
            Pattern patternTemp = Pattern.compile("^\\$T[0-9]+");
            Matcher matcherTemp = patternTemp.matcher(arg);
            if (GlobalVariable.symbolTable.containSymbol(arg)) {//全局中存在该非函数变量
                Symbol symbol = GlobalVariable.symbolTable.getSymbol(arg);
                if (registerPool.containSRegister(symbol)) {
                    valueReg = registerPool.getSRegister(symbol);
                } else {
                    String argAddr = loadIdentAddress(arg);    //非全局变量-sp具体位置 全局变量-名称（起始地址）
                    valueReg = registerPool.allocTRegister();
                    mips.add("lw " + valueReg + ", " + argAddr);
                }
            } else if (matcherTemp.matches()) {
                valueReg = registerPool.getTRegister(arg);
                registerPool.freeTRegister(valueReg);//因为存的是中间变量，因此用过后可以直接free掉
            } else if (arg.equals("@RET")) {
                valueReg = "$v0";
            } else if (isImm(arg)) {
                valueReg = registerPool.allocTRegister();
                mips.add("li " + valueReg + ", " + arg);
            } else {
                System.out.println("load wrong");
            }
        }
        return valueReg;
    }
    // 将寄存器值存入栈，注意区分已分配栈和未分配栈
    public void store(String arg, String valueReg) {
        Pattern pattern = Pattern.compile("(.+?)\\[(.+)\\]");
        Matcher matcher = pattern.matcher(arg);
        if (matcher.matches()) {
            String ident = matcher.group(1);
            String index = matcher.group(2);
            String arrAddr = loadArrayAddress(ident, index);
            mips.add("sw " + valueReg + ", " + arrAddr);
        } else {
            if (GlobalVariable.symbolTable.containSymbol(arg)) {
                Symbol symbol = GlobalVariable.symbolTable.getSymbol(arg);
                if (registerPool.containSRegister(symbol)) {
                    String sReg = registerPool.getSRegister(symbol);
                    if (!sReg.equals(valueReg)) {
                        mips.add("move " + sReg + ", " + valueReg);
                    } else {
                        String argAddr = loadIdentAddress(arg);
                        mips.add("sw " + valueReg + ", " + argAddr);
                    }
                } else {
                    // -1 未存入栈
                    if (symbol.getOffset() == -1) {
                        GlobalVariable.symbolTable.setSymbolOffset(arg);
                    }
                    String argAddr = loadIdentAddress(arg);
                    mips.add("sw " + valueReg + ", " + argAddr);
                }
            } else {
                System.out.println("store wrong " + valueReg + " " + arg);
            }
        }
    }
    // 获取内存地址（全局变量或栈）
    public String loadIdentAddress(String arg) {
        if (GlobalVariable.symbolTable.containLocalSymbol(arg)) {
            return GlobalVariable.symbolTable.getSymbolOffset(arg) + "($sp)";
        } else {
            return GlobalVariable.symbolTable.getTableList().get(0).getMap().get(arg).getName();
        }
    }
    public boolean isImm(String arg) {
        Pattern patternImm = Pattern.compile("-?[0-9]+");
        Matcher matcherImm = patternImm.matcher(arg);
        return matcherImm.matches();
    }

    public String getMips() {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<mips.size();i++){
            stringBuffer.append(mips.get(i)+"\n");
        }
        return stringBuffer.toString();
    }
}
