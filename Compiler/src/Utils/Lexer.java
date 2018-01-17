package Utils;

import Utils.Value.NoneValue;
import Utils.Value.ValueObject;

import java.util.HashMap;

/*
关键词查找表
 */
class RESERVED_KEYWORDS {
    static private HashMap<String, Token> keyword_map = new HashMap<>();
    static  {
        keyword_map.put("BEGIN", new Token(TokenType.BEGIN, "BEGIN"));
        keyword_map.put("END", new Token(TokenType.BEGIN, "END"));
        keyword_map.put("VAR", new Token(TokenType.VARDECL, "VARDECL"));
        keyword_map.put("PROCEDURE", new Token(TokenType.PROCEDURE, "PROCEDURE"));
        keyword_map.put("INTEGER", new Token(TokenType.INTTYPE, "INTEGER"));
        keyword_map.put("REAL", new Token(TokenType.REALTYPE, "REAL"));
        keyword_map.put("writeln", new Token(TokenType.WRITELN, "writeln"));
    }
    static Token get_token(String arg) {
        return keyword_map.get(arg);
    }
}

/*
 词法分析器
 能够把各个字符串片段转化成离散的词语
 */
public class Lexer {
    // 源程序文本
    private final String src_text;
    private int index;
    private int line;
    public Lexer(String text) {
        this.src_text = text;
        index = 0;
        line = 0;
    }

    // TODO make clear if this should be changed
    private Token get_variable() {
        int begin_index = index;
        while (index < src_text.length() && Character.isLetter(src_text.charAt(index)) || src_text.charAt(index) == '_') {
            ++index;
        }
        // optimize
        String var_name = src_text.substring(begin_index, index);

        Token var_token = new Token(TokenType.VAR, var_name);

        return var_token;
    }

    /*
    前瞻字符
     */
    private Character peek() {
        /* 出现语法错误 */
        // TODO 显示捕获异常
        if (index + 1 >= src_text.length())
            error();
        return src_text.charAt(index + 1);
    }
    /*
    根据字符串返回获得的内容，可以是关键字或者符号表中的内容
     */
    private Token get_id() {

        int current_index = index;
        while (Character.isLetter(src_text.charAt(current_index))) {
            ++current_index;
        }
        Token to_get = RESERVED_KEYWORDS.get_token(src_text.substring(index, current_index));
        if (to_get == null) {
            // TODO: modify this to make it
            to_get = get_variable();
        }
        // TODO: 显式捕获异常
        return to_get;

    }

    /*
    eat 的辅助函数，用去吞噬ID
     */
    private void eat_id(TokenType t) {
        int begin_index = index;
        while (Character.isLetter(src_text.charAt(index))) {
            ++index;
        }
        if (!src_text.substring(begin_index, index).equals(t.toString())) {
//            get_variable();
            System.err.println("Except " + t.toString() + " but got " + src_text.substring(begin_index, index));
            error(t);
        }
    }


    Token get_next_token() {
        eat_whitespace();
        if (index >= src_text.length()) {
            return new Token(TokenType.EOF, new EndOfText());
        }
        if (Character.isDigit(src_text.charAt(index))) {
            return number();
        } else if (src_text.charAt(index) == ':'){
            if (peek() == '=') {
                return new Token(TokenType.ASSIGN, ":=");
            } else {
                return new Token(TokenType.COLON, ":");
            }
        } else if (in_single_map(src_text.charAt(index))) {
            return single_character_token.get(src_text.charAt(index));
        } else if (Character.isLetter(src_text.charAt(index))) {
            // 如果是字母
            return get_id();
        }  else {
            error();
            // TODO: make clear what to return
            return new Token(TokenType.EOF, new EndOfText());
        }
    }

    private void eat_whitespace() {
        while (index < src_text.length() && (src_text.charAt(index) == ' ' || src_text.charAt(index) == '\n')) {
            if (src_text.charAt(index) == '\n') ++line;
            ++index;
        }
    }

