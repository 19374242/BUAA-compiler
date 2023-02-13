package frontend.mcode.AST;

public class Stmt {
    public String type="";//BLOCK/PRINTF/GETINT/ASSIGN/EXP/RETURN/IF/WHILE/BREAK/CONTINUE
    public Block block=null;
    public ReturnStmt returnStmt=null;
    public PrintfStmt printfStmt=null;
    public GetintStmt getintStmt=null;
    public AssignStmt assignStmt=null;
    public IfStmt ifStmt=null;
    public WhileStmt whileStmt=null;
    public BreakStmt breakStmt=null;
    public ContinueStmt continueStmt=null;
    public Exp exp=null;

    public void generate() {

        if (this.type.equals("BLOCK")) {
            this.block.generate();
        } else if (this.type.equals("PRINTF")) {
            this.printfStmt.generate();
        } else if (this.type.equals("GETINT")) {
            this.getintStmt.generate();
        } else if (this.type.equals("ASSIGN")) {
            this.assignStmt.generate();
        } else if (this.type.equals("EXP")) {
            this.exp.generate();
        } else if (this.type.equals("RETURN")) {
            this.returnStmt.generate();
        } else if (this.type.equals("IF")) {
            this.ifStmt.generate();
        } else if (this.type.equals("WHILE")) {
            this.whileStmt.generate();
        } else if (this.type.equals("BREAK")) {
            this.breakStmt.generate();
        } else if (this.type.equals("CONTINUE")) {
            this.continueStmt.generate();
        }
    }
}
