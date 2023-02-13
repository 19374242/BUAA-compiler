package frontend.mcode.Symbols;

import backend.RegisterPool;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockTable {
    private HashMap<String, Symbol> map=new HashMap<>();
    private HashMap<String, Symbol> funcMap=new HashMap<>();
    private RegisterPool registerPool;
    private int stackOffset=0;

    public BlockTable(RegisterPool registerPool, int stackOffset) {
        this.registerPool = registerPool;
        this.stackOffset = stackOffset;
    }





    public void setOffset(String name) {
        if (this.map.containsKey(name)) {
            this.map.get(name).setOffset(this.stackOffset);
            this.stackOffset += 4;
        } else {
            System.out.println("setOffset wrong");
        }
    }
    public void setArrOffset(String name, int len) {
        if (this.map.containsKey(name)) {
            this.map.get(name).setArrOffset(stackOffset);
            this.stackOffset += len * 4;
        } else {
            System.out.println("setArrOffset wrong");
        }
    }
    public void addSymbol(Symbol symbol) {
        if (symbol.getType().equals("INT_FUNC") || symbol.getType().equals("VOID_FUNC")) {
            funcMap.put(symbol.getName(), symbol);
        } else {
            map.put(symbol.getName(), symbol);
        }
    }
    public Symbol getNoFuncSymbol(String name) {
        return map.get(name);
    }

    public int getStackOffset() {
        return stackOffset;
    }
    public void setStackOffset(int i) {
        stackOffset=i;
    }

    public boolean contain(String name) {
        return map.containsKey(name);
    }
    public HashMap<String, Symbol> getMap() {
        return map;
    }

    public boolean containFunc(String name) {
        return funcMap.containsKey(name);
    }

    public HashMap<String, Symbol> getFuncMap() {
        return funcMap;
    }

    public void clearVar() {
        ArrayList<String> varList = new ArrayList<>();
        for (String name : map.keySet()) {
            if (map.get(name).getSymbolType().equals("VAR")) {
                varList.add(name);
            }
        }
        stackOffset = stackOffset - 4 * varList.size();
        for (String name : varList) {
            map.remove(name);
        }
    }
}
