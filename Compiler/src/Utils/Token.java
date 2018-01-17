package Utils;

import Utils.Value.ValueObject;
import Utils.Value.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

enum TokenType {
    INTEGER("INTEGER"),
    PLUS("PLUS"),
    MINUS("MINUS"),
    MUL("MUL"),
    DIV("DIV"),
    EOF("EOF"),
    LPAR("LPAR"),
    RPAR("RPAR"),
    NULLVALUE("NULLVALUE"),
    BEGIN("BEGIN"),
    END("END"),
    ASSIGN("ASSIGN"),
    SEMI("SEMI"),
    DOT("DOT"),
    REAL("REAL"),
    COMMA("COMMA"),
    COLON("COLON"),
    // 变量声明
    VARDECL("VAR"),
    INTTYPE("INTEGER"),
    REALTYPE("REAL"),
    // 过程定义
    PROCEDURE("PROCEDURE"),
    // 表示变量
    VAR("VAR"),
    // 表示什么都没有, 而且与值类型没有隶属关系
    EMPTY("EMPTY"),
    // 表示类型
    TYPE("TYPE"),
    // 打印并换行
    WRITELN("writeln"),
    FOR("for"),
    TO("to"),
    DOWNTO("downto"),
    DO("do");

    private String abbrevation;

    TokenType(String s) {
        this.abbrevation = s;
    }
    @Override
    public String toString() {
        return this.abbrevation;
    }

}

class EndOfText {}

/*
Readable instance
 */
class NullValue {
    private NullValue() {}
    private static NullValue instance;
    public static NullValue getInstance() {
        if (instance == null) {
            instance = new NullValue();
        }
        return instance;
    }
}      // empty class without value



public class Token {
    // 保存Token 的类型
    public TokenType type;
    public Object value;

    // constructor
    Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, %s)", type, value);
    }

    public static void main(String[] args) {
        Token t1 = new Token(TokenType.INTEGER, 192);
//        System.out.println(t1);
    }
}
