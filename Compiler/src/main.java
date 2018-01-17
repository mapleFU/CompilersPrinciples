import Utils.Interpreter;
import Utils.Lexer;
import Utils.Parser;
import Utils.Value.ValueObject;

import java.nio.file.Path;
import java.util.Scanner;

public class main {
    private static String sentence = "12 * (25 + 36) - 25 / 5";
    private static String sentence1 = "1 * (4 - 3) * 3 / 2 * (2 - 1)";
    private static String sentence2 = "(439+725) /68";
    private static String sentence3 = "7 + 3 * (10 / (12 / (3 + 1) - 1))";
    private static String sentence4 =  "7 --- 3";
    private static String sentence5 = "5 -- 2 + --3";

    private static String sentence_double = "1.23 * (4 - 2.1)";
    private static String sentence_double2 = "1 * 3 - 2.6 / 1.3";

    private static String pascal_prog0 = "VAR\n" +
            "a, b, c, number: INTEGER;\n" +
            "x: REAL;\n" +
            "BEGIN\n" +
            "    BEGIN\n" +
            "        number := 2;\n" +
            "        a := number;\n" +
            "        b := 10 * a + 10 * number / 4;\n" +
            "        c := a - - b;\n" +
            "        writeln(a, b, c, number);\n" +
            "    END;\n" +
            "    x := 11;\n" +
            "END.";

    private static String pascal_prog1 = "VAR\n" +
            "   x : INTEGER;\n" +
            "BEGIN\n" +
            "   x := 2;\n" +
            "END.";

    private static String pascal_prog2 = "VAR\n" +
            "   x, y : INTEGER;\n" +
            "BEGIN\n" +
            "   x := 2;\n" +
            "   y := 3 + x;\n" +
            "END.";

    public static void main(String[] args) {
//        String line;
//        Scanner in = new Scanner(System.in);
//        while (in.hasNextLine()) {
//            line = in.nextLine();
//            if (line.equalsIgnoreCase("exit")) {
//                return;
//            }
//            System.out.println(get_value(line));
//        }
        test_pascal_prog(pascal_prog0);
//        test_pascal_prog(pascal_prog1);
//        test_pascal_prog(pascal_prog2);
    }

    public static void test_pascal_prog(String prog) {
        Lexer lexer = new Lexer(prog);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        ValueObject var = interpreter.interprete();
    }

    public static void test_double() {

        test(sentence_double, 2.337);
        test(sentence_double2, 1.0);
    }

    public static void test_integer() {
        test(sentence, 727);
        test(sentence1, 1);
        test(sentence2, 17);
        test(sentence3, 22);
        test(sentence4, 4);
        test(sentence5, 10);
    }
    public static Number get_value(String sentence) {
        Lexer lexer = new Lexer(sentence);
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        ValueObject var = interpreter.interprete();
        return (Number) var.getValue();
    }
    public static void test(String sentence, int value) {
        int get = (Integer) get_value(sentence);
        if (get == value) {
            System.out.println("Right");
        } else {
            System.out.println("Except " + value + " but got " + get);
        }
    }

    public static void test(String sentence, double value) {
        double get = (Double) get_value(sentence);
        if (Math.abs(value - get) < 0.0001) {
            System.out.println("Right");
        } else {
            System.out.println("Except " + value + " but got " + get);
        }
    }
}
