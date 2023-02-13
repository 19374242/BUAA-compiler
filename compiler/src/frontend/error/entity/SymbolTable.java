package frontend.error.entity;

import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<SymbolTable> symbolTables=new ArrayList<>();
    private String name="";
    private Integer layer;
    private String kind;
    private Boolean isConst;
    private Integer dimension;
    private String type;// int/void
    private Integer line;
    private ArrayList<SymbolTable> params;
//    block
    public SymbolTable(String name,Integer layer,String kind) {
        this.name = name;
        this.layer = layer;
        this.kind = kind;
    }
//    IDENFR
    public SymbolTable(String name,Integer layer,String kind,Boolean isConst,Integer dimension,String type,Integer line) {
        this.name = name;
        this.layer = layer;
        this.kind = kind;
        this.dimension=dimension;
        this.isConst=isConst;
        this.type=type;
        this.line=line;
    }
//    func
    public SymbolTable(String name,Integer layer,String kind,String type,Integer line,ArrayList<SymbolTable> params) {
        this.name = name;
        this.layer = layer;
        this.kind = kind;
        this.type=type;
        this.line=line;
        this.params=params;
    }
    public ArrayList<SymbolTable> getSymbolTables() {
        return symbolTables;
    }
    public SymbolTable findIdent(String name) {
        for (SymbolTable subTable:this.symbolTables) {
            if (subTable.kind.equals("IDENFR")&&subTable.name.equals(name)) {
                return subTable;
            }
        }
        return null;
    }
    public SymbolTable findFunc(String name) {
        for (SymbolTable symbolTable : this.symbolTables) {
            if (symbolTable.getKind().equals("func") && symbolTable.getName().equals(name)) {
                return symbolTable;
            }
        }
        return null;
    }

    public Boolean getConst() {
        return isConst;
    }

    public String getName() {
        return name;
    }

    public Integer getLayer() {
        return layer;
    }

    public String getKind() {
        return kind;
    }

    public Integer getDimension() {
        return dimension;
    }

    public String getType() {
        return type;
    }

    public Integer getLine() {
        return line;
    }

    public ArrayList<SymbolTable> getParams() {
        return params;
    }

    //    有点问题
    public ErrorEntity addSymbol(SymbolTable symbolTable) {
        for (SymbolTable subTable : this.symbolTables) {
            if (layer==0) {
                if (symbolTable.name.equals(subTable.name)) {
                    return new ErrorEntity(symbolTable.line, "b");
                }
            } else {
                if (symbolTable.name.equals(subTable.name)&&symbolTable.type.equals(subTable.type)) {
                    return new ErrorEntity(symbolTable.line, "b");
                }
            }
        }
        this.symbolTables.add(symbolTable);
        return null;
    }
}
