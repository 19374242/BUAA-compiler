package frontend.mcode.AST;

public class PrimaryExp {
    public String primaryExpType=""; //EXP/NUMBER/LVal
    public Exp exp=null;
    public LVal lVal=null;
    public PNumber pNumber=null;


    public String generate() {
        if (this.primaryExpType.equals("EXP")) {
            return this.exp.generate();
        } else if (this.primaryExpType.equals("LVal")) {
            return this.lVal.generate();
        } else if (this.primaryExpType.equals("NUMBER")){
            return this.pNumber.generate();
        }
        return "primaryExp wrong";
    }
    public int generateNumber() {
        if (this.primaryExpType.equals("EXP")) {
            return this.exp.generateNumber();
        } else if (this.primaryExpType.equals("LVal")) {
            return this.lVal.generateNumber();
        } else if (this.primaryExpType.equals("NUMBER")) {
            return Integer.parseInt(this.pNumber.generate());
        }
        return 0;
    }


    public int getDim() {
        if (this.primaryExpType.equals("EXP")) {
            return this.exp.getDim();
        } else if (this.primaryExpType.equals("LVal")) {
            return this.lVal.getDim();
        } else if (this.primaryExpType.equals("NUMBER")) {
            return 0;
        } else {
            return 0;
        }
    }


}
