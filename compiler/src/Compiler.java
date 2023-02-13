import backend.MIPSGenerator;
import backend.MIPSGeneratorOpt;
import frontend.error.Traversal;
import frontend.grammar.GrammarAnalysis;
import frontend.IOTool;
import frontend.mcode.GlobalVariable;
import frontend.mcode.MCodeGenerator;
import frontend.mcode.Quadruple;
import frontend.word.WordAnalysis;
import frontend.word.entity.WordEntity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
    public static Boolean isOpt=true;
    public static void main(String[] args) throws IOException {
        WordAnalysis wordAnalysis=new WordAnalysis(IOTool.getInput("testfile.txt"));
        //IOTool.outputAns(wordAnalysis.getWordEntitiesToString());//词法分析
        GrammarAnalysis grammarAnalysis=new GrammarAnalysis(wordAnalysis.getWordEntities());
        IOTool.outputAns(grammarAnalysis.getRes());
//        错误处理
        Traversal traversal=new Traversal(grammarAnalysis.getErrorEntities(),grammarAnalysis.getCurGrammarTree());
        traversal.put();
        //wordAnalysis.put();
        IOTool.outputErrorAns(traversal.getErrorRes());
        if(traversal.getErrorRes().isEmpty()){
            //生成ast语法树
            MCodeGenerator mCodeGenerator=new MCodeGenerator(wordAnalysis.getWordEntities());
            //生成中间代码
            mCodeGenerator.getRoot().generate();

            IOTool.outputFile(GlobalVariable.getQuadruple(),"testfile1_19374242_高远_优化前中间代码.txt");
            System.out.println("----------------------------before opt-------------------------------");
            System.out.println(GlobalVariable.getQuadruple());
            if(isOpt){
                System.out.println("----------------------------after opt-------------------------------");
                GlobalVariable.MCodeOpt();
                IOTool.outputFile(GlobalVariable.getQuadruple(),"testfile1_19374242_高远_优化后中间代码.txt");
            }
            System.out.println(GlobalVariable.getQuadruple());
            System.out.println("---------------------------------MIPS------------------------------------");
            MIPSGenerator mipsGenerator=new MIPSGenerator();
            MIPSGeneratorOpt mipsGeneratorOpt=new MIPSGeneratorOpt();
            System.out.println("---------------------------------before opt------------------------------------");
            System.out.println(mipsGenerator.getMips());
            System.out.println("---------------------------------after opt------------------------------------");
            System.out.println(mipsGeneratorOpt.getMips());
            //IOTool.outputMips(mipsGenerator.getMips());
            IOTool.outputMips(mipsGeneratorOpt.getMips());
            IOTool.outputFile(mipsGenerator.getMips(),"testfile1_19374242_高远_优化前目标代码.txt");

            if(isOpt){
                IOTool.outputFile(mipsGeneratorOpt.getMips(),"testfile1_19374242_高远_优化后目标代码.txt");
            }
        }


    }
}
