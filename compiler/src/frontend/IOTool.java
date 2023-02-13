package frontend;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOTool {
//    词法分析读取文件
    public static String getInput(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        int x;
        while ((x=inputStreamReader.read())!=-1) {
            char letter=(char)x;
            stringBuilder.append(letter);
        }
        inputStreamReader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
//    词法分析输出文件
    public static void outputAns(String ans) throws IOException {
        OutputStream out = new FileOutputStream("output.txt");
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.append(ans);
        writer.close();
        out.close();
    }
//    错误处理
    public static void outputErrorAns(String ans) throws IOException {
        OutputStream out = new FileOutputStream("error.txt");
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.append(ans);
        writer.close();
        out.close();
    }
    //    mips
    public static void outputMips(String ans) throws IOException {
        OutputStream out = new FileOutputStream("mips.txt");
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.append(ans);
        writer.close();
        out.close();
    }
    //else
    public static void outputFile(String ans,String fileName) throws IOException {
        OutputStream out = new FileOutputStream(fileName);
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.append(ans);
        writer.close();
        out.close();
    }
}
