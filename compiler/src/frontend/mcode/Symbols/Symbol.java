package frontend.mcode.Symbols;

import java.util.ArrayList;

public class Symbol {
    private String name="";
    private String type="";//CONST,VAR,VOID_FUNC,INT_FUNC,PARA
    private int dim=0;
    private int dim1=0;
    private int dim2=0;
    private int value=0;
    private int paramsNum = 0;// FUNCDEF_IDENT, UNARYEXP_IDENT
    private ArrayList<Integer> paramsDimList= new ArrayList<>();
    private ArrayList<Integer> valueList = new ArrayList<>(); // 中间代码过程值
    private int offset = -1;//fp偏移
    private Boolean isArrAddr=false;//是否是地址
    private Integer arrOffset=0;//数组起始地址相对fp所在的偏移量
    //ident
    public Symbol(String name, String type){
        this.name=name;
        this.type=type;
    }
    // func
    public Symbol(String name, String type, int ParamsNum, ArrayList<Integer> paramsDimList) {
        this.name = name;
        this.type = type;
        this.paramsNum = ParamsNum;
        this.paramsDimList = paramsDimList;
    }

    public void addValueList(Integer x){
        valueList.add(x);
    }
    public ArrayList<Integer> getValueList() {
        return valueList;
    }


    public void setArrAddr(Boolean arrAddr) {
        isArrAddr = arrAddr;
    }

    public Boolean getArrAddr() {
        return isArrAddr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public void setDim1(int dim1) {
        this.dim1 = dim1;
    }

    public void setDim2(int dim2) {
        this.dim2 = dim2;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getDim() {
        return dim;
    }

    public int getDim1() {
        return dim1;
    }

    public int getDim2() {
        return dim2;
    }

    public int getValue() {
        return value;
    }

    public String getSymbolType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public boolean isArrAddr() {
        return isArrAddr;
    }
    public int getArrOffset() {
        return arrOffset;
    }
    public void setArrOffset(Integer arrOffset) {
        this.arrOffset = arrOffset;
    }
}
