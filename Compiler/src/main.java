import Utils.Interpreter;
import Utils.Lexer;
import Utils.Parser;
import Utils.Value.ValueObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class main {
    /*
    args1 means dict
     */
    public static void main(String[] args) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(args[0]));
        String src = new String(encoded, Charset.forName("UTF-8"));

        Lexer lexer = new Lexer(src);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        interpreter.interprete();
    }
}
