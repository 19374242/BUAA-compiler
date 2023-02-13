package frontend.mcode.AST;

public class Exp {
    public AddExp addExp=null;



    public String generate() {
        return this.addExp.generate();
    }
    public int generateNumber() {
        return addExp.generateNumber();
    }

    public int getDim() {
        return this.addExp.getDim();
    }

}
