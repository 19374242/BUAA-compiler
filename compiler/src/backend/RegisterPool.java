package backend;

import frontend.mcode.Symbols.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPool {
    private int tPoint=0;
    // 约定 $t0-$t4 为中间结果寄存器，可任意分配，因为马上会存到sp里面去，所以不用害怕被覆盖
    private ArrayList<Integer> sRegList=new ArrayList<>();//优化部分
    private HashMap<Integer, Symbol> sRegMap=new HashMap<>();
    private ArrayList<Integer> tRegList=new ArrayList<>();
    private HashMap<Integer, String> tRegMap=new HashMap<>();

    public RegisterPool(){
        for (int i=0;i<8;i++){
            this.sRegList.add(0);
        }
        for (int i = 0; i < 10; i++) {
            this.tRegList.add(0);
        }
    }

    public boolean isSRegAvailable() {
        for (int i = 0; i < this.sRegList.size(); i++) {
            if (this.sRegList.get(i) == 0) {
                return true;
            }
        }
        return false;
    }
    public void mapSRegister(Symbol symbol) {
        for (int i = 0; i < this.sRegList.size(); i++) {
            if (this.sRegList.get(i) == 0) {
                this.sRegMap.put(i, symbol);
                this.sRegList.set(i, 1);
                return ;
            }
        }
        System.out.println("map wrong");
    }
    public String getSRegister(Symbol symbol) {
        for (Integer i : this.sRegMap.keySet()) {
            if (this.sRegMap.get(i).equals(symbol)) {
                return "$s" + i;
            }
        }
        return "getSRegister wrong";
    }
    public boolean containSRegister(Symbol symbol) {
        for (Symbol s : this.sRegMap.values()) {
            if (s.equals(symbol)) {
                return true;
            }
        }
        return false;
    }


    public void freeTRegister(String t) {
        Pattern patternTReg = Pattern.compile("^\\$t([0-9])");
        Matcher matcherTReg = patternTReg.matcher(t);
        if (matcherTReg.matches()) {
            int i = Integer.parseInt(matcherTReg.group(1));
            this.tRegMap.remove(i);
            this.tRegList.set(i, 0);
        }
    }

    public String getTRegister(String varName) {
        for (Integer i : this.tRegMap.keySet()) {
            if (this.tRegMap.get(i).equals(varName)) {
                return "$t" + i;
            }
        }
        return "getTRegister wrong";
    }
    public Boolean isTRegister(String varName) {
        for (Integer i : this.tRegMap.keySet()) {
            if (this.tRegMap.get(i).equals(varName)) {
                return true;
            }
        }
        return false;
    }
    public boolean isTRegAvailable() {
        for (int i = 4; i < this.tRegList.size(); i++) {
            if (this.tRegList.get(i) == 0) {
                return true;
            }
        }
        return false;
    }


    // 约定 $t4-$t9 为临时变量的寄存器
    public String allocTRegister(String varName) {
        for (int i = 4; i < this.tRegList.size(); i++) {
            if (this.tRegList.get(i) == 0) {
                this.tRegMap.put(i, varName);
                this.tRegList.set(i, 1);
                return "$t" + i;
            }
        }
        return "allocTRegister wrong";
    }
    public String allocTRegister() {
        tPoint++;
        if (tPoint >= 4) {
            tPoint = 0;
        }
        return "$t" + tPoint;
    }
    public void clearAll(){
        this.tRegList = new ArrayList<>();
        this.sRegList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            this.tRegList.add(0);
        }
        this.sRegList = new ArrayList<>();
        for (int i = 0; i < 8; i++ ) {
            this.sRegList.add(0);
        }
        this.tRegMap = new HashMap<>();
        this.sRegMap = new HashMap<>();
        this.tPoint = 0;
    }
    public void clearTRegister(){
        this.tRegList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            this.tRegList.add(0);
        }
        this.tRegMap = new HashMap<>();
    }

    public ArrayList<Integer> gettRegList() {
        return tRegList;
    }
    public HashMap<Integer, String> gettRegMap() {
        return tRegMap;
    }
    public ArrayList<Integer> getsRegList() {
        return sRegList;
    }


}
