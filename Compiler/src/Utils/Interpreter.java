package Utils;

import Utils.Value.ValueObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Interpreter {
    // 最后获得的值
    private ValueObject final_value;
    private Parser parser;
    private AST final_ast;
    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public ValueObject interprete() {
        if (final_value == null) {
            final_ast = this.parser.parse();
            final_value = final_ast.walk();
        }
        return final_value;
    }

    private static String sentence = "12 * (25 + 36) - 25 / 5";
    private static String sentence1 = "1 * (4 - 3) * 3 / 2 * (2 - 1)";
    private static String sentence2 = "(439+725) /68";
    private static String sentence3 = "7 + 3 * (10 / (12 / (3 + 1) - 1))";
    private static String sentence4 =  "7 --- 3";
    private static String sentence5 = "5 -- 2 + --3";

    public static void main(String[] args) {
        Lexer lexer = new Lexer(sentence);
        Parser parser = new Parser(lexer);
        Interpreter interpreterWithAst = new Interpreter(parser);
        System.out.println(interpreterWithAst.interprete().getValue());
    }
}