package frontend.mcode;

import frontend.mcode.Symbols.Symbol;
import frontend.mcode.Symbols.SymbolTable;

import java.util.ArrayList;

public class Temp {
    private int temp=0;
    private SymbolTable symbolTable;
    public Temp(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String generateTemp(int value) {
        String name = "$T" + this.temp++;
        Symbol symbol = new Symbol(name,"VAR");
        symbol.setValue(value);
        symbolTable.addSymbol(symbol);
        return name;
    }
}
