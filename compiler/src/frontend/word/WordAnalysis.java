package frontend.word;

import frontend.word.entity.WordEntity;

import java.util.ArrayList;

public class WordAnalysis {
    private String sentence;
    private Integer length;
    private Integer curPos=0;          //    当前字符位置
    private ArrayList<WordEntity> wordEntities=new ArrayList<>();
    private StringBuffer curStr;           //读取的单词
    private String type;                  //单词类型
    private Integer lineNum=1;
    public WordAnalysis(String sentence){
//        输入文章(包含换行)
        this.sentence=sentence;
        length=this.sentence.length();  //注意，换行符算在.length()内
        beginWordAnalysis();
    }
    private void beginWordAnalysis(){
        while(true){
            curStr=new StringBuffer("");
//            空白字符
            while (curPos<length&&Character.isWhitespace(sentence.charAt(curPos))){
                if(sentence.charAt(curPos)=='\n') lineNum++;
                curPos++;
            }
            if(curPos>=length) break;
            if(isDigit()){
                while (isDigit()&&curPos<length){
                    curStr.append(sentence.charAt(curPos));
                    curPos++;
                }
                WordEntity wordEntity=new WordEntity("INTCON",curStr.toString(),lineNum);
                wordEntities.add(wordEntity);
            } else if(isLetter()){
                while((isLetter()||isDigit())&&curPos<length){
                    curStr.append(sentence.charAt(curPos));
                    curPos++;
                }
                type=getIdentifier(curStr.toString());
                WordEntity wordEntity=new WordEntity(type,curStr.toString(),lineNum);
                wordEntities.add(wordEntity);
            } else {
                handleSpecialWord();
                if(type.equals("EOF")) break;
                else if(type.equals("ANNOTATION")) continue;
                else {
                    WordEntity wordEntity=new WordEntity(type,curStr.toString(),lineNum);
                    wordEntities.add(wordEntity);
                }

            }


        }
    }

    //判断当前字符是否为字母
    public boolean isLetter() {
        return sentence.charAt(curPos) == '_'||Character.isLetter(sentence.charAt(curPos));
    }
    //判断当前字符是否为数字
    public boolean isDigit() {
        return Character.isDigit(sentence.charAt(curPos));
    }
    //处理特殊字符
    public void handleSpecialWord(){
        char letter=sentence.charAt(curPos);
        switch (letter){
            case '+':
                curStr.append(letter);
                type="PLUS";
                curPos++;
                break;
            case '-':
                curStr.append(letter);
                type="MINU";
                curPos++;
                break;
            case '*':
                curStr.append(letter);
                type="MULT";
                curPos++;
                break;
            case '%':
                curStr.append(letter);
                type="MOD";
                curPos++;
                break;
            case ',':
                curStr.append(letter);
                type="COMMA";
                curPos++;
                break;
            case ';':
                curStr.append(letter);
                type="SEMICN";
                curPos++;
                break;
            case '{':
                curStr.append(letter);
                type="LBRACE";
                curPos++;
                break;
            case '}':
                curStr.append(letter);
                type="RBRACE";
                curPos++;
                break;
            case '[':
                curStr.append(letter);
                type="LBRACK";
                curPos++;
                break;
            case ']':
                curStr.append(letter);
                type="RBRACK";
                curPos++;
                break;
            case '(':
                curStr.append(letter);
                type="LPARENT";
                curPos++;
                break;
            case ')':
                curStr.append(letter);
                type="RPARENT";
                curPos++;
                break;
            case '!':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='='){
                    curStr.append("!=");
                    type="NEQ";
                    curPos=curPos+2;
                    return;
                }
                curStr.append("!");
                type="NOT";
                curPos++;
                break;
            case '<':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='='){
                    curStr.append("<=");
                    type="LEQ";
                    curPos=curPos+2;
                    return;
                }
                curStr.append("<");
                type="LSS";
                curPos++;
                break;
            case '>':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='='){
                    curStr.append(">=");
                    type="GEQ";
                    curPos=curPos+2;
                    return;
                }
                curStr.append(">");
                type="GRE";
                curPos++;
                break;
            case '=':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='='){
                    curStr.append("==");
                    type="EQL";
                    curPos=curPos+2;
                    return;
                }
                curStr.append("=");
                type="ASSIGN";
                curPos++;
                break;
            case '|':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='|'){
                    curStr.append("||");
                    type="OR";
                    curPos=curPos+2;
                }
                break;
            case '&':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='&'){
                    curStr.append("&&");
                    type="AND";
                    curPos=curPos+2;
                }
                break;
            case '"':
                curStr.append(sentence.charAt(curPos));
                curPos++;
                while (curPos<length&&sentence.charAt(curPos) != '"') {
                    curStr.append(sentence.charAt(curPos));
                    curPos++;
                }
                curStr.append(sentence.charAt(curPos));
                curPos++;
                type="STRCON";
                break;
            case '/':
                if(curPos+1<length&&sentence.charAt(curPos+1)=='/'){
                    curPos=curPos+2;
                    while(curPos<length&&sentence.charAt(curPos)!='\n') curPos++;
                    lineNum++;
                    curPos++;
                    type="ANNOTATION";
                    return;
                } else if(curPos+1<length&&sentence.charAt(curPos+1)=='*'){
                    curPos=curPos+2;
                    while(curPos<length&&!(sentence.charAt(curPos)=='*'&&sentence.charAt(curPos+1)=='/')){
                        if(sentence.charAt(curPos)=='\n') lineNum++;
                        curPos++;
                    }
                    curPos=curPos+2;
                    type="ANNOTATION";
                    return;
                }
                curStr.append("/");
                type="DIV";
                curPos++;
                break;
            default:
                type="EOF";
                curPos++;
                break;
        }
    }

    //返回标识符种类
    public String getIdentifier(String word) {
        switch (word) {
            case "main":
                return "MAINTK";
            case "const":
                return "CONSTTK";
            case "int":
                return "INTTK";
            case "break":
                return "BREAKTK";
            case "continue":
                return "CONTINUETK";
            case "if":
                return "IFTK";
            case "else":
                return "ELSETK";
            case "while":
                return "WHILETK";
            case "getint":
                return "GETINTTK";
            case "printf":
                return "PRINTFTK";
            case "return":
                return "RETURNTK";
            case "void":
                return "VOIDTK";
            default:
                return "IDENFR";
        }
    }
    public String getWordEntitiesToString(){
        StringBuffer res=new StringBuffer("");
        for(int i=0;i<wordEntities.size();i++){
            res.append(wordEntities.get(i).getType()+" "+wordEntities.get(i).getWord()+"\n");
        }
        return res.toString();
    }
    public void put(){
        for(int i=0;i<wordEntities.size();i++){
            System.out.println(wordEntities.get(i).getType()+" "+wordEntities.get(i).getWord()+" "+wordEntities.get(i).getLine());
        }
    }
    public ArrayList<WordEntity> getWordEntities(){
        return wordEntities;
    }


}
