package frontend.error.entity;

public class ErrorEntity implements Comparable<ErrorEntity> {
    private Integer line;
    private String code;
    public ErrorEntity(Integer line,String code){
        this.line=line;
        this.code=code;
    }

    public Integer getLine() {
        return line;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(ErrorEntity o) {
        //降序
        //return o.line - this.line;
        //升序
        return this.line-o.line;
    }
}
