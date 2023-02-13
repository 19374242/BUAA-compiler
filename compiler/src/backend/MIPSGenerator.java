package backend;

import frontend.mcode.GlobalVariable;
import frontend.mcode.Quadruple;
import frontend.mcode.Symbols.BlockTable;
import frontend.mcode.Symbols.Symbol;
import frontend.mcode.Symbols.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//mips符号表不需要保存值
public class MIPSGenerator {
    RegisterPool registerPool=GlobalVariable.symbolTable.getRegisterPool();
    private ArrayList<String> mips=new ArrayList<>();
    public MIPSGenerator(){
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
    //与load相似，输入变量名和包含值的寄存器，将值存入变量在内存中的相应位置（符号表不用管，只要mips内部存了就行）
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
                if (symbol.getOffset() == -1) {//该变量未存过(已加入符号表，但没给其分配存储地址)
                    GlobalVariable.symbolTable.setSymbolOffset(arg);//给该变量一个sp偏移量，整个快偏移量加4
                }
                String argAddr = loadIdentAddress(arg);//-4($sp)
                mips.add("sw " + valueReg + ", " + argAddr);
            } else {
                System.out.println("something wrong with store");
            }
        }
    }
    //把变量加载到寄存器并返回该寄存器
    public String load(String arg) {
        String valueReg = "";
        Pattern pattern = Pattern.compile("(.+?)\\[(.+)\\]");//a[b]二维数组也是用一维数组表示
        Matcher matcher = pattern.matcher(arg);
        if (matcher.matches()) {                                           //数组
            String ident = matcher.group(1);//a
            String index = matcher.group(2);//b
            String arrAddr = loadArrayAddress(ident, index);
            valueReg = registerPool.allocTRegister();
            mips.add("lw " + valueReg + ", " + arrAddr);
        } else {                                                          //非数组,是数组也是起始地址a
            if (GlobalVariable.symbolTable.containSymbol(arg)) {          //全局中存在该非函数变量
                String argAddr = loadIdentAddress(arg);                   //非全局变量-sp具体位置 全局变量-名称（起始地址）
                valueReg = registerPool.allocTRegister();
                mips.add("lw " + valueReg + ", " + argAddr);
            } else if (isImm(arg)) {                                    //数
                valueReg = registerPool.allocTRegister();
                mips.add("li " + valueReg + ", " + arg);
            } else if (arg.equals("@RET")) {
                valueReg = registerPool.allocTRegister();
                mips.add("move " + valueReg + ", $v0");
            } else {
                System.out.println("something wrong with load");
            }
        }
        return valueReg;
    }
    //非数组--非全局变量-sp具体位置 全局变量-名称（起始地址）（可以直接用来存取）
    //必须是相对于当前sp的偏移量
    public String loadIdentAddress(String arg) {
        if (GlobalVariable.symbolTable.containLocalSymbol(arg)) { //非全局变量
            return GlobalVariable.symbolTable.getSymbolOffset(arg) + "($sp)";//找到元素相对于当前sp的偏移量
        } else {
            //全局变量名称即为初始地址
            return GlobalVariable.symbolTable.getTableList().get(0).getMap().get(arg).getName();
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


    public void getMAIN(Quadruple quadruple){
        mips.add("main:");
    }
    public void getBLOCK_BEGIN(Quadruple quadruple){
        GlobalVariable.symbolTable.addBlockTable();
    }
    public void getBLOCK_END(Quadruple quadruple){
        GlobalVariable.symbolTable.removeBlockTable();
    }
    //定义   非const 非数组
    public void getDECL_VAR_IDENT(Quadruple quadruple) {
        String dst = quadruple.getDst();//a
        String arg = quadruple.getArg1();//赋的值
        Symbol symbol = new Symbol(dst,"VAR");
        symbol.setValue(GlobalVariable.symbolTable.getSymbolValue(arg));//这里有可能是中间寄存器，也可能是空（定义不赋值（返回0））
        GlobalVariable.symbolTable.addSymbol(symbol);
        String argReg = "$0";//如果没有初始值，就把0赋给相应sp
        if (!arg.equals("")) {//不为空值
            argReg = load(arg);
        }
        store(dst, argReg);
    }
    //定义  const 非数组
    public void getDECL_CONST_IDENT(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg = quadruple.getArg1();
        Symbol symbol = new Symbol(dst,"CONST");
        symbol.setValue(Integer.parseInt(arg));        //已经存入符号表
        GlobalVariable.symbolTable.addSymbol(symbol);
        String argReg = load(arg);
        store(dst, argReg);
    }
    //数组声明（加入符号表＋设置偏移）
    public void getDECL_ARR(Quadruple quadruple) {
        String dst = quadruple.getDst();
        int len = quadruple.getLen();
        int dim2 = quadruple.getDim2();
        Symbol symbol = new Symbol(dst,"VAR");
        symbol.setDim2(dim2);
        GlobalVariable.symbolTable.addSymbol(symbol);
        GlobalVariable.symbolTable.setArrOffset(dst, len);//数组声明时，给数组划分一块区域来赋值，符号表会记住其数组（该符号）初始地址，同时块表栈下移相应位置
    }
    public void getASSIGNMENT_VAR_ARRAY(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String index = quadruple.getIndex();//第几个
        String arg = quadruple.getArg1();//值
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String valueReg = load(arg);
            String dstAddr = loadArrayAddress(dst, index);
            mips.add("sw " + valueReg + ", " + dstAddr);
        } else {
            System.out.println("getASSIGNMENT_VAR_ARRAY wrong");
        }
    }
    public void getASSIGNMENT_CONST_ARRAY(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String index = quadruple.getIndex();
        String arg = quadruple.getArg1();
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String valueReg = load(arg);
            String dstAddr = loadArrayAddress(dst, index);
            mips.add("sw " + valueReg + ", " + dstAddr);
        } else {
            System.out.println("getASSIGNMENT_CONST_ARRAY wrong");
        }
    }
    public void getADD(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);//值或一维数组（二维数组当作一维数组），load都可以将其值存在一个寄存器内返回
        String t2 = load(arg2);
        //有变量，赋给变量的位置，无变量，赋给新的sp位置，所以虽然符号表不更新值，但是一样正确
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String tmp = registerPool.allocTRegister();
            mips.add("addu " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        } else {
            //这里需要注意，这个符号表只保存变量名，不保存值，因为值没有意义
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("addu " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getSUB(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        String t2 = load(arg2);
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String tmp = registerPool.allocTRegister();
            mips.add("subu " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("subu " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getMUL(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        String t2 = load(arg2);
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String tmp = registerPool.allocTRegister();
            mips.add("mul " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("mul " + tmp + ", " + t1 + ", " + t2);
            store(dst, tmp);
        }
    }
    public void getDIV(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        String t2 = load(arg2);
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String tmp = registerPool.allocTRegister();
            mips.add("div " + t1 + ", " + t2);
            mips.add("mflo " + tmp);
            store(dst, tmp);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String tmp = registerPool.allocTRegister();
            mips.add("div " + t1 + ", " + t2);
            mips.add("mflo " + tmp);
            store(dst, tmp);
        }
    }
    public void getMOD(Quadruple quadruple) {
        String dst = quadruple.getDst();
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String t1 = load(arg1);
        String t2 = load(arg2);
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            String tmp = registerPool.allocTRegister();
            mips.add("div " + t1 + ", " + t2);
            mips.add("mfhi " + tmp);
            store(dst, tmp);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            String t = registerPool.allocTRegister();
            mips.add("div " + t1 + ", " + t2);
            mips.add("mfhi " + t);
            store(dst, t);
        }
    }
    public void getBEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();//左边
        String arg2 = quadruple.getArg2();//右边
        String label = quadruple.getDst();//标签
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
        String dst = quadruple.getDst();//新的一个寄存器  $T0
        String t1 = load(arg1);
        String t2 = load(arg2);

        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String tmp = registerPool.allocTRegister();
        mips.add("seq " + tmp + ", " + t1 + ", " + t2);//相等赋值1，不等赋值0
        store(dst, tmp);
    }
    public void getNEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);

        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String tmp = registerPool.allocTRegister();
        mips.add("sne " + tmp + ", " + t1 + ", " + t2);
        store(dst, tmp);
    }
    public void getGRE(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);

        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String tmp = registerPool.allocTRegister();
        mips.add("sgt " + tmp + ", " + t1 + ", " + t2);
        store(dst, tmp);
    }
    public void getLSS(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);

        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String tmp = registerPool.allocTRegister();
        mips.add("slt " + tmp + ", " + t1 + ", " + t2);
        store(dst, tmp);
    }
    public void getGEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);

        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String tmp = registerPool.allocTRegister();
        mips.add("sge " + tmp + ", " + t1 + ", " + t2);
        store(dst, tmp);
    }
    public void getLEQ(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String arg2 = quadruple.getArg2();
        String dst = quadruple.getDst();
        String t1 = load(arg1);
        String t2 = load(arg2);

        Symbol symbol = new Symbol(dst,"VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        String tmp = registerPool.allocTRegister();
        mips.add("sle " + tmp + ", " + t1 + ", " + t2);
        store(dst, tmp);
    }
    private void getGOTO(Quadruple quadruple) {
        String label=quadruple.getArg1();
        mips.add("j " + label);
    }

    private void getLABEL(Quadruple quadruple) {
        String label=quadruple.getArg1();
        mips.add(label + ":");
    }
    public void getPRINT_STR(Quadruple quadruple) {
        String str=quadruple.getArg1();
        mips.add("la $a0, str" + GlobalVariable.symbolTable.getStringConstMap().get(str));
        mips.add("li $v0, 4");
        mips.add("syscall");
    }
    public void getPRINT_INT(Quadruple quadruple) {
        String arg = quadruple.getArg1();
        String valueReg = load(arg);//也可能是a[$T0]，也可能是a，但都可以通过load获得其位置的寄存器
        mips.add("move $a0, " + valueReg);
        mips.add("li $v0, 1");
        mips.add("syscall");
    }
    public void getGETINT(Quadruple quadruple) {
        String dst = quadruple.getDst();
        mips.add("li $v0, 5");
        mips.add("syscall");
        store(dst, "$v0");
    }
    //赋值（等于）
    public void getASSIGNMENT(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        String dst = quadruple.getDst();
        String valueReg = load(arg1);
        if (GlobalVariable.symbolTable.containSymbol(dst)) {
            store(dst, valueReg);
        } else {
            Symbol symbol = new Symbol(dst,"VAR");
            GlobalVariable.symbolTable.addSymbol(symbol);
            store(dst, valueReg);
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
    public void getPARA(Quadruple quadruple) {//函数声明时
        //中间代码生成时保留了二维数组
        String arg = quadruple.getArg1();
        Pattern pattern1 = Pattern.compile("(.+?)\\[\\]");//a[]
        Pattern pattern2 = Pattern.compile("(.+?)\\[\\]\\[(.+)\\]");//a[][b]
        Matcher matcher1 = pattern1.matcher(arg);
        Matcher matcher2 = pattern2.matcher(arg);
        if (matcher1.matches()) {//a[]
            String ident = matcher1.group(1);//a
            Symbol symbol = new Symbol(ident,"VAR");
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


    public void getCALL_BEGIN(Quadruple quadruple) {
        // 因为参数要压到新栈里，所以在调用开始就要创建新栈
        Symbol symbol = new Symbol("$RA","VAR");
        GlobalVariable.symbolTable.addSymbol(symbol);
        GlobalVariable.symbolTable.setSymbolOffset("$RA");//存，偏移加4
        String addr = loadIdentAddress("$RA");
        mips.add("sw $ra, " + addr);

        registerPool.clearAll();
        mips.add("subu $sp, $sp, " + GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset());
        //创建新的符号表，栈
        GlobalVariable.symbolTable.addBlockTable();
        GlobalVariable.symbolTable.createNewStack();
    }
    //函数传参
    public void getPUSH(Quadruple quadruple) {
        String dst = GlobalVariable.symbolTable.generateParaName();
        //在push中，arg值f(a[1])中的a[1],而dim是a[1]定义时维度减去现在使用维度
        String arg = quadruple.getArg1();//现在变量名称
        int pushDim = quadruple.getPushDim();//变量定义时维度-使用时维度
        GlobalVariable.symbolTable.addSymbol(new Symbol(dst,"PARA"));
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
                String iReg = load(i);//含有下标的寄存器
                String index4Reg = registerPool.allocTRegister();
                mips.add("li " + index4Reg + ", " + dim2);
                mips.add("mul " + index4Reg + ", " + index4Reg + ", " + iReg);
                mips.add("sll " + index4Reg + ", " + index4Reg + ", " + 2);//使用时数组下标
                if (GlobalVariable.symbolTable.containLocalSymbol(ident)) { // 局部数组 a[]
                    if (symbol1.isArrAddr()) { //是地址（因为有可能是在函数中调用）
                        String arrOffsetReg = load(ident);
                        valueReg = registerPool.allocTRegister();
                        mips.add("subu " + valueReg + ", " + arrOffsetReg + ", " + index4Reg);//a起始地址-偏移量
                    } else {
                        // 存绝对地址
                        valueReg = registerPool.allocTRegister();
                        mips.add("addu " + valueReg + ", " + index4Reg + ", " + symbol1.getArrOffset());
                        mips.add("subu " + valueReg + ", $fp, " + valueReg);
                    }
                } else { // 全局数组 a[]
                    // 首位反转 + 下标偏移
                    String len4Reg = registerPool.allocTRegister();
                    int offset=0;
                    if (symbol1.getDim() == 1) offset=(symbol1.getDim1() - 1) * 4;
                    else offset=(symbol1.getDim1() * symbol1.getDim2() - 1) * 4;
                    mips.add("li " + len4Reg + ", " + offset);
                    String offsetReg = registerPool.allocTRegister();
                    mips.add("subu " + offsetReg + ", " + len4Reg + ", " + index4Reg);
                    valueReg = registerPool.allocTRegister();
                    mips.add("la " + valueReg + ", " + ident + "(" + offsetReg + ")");
                }
            } else { // 现在0，定义1/2
                Symbol symbol1 = GlobalVariable.symbolTable.getSymbol(arg);
                if (GlobalVariable.symbolTable.containLocalSymbol(arg)) { // 局部数组 a
                    if (symbol1.isArrAddr()) {
                        valueReg = load(arg);
                    } else {
                        // 存绝对地址
                        valueReg = registerPool.allocTRegister();
                        mips.add("subu " + valueReg + ", $fp, " + symbol1.getArrOffset());
                    }
                } else { // 全局数组 a
                    // 首尾反转
                    String len4Reg = registerPool.allocTRegister();
                    int offset=0;
                    if (symbol1.getDim() == 1) offset=(symbol1.getDim1() - 1) * 4;
                    else offset=(symbol1.getDim1() * symbol1.getDim2() - 1) * 4;
                    mips.add("li " + len4Reg + ", " + offset);
                    valueReg = registerPool.allocTRegister();
                    mips.add("la " + valueReg + ", " + arg + "(" + len4Reg + ")");
                }
            }
        }
        //改变栈的位置
        GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).clearVar();
        store(dst, valueReg);
    }
    public void getCALL(Quadruple quadruple) {
        mips.add("move $fp, $sp");
        mips.add("jal " + quadruple.getArg1());

        //返回，除去函数的符号表
        GlobalVariable.symbolTable.removeBlockTable();
        GlobalVariable.symbolTable.removeLastStack();
        mips.add("addu $sp, $sp, " + GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset());
        mips.add("move $fp, $sp");
        String addr = loadIdentAddress("$RA");
        mips.add("lw $ra, " + addr);
        GlobalVariable.symbolTable.removeSymbol("$RA");
        //获取原来的栈偏移量（去掉ra后的）
        int blockOffset = GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).getStackOffset();
        GlobalVariable.symbolTable.getTableList().get(GlobalVariable.symbolTable.getLevel()).setStackOffset(blockOffset - 4);
    }
    public void getRET(Quadruple quadruple) {
        String arg1 = quadruple.getArg1();
        if (!arg1.equals("")) {
            String tmp = load(arg1);
            mips.add("move $v0, " + tmp);
        }
        mips.add("jr $ra");
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