    /*
    获得的是数字
     */
    private double fractional() {
        if (src_text.charAt(index) != '.')
            error("Call fractional error and found " + src_text.charAt(index));
        ++index;
        double frac = 0;
        int exp = -1;
        while (index < src_text.length() && Character.isDigit(src_text.charAt(index))) {
            frac += Math.pow(10, exp) * Character.digit(src_text.charAt(index), 10);
            --exp;
            ++index;
        }
        return frac;
    }
    private Token number() {
        /*
        Integer -> \d+
        real -> \d+.\d+
         */
        int value = 0;
        while (index < src_text.length() && Character.isDigit(src_text.charAt(index))) {
            value *= 10;
            value += Character.digit(src_text.charAt(index), 10);
            advance();
        }

        if (index == src_text.length()) {
            --index;
        }

        // 回退后不可能还是.吧
        if (src_text.charAt(index) == '.') {
            /*
            it may be a real
            */
            double get = (double)value + fractional();
            return new Token(TokenType.REAL, get);
        } else {
            return new Token(TokenType.INTEGER, value);
        }



    }

    private void advance() {
        ++index;
    }

    private static HashMap<TokenType, Character> signle_token_character = new HashMap<>();
    static {
        signle_token_character.put(TokenType.DIV, '/');
        signle_token_character.put(TokenType.MUL, '*');
        signle_token_character.put(TokenType.LPAR, '(');
        signle_token_character.put(TokenType.PLUS, '+');
        signle_token_character.put(TokenType.MINUS, '-');
        signle_token_character.put(TokenType.RPAR, ')');

        signle_token_character.put(TokenType.SEMI, ';');
        signle_token_character.put(TokenType.DOT, '.');
        signle_token_character.put(TokenType.COMMA, ',');
        signle_token_character.put(TokenType.COLON, ':');
    }

    private static HashMap<Character, Token> single_character_token = new HashMap<>();
    static {
        for (HashMap.Entry<TokenType, Character> entry:
             signle_token_character.entrySet()) {
            TokenType t = entry.getKey();
            Character ch = entry.getValue();
            single_character_token.put(ch, new Token(t, ch));
        }
    }

    private static boolean in_single_map(Character ch) {
        return single_character_token.containsKey(ch);
    }

    void eat_token(TokenType t) {
        switch (t) {
            case DIV:
            case MUL:
            case LPAR:
            case PLUS:
            case MINUS:
            case RPAR:
            case SEMI:
            case DOT:
            case COMMA:
            case COLON:
                char current_char = src_text.charAt(index);
                // 单字符的 Token
                if (signle_token_character.get(t) == current_char) {
                    advance();
                } else {
                    error(t);
                }
                break;
            case INTEGER:
            case REAL:
                eat_whitespace();
                break;
            case ASSIGN:
                if (src_text.charAt(index) == ':' && src_text.charAt(index + 1) == '=') {
                    index += 2;
                } else {
                    error("Assign is not :=");
                }
                break;
            case EOF:
            case EMPTY:
                break;
            case END:
            case BEGIN:
            case INTTYPE:
            case REALTYPE:
            case PROCEDURE:
            case VARDECL:
                //do writeln
            case WRITELN:
                // 这两个case都已经检阅完毕
                eat_id(t);
                break;
            case VAR:
                eat_var();
                break;
            default:
                error(t);
        }
        eat_whitespace();
    }

    // optimize
    private void eat_var() {
        while (index < src_text.length() && Character.isLetter(src_text.charAt(index)) || src_text.charAt(index) == '|') {
            ++index;
        }
    }
    /*
     抛出异常
     */
    private void error() {
        throw new RuntimeException("Character Error! ");
    }
    private void error(String hint) { throw new RuntimeException(hint); }
    private void error(TokenType t) {
        throw new RuntimeException("Character Error! " + t + " is found!");
    }
}
