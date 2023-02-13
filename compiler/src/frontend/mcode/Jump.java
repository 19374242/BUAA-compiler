package frontend.mcode;

import frontend.mcode.AST.IfStmt;

public class Jump {
    public static Integer ifBeginLabel=0;
    public static Integer ifEndLabel=0;
    public static Integer label=0;

    public static Integer labelLoop = 0;
    public static Integer labelLoopBegin = 0;
    public static Integer labelLoopEnd = 0;
    public static Boolean isLoopIf=false; //判断if是不是在while（xxxx）中
    public static String lastLoop="";
    public static String lastLoopEnd="";

}
