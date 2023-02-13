package frontend.mcode;

import java.util.ArrayList;

public class Quadruple {
    private String dst = "";
    private String op;
    private String arg1 = "";
    private String arg2 = "";
    private int len = 0;//数组长度
    private String index = "";//当前位置
    private int dim2 = 0 ;
    private ArrayList<String> sRegList=new ArrayList<>();//跳转时存s寄存器
    private ArrayList<String> tRegList=new ArrayList<>();//跳转时存t寄存器
    private int pushDim = 0;//定义时维度-使用时维度
    private int sRegFlag=0;//是否可用s寄存器


    public Quadruple(String dst, String op, String arg1, String arg2) {
        this.dst = dst;
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public ArrayList<String> gettRegList() {
        return tRegList;
    }

    public void settRegList(ArrayList<String> tRegList) {
        this.tRegList = tRegList;
    }

    public void setPushDim(int pushDim) {
        this.pushDim = pushDim;
    }

    public int getsRegFlag() {
        return sRegFlag;
    }
    public ArrayList<String> getsRegList() {
        return sRegList;
    }


    public String getDst() {
        return dst;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setOp(String op) {
        this.op = op;
    }

    @Override
    public String toString() {
        switch (op){
            case "DECL_CONST_IDENT":
                return "const int " + this.dst + " = " + this.arg1;
            case "ASSIGNMENT_CONST_ARRAY":
                return "const " + this.dst + "[" + this.index + "]" + " = " + this.arg1;
            case "MAIN":
                return "main begin";
            case "MAIN_END":
                return "main end";
            case "BLOCK_BEGIN":
                return "block begin";
            case "BLOCK_END":
                return "block end";
            case "DECL_ARR":
                return "arr int " + this.dst + "[" + this.len + "]";
            case "ASSIGNMENT_VAR_ARRAY":
                return "var " + this.dst + "[" + this.index + "]" + " = " + this.arg1;
            case "DECL_VAR_IDENT":
                return this.arg1.equals("") ? "var int " + this.dst : "var int " + this.dst + " = " + this.arg1;
            case "ASSIGNMENT":
                return this.dst + " = " + this.arg1;
            case "ADD":
                return this.dst + " = " + this.arg1 + " + " + this.arg2;
            case "SUB":
                return this.dst + " = " + this.arg1 + " - " + this.arg2;
            case "MUL":
                return this.dst + " = " + this.arg1 + " * " + this.arg2;
            case "DIV":
                return this.dst + " = " + this.arg1 + " / " + this.arg2;
            case "MOD":
                return this.dst + " = " + this.arg1 + " % " + this.arg2;
            case "PRINT_STR":
                return "print string " + this.arg1;
            case "PRINT_INT":
                return "print int " + this.arg1;
            case "GETINT":
                return "read " + this.dst;
            case "RETURN":
                return "ret " + this.arg1;
            case "PUSH":
                return "push " + this.arg1;
            case "FUNC_BEGIN":
                return "func begin";
            case "FUNC_END":
                return "func end";
            case "INT_FUNC":
                return "int " + this.arg1 + "()";
            case "VOID_FUNC":
                return "void " + this.arg1 + "()";
            case "PARA":
                return "para int " + this.arg1;
            case "CALL":
                return "call " + this.arg1;
            case "CALL_BEGIN":
                return "call begin " + this.arg1;
            case "GOTO":
                return "goto " + this.arg1;
            case "BEQ":
                return "branch " + this.dst + " on " + this.arg1 + " == " + this.arg2;
            case "BNE":
                return "branch " + this.dst + " on " + this.arg1 + " != " + this.arg2;
            case "EQL":
                return "set " + this.dst + " on " + this.arg1 + " == " + this.arg2;
            case "NEQ":
                return "set " + this.dst + " on " + this.arg1 + " != " + this.arg2;
            case "GRE":
                return "set " + this.dst + " on " + this.arg1 + " > " + this.arg2;
            case "LSS":
                return "set " + this.dst + " on " + this.arg1 + " < " + this.arg2;
            case "GEQ":
                return "set " + this.dst + " on " + this.arg1 + " >= " + this.arg2;
            case "LEQ":
                return "set " + this.dst + " on " + this.arg1 + " <= " + this.arg2;
            case "LABEL":
                return "label " + this.arg1 + ":";
            case "WHILE_BEGIN":
                return "while begin";
            case "WHILE_END":
                return "while end";
            default:
                return "1234"+this.op;
        }
//        if (this.op.equals("DECL_CONST_IDENT")) {
//            return "const int " + this.dst + " = " + this.arg1;
//        } else if (this.op.equals("ASSIGNMENT_CONST_ARRAY")) {
//            return "const " + this.dst + "[" + this.index + "]" + " = " + this.arg1;
//        } else if (this.op.equals("RETURN")) {
//            return "ret " + this.arg1;
//        } else if (this.op.equals("MAIN")) {
//            return "main begin";
//        } else if (this.op.equals("MAIN_END")) {
//            return "main end";
//        } else if (this.op.equals("BLOCK_BEGIN")) {
//            return "block begin";
//        } else if (this.op.equals("BLOCK_END")) {
//            return "block end";
//        } else if (this.op.equals("DECL_ARR")) {
//            return "arr int " + this.dst + "[" + this.len + "]";
//        } else if (this.op.equals("ASSIGNMENT_VAR_ARRAY")) {
//            return "var " + this.dst + "[" + this.index + "]" + " = " + this.arg1;
//        } else if (this.op.equals("DECL_VAR_IDENT")) {
//            return this.arg1.equals("") ? "var int " + this.dst : "var int " + this.dst + " = " + this.arg1;
//        } else if (this.op.equals("ASSIGNMENT")) {
//            return this.dst + " = " + this.arg1;
//        } else if (this.op.equals("ADD")) {
//            return this.dst + " = " + this.arg1 + " + " + this.arg2;
//        } else if (this.op.equals("SUB")) {
//            return this.dst + " = " + this.arg1 + " - " + this.arg2;
//        } else if (this.op.equals("MUL")) {
//            return this.dst + " = " + this.arg1 + " * " + this.arg2;
//        } else if (this.op.equals("DIV")) {
//            return this.dst + " = " + this.arg1 + " / " + this.arg2;
//        } else if (this.op.equals("MOD")) {
//            return this.dst + " = " + this.arg1 + " % " + this.arg2;
//        } else if (this.op.equals("PRINT_STR")) {
//            return "print string " + this.arg1;
//        } else if (this.op.equals("PRINT_INT")) {
//            return "print int " + this.arg1;
//        } else if (this.op.equals("GETINT")) {
//            return "read " + this.dst;
//        } else if (this.op.equals("RETURN")) {
//            return "ret " + this.arg1;
//        } else if (this.op.equals("PUSH")) {
//            return "push " + this.arg1;
//        } else if (this.op.equals("FUNC_BEGIN")) {
//            return "func begin";
//        } else if (this.op.equals("FUNC_END")) {
//            return "func end";
//        } else if (this.op.equals("INT_FUNC")) {
//            return "int " + this.arg1 + "()";
//        } else if (this.op.equals("VOID_FUNC")) {
//            return "void " + this.arg1 + "()";
//        } else if (this.op.equals("PARA")) {
//            return "para int " + this.arg1;
//        } else if (this.op.equals("CALL")) {
//            return "call " + this.arg1;
//        } else if (this.op.equals("CALL_BEGIN")) {
//            return "call begin " + this.arg1;
//        } else if (this.op.equals("GOTO")) {
//            return "goto " + this.arg1;
//        } else if (this.op.equals("BEQ")) {
//            return "branch " + this.dst + " on " + this.arg1 + " == " + this.arg2;
//        } else if (this.op.equals("BNE")){
//            return "branch " + this.dst + " on " + this.arg1 + " != " + this.arg2;
//        } else if (this.op.equals("EQL")) {
//            return "set " + this.dst + " on " + this.arg1 + " == " + this.arg2;
//        } else if (this.op.equals("NEQ")) {
//            return "set " + this.dst + " on " + this.arg1 + " != " + this.arg2;
//        } else if (this.op.equals("GRE")) {
//            return "set " + this.dst + " on " + this.arg1 + " > " + this.arg2;
//        } else if (this.op.equals("LSS")) {
//            return "set " + this.dst + " on " + this.arg1 + " < " + this.arg2;
//        } else if (this.op.equals("GEQ")) {
//            return "set " + this.dst + " on " + this.arg1 + " >= " + this.arg2;
//        } else if (this.op.equals("LEQ")) {
//            return "set " + this.dst + " on " + this.arg1 + " <= " + this.arg2;
//        } else if (this.op.equals("LABEL")) {
//            return "label " + this.arg1 + ":";
//        } else if (this.op.equals("WHILE_BEGIN")) {
//            return "while begin";
//        } else if (this.op.equals("WHILE_END")) {
//            return "while end";
//        }
//        return "1234"+this.op;
    }
    public void setLen(int len) {
        this.len = len;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public void setDim2(int dim2) {
        this.dim2 = dim2;
    }
    public void setsRegList(ArrayList<String> sRegList) {
        this.sRegList = sRegList;
    }

    public String getOp() {
        return op;
    }

    public int getPushDim() {
        return pushDim;
    }

    public int getLen() {
        return len;
    }
    public void setDst(String dst) {
        this.dst = dst;
    }

    public int getDim2() {
        return dim2;
    }

    public void setsRegFlag(int sRegFlag) {
        this.sRegFlag = sRegFlag;
    }
}
