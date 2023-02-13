package frontend.mcode.AST;

import frontend.word.entity.WordEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PNumber {
    public WordEntity number=null;

    public String generate() {
        return number.getWord();
    }

}
