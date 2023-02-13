package frontend.word.entity;

public class WordEntity {
    private String type;
    private String word;
    private Integer line;
    public WordEntity(String type,String word,Integer line){
        this.type=type;
        this.word=word;
        this.line=line;
    }

    public String getType() {
        return type;
    }

    public String getWord() {
        return word;
    }

    public Integer getLine() {
        return line;
    }
}
