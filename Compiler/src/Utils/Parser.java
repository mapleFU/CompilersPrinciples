package Utils;

import Utils.Value.*;
import com.sun.javafx.fxml.expression.Expression;

import java.io.Serializable;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 Abstract Syntax Tree
 */
abstract class AST implements Serializable {
//    Object value;
    Token token;
    AST(Token token) {
        this.token = token;
    }
    abstract ValueObject walk();
}


class TypeIdentify extends AST {
    private ArrayList<String> var_names;
    TypeIdentify(Token t) {
        super(t);
        var_names = new ArrayList<>();
    }

    void add_var_name(String s) {
        var_names.add(s);
    }

    @Override
    NoneValue walk() {
        String type = (String) this.token.value;
        for (final String name:
             var_names) {
            if (type.equals("REAL")) {
                SymbolTable.insert(name, new RealValue(0.0));
            } else if (type.equals("INTEGER")) {
                SymbolTable.insert(name, new IntegerValue(0));
            } else {
                throw new RuntimeException("Type in TypeIdentify error");
            }
        }
        return new NoneValue();
    }
}

/*w
变量节点
 */
class Var extends AST {
    private boolean initialized = false;
    Var(String var_name) {
        super(new Token(TokenType.VAR, var_name));
    }

    public String getName() {
        return (String) this.token.value;
    }

    @Override
    ValueObject walk() {
        ValueObject variable = SymbolTable.lookup((String) this.token.value);
        if (variable == null) {
            throw new RuntimeException("Variable " + (String) this.token.value + " not existed");
        }
        return variable;
    }
}

/*
 UnaryOp
 */
class UnaryOp extends AST {
    AST expr;
    UnaryOp(Token token, AST expr) {
        super(token);
        this.expr = expr;
    }

    @Override
    DigitValue walk() {
        DigitValue to_return;
        // 用函数式的方法解决这个问题...?
        switch (token.type) {
            case PLUS:
                to_return = (DigitValue) this.expr.walk();
                break;
            case MINUS:
                // express negetive
                to_return = ((DigitValue) this.expr.walk()).getNegative();
                break;
            default:
                // TODO: add exception here
                throw new RuntimeException("SyntaxError in UnaryOp with token " + token);
        }
        return to_return;
    }
}

class Forloop extends AST {
    private String value_name;  //循环变量名称
    private AssignNode init;
    private AST upper;
    private boolean inc;    //是否递增
    private AST exec;
    Forloop() {
        super(new Token(TokenType.FOR,null));
    }

    @Override
    NoneValue walk() {
        init.walk();
        ValueObject variable = SymbolTable.lookup(value_name);
        if (variable == null) {
            throw new RuntimeException("Didn'd declear " + value_name + " before use it");
        }
        int upper_value = (int)upper.walk().getValue();
        int cur_value = (int)(SymbolTable.lookup(value_name).getValue());
        while (cur_value != upper_value) {
            exec.walk();
            if (inc) {
                ++cur_value;
            } else {
                --cur_value;
            }
        }
        return new NoneValue();
    }

    public void setInc(boolean inc) {
        this.inc = inc;
    }

    public void setInit(AssignNode init) {
        this.init = init;
    }

    public void setUpper(AST upper) {
        this.upper = upper;
    }

    public void setValue_name(String value_name) {
        this.value_name = value_name;
    }

    public void setExec(AST exec) {
        this.exec = exec;
    }
}

/*
BINARY OP
 */
class BinaryOp extends AST {
    AST left;
    AST right;

    BinaryOp(Token token, AST left, AST right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    @Override
    DigitValue walk() {
        DigitValue to_return;
        // 用函数式的方法解决这个问题...?
        DigitValue left_walk = (DigitValue) this.left.walk();
        DigitValue right_walk = (DigitValue) this.right.walk();
        switch (token.type) {
            case PLUS:
                to_return = DigitValue.add(left_walk, right_walk);
                break;
            case MINUS:
                to_return = DigitValue.minus(left_walk, right_walk);
                break;
            case MUL:
                to_return = DigitValue.multiply(left_walk, right_walk);
                break;
            case DIV:
                to_return = DigitValue.division(left_walk, right_walk);
                break;
            default:
                throw new RuntimeException("Syntax Error");
                // TODO: add error to raise here
        }
        return to_return;
    }
}

class CompoundWithCtx extends Compound {
    @Override
    ValueObject walk() {
        SymbolTable.InitializeScope();

        super.walk();
        SymbolTable.FinalizeScope();
        // return a new none value
        return new NoneValue();
    }
}

// temporary abs
// 表示一个能够拥有多个容器的元素类
// 会开启上下文
class Compound extends AST {
    ArrayList<AST> components = new ArrayList<>();

    protected void append_ast(AST expr) {
        components.add(expr);
    }
    protected AST get_node(int index) {
        return components.get(index);
    }
    Compound() {
        // create a null value to Object
        super(new Token(TokenType.NULLVALUE, NullValue.getInstance()));
    }

    void add_typedecl(Compound identifies) {
        components.add(0, identifies);
    }

    @Override
    ValueObject walk() {

        for (int i = 0; i < components.size(); i++) {
            components.get(i).walk();
        }

        // return a new none value
        return new NoneValue();
    }
}

class WriteStatement extends Compound {
    @Override
    ValueObject walk() {

        for (int i = 0; i < components.size(); i++) {
            System.out.print(components.get(i).walk());
            System.out.print(" ");
        }
        System.out.print('\n');
        // return a new none value
        return new NoneValue();
    }
}

class ValueNode extends AST {
    ValueNode(Token value_token) {
        super(value_token);
    }

    @Override
    DigitValue walk() {
        if (this.token.type == TokenType.INTEGER) {
            return new IntegerValue((Integer) this.token.value);
        } else if (this.token.type == TokenType.REAL) {
            return new RealValue((Double)this.token.value);
        } else if (this.token.type == TokenType.VAR) {
            DigitValue value = (DigitValue) SymbolTable.lookup((String) this.token.value);
//            if (value == null) {
//                throw new RuntimeException("Var in expression is null.");
//            }
            return value;
        } else {
            // TODO: need to raise error?
            return null;
        }
    }
}


class AssignNode extends AST {
    private AST left_expr;
    private AST right_expr;

    public AssignNode(Token assign_token, AST left, AST right) {
        super(assign_token);
        this.left_expr = left;
        this.right_expr = right;
    }

    @Override
    NoneValue walk() {
        // pseudo return value
        if (left_expr.token.type == TokenType.VAR) {
            // TODO: 明确能否直接修改引用？对于左侧的WALK是否是必须的 --> 好吧，我们应该试着直接"修改"值
            // initialize left,
            if (!(left_expr instanceof Var)) {
                throw new RuntimeException("Left " + left_expr + " in " + this + " is not a variable node.");
            }
            String var_name = ((Var)left_expr).getName();
            SymbolTable.update(var_name, right_expr.walk());
        }
        return new NoneValue();
    }
}

/*
TODO: check what we can do better when we use EmptyNode
 */
class EmptyNode extends AST {
    EmptyNode() {
        super(new Token(TokenType.EMPTY, new NoneValue()));
    }

    @Override
    ValueObject walk() {
        return new NoneValue();
    }
}

/*
 语法分析
 */
public class Parser {
    private Lexer lexer;
    private Token current_token;
    public Parser(Lexer lexer) {
        current_token = lexer.get_next_token();
        this.lexer = lexer;
    }

    private Forloop forloop() {
        /*
        for assign (to|downto) value
        compound_statement|statement
         */
        Forloop forloop = new Forloop();
        lexer.eat_token(TokenType.FOR);
        current_token = lexer.get_next_token();
        forloop.setValue_name((String)current_token.value);
        AssignNode assignNode = assignment_statement();
        forloop.setInit(assignNode);
        if (current_token.type == TokenType.TO) {
            forloop.setInc(true);
        } else if (current_token.type == TokenType.DOWNTO) {
            forloop.setInc(false);
        } else {
            throw new RuntimeException("Cannot found token to or downto in for loop");
        }
        lexer.eat_token(current_token.type);
        current_token = lexer.get_next_token();

        if (current_token.type == TokenType.INTEGER) {
            forloop.setUpper(factor());
            lexer.eat_token(TokenType.INTEGER);
            current_token = lexer.get_next_token();
        } else {
            throw new RuntimeException("Cannot found end in forloop.");
        }

        lexer.eat_token(TokenType.DO);
        current_token = lexer.get_next_token();
        if (current_token.type == TokenType.BEGIN) {
            forloop.setExec(compound_statement());
        } else {
            forloop.setExec(statement());
        }
        return forloop;
    }

    private WriteStatement writeln_statement() {
        /*
        writeln( expr , )
         */
        lexer.eat_token(TokenType.WRITELN);
        current_token = lexer.get_next_token();
        lexer.eat_token(TokenType.LPAR);
        current_token = lexer.get_next_token();

        WriteStatement writeStatement = new WriteStatement();

        AST current_ast = expr();
        writeStatement.append_ast(current_ast);

        while (current_token.type == TokenType.COMMA) {
            lexer.eat_token(TokenType.COMMA);
            current_token = lexer.get_next_token();

            current_ast = expr();
            writeStatement.append_ast(current_ast);
        }


        lexer.eat_token(TokenType.RPAR);
        current_token = lexer.get_next_token();

        return writeStatement;
    }
    
    /*
    """
    compound_statement: BEGIN statement_list END
    """
     */
    private CompoundWithCtx compound_statement() {
        lexer.eat_token(TokenType.BEGIN);
        current_token = lexer.get_next_token();
        ArrayList<AST> statements = statement_list();
        lexer.eat_token(TokenType.END);
        current_token = lexer.get_next_token();

        CompoundWithCtx compound = new CompoundWithCtx();
        for (AST statement:
             statements) {
            compound.append_ast(statement);
        }

        return compound;
    }

    /*
    """
    statement_list : statement
                   | statement SEMI statement_list
    """
     */
    private ArrayList<AST> statement_list() {
        ArrayList<AST> statements = new ArrayList<>();
        AST cur_state = statement();
        statements.add(cur_state);
        while (current_token.type == TokenType.SEMI || current_token.type == TokenType.FOR) {
            if (current_token.type == TokenType.SEMI) {
                lexer.eat_token(TokenType.SEMI);
                current_token = lexer.get_next_token();
                if (current_token.type != TokenType.VAR && current_token.type != TokenType.WRITELN && current_token.type != TokenType.FOR) {
                    break;
                }
                cur_state = statement();
                statements.add(cur_state);
            } else if (current_token.type == TokenType.FOR) {
                cur_state = forloop();
                statements.add(cur_state);
            }
        }

        return statements;
    }

    /*
    """program : compound_statement DOT"""
     */
    private AST program() {
        Compound decl = declarations();
        Compound node = compound_statement();
        node.add_typedecl(decl);
        lexer.eat_token(TokenType.DOT);
        current_token = lexer.get_next_token();
        return node;
    }

    private AST empty() {
        return new EmptyNode();
    }

    /*
    statement : compound_statement
              | assignment_statement
              | empty
    程序执行的语句
     */
    private AST statement() {
        AST statement_node;
        // TODO: 添加一个expr, 类似REPL这样的交互形式的语法
        // what's wrong? should we care about expr
        TokenType t = current_token.type;
        // http://blog.csdn.net/u012230055/article/details/73469202
        switch (t) {
            case BEGIN:
                statement_node = compound_statement();
                break;
            case VAR:
                statement_node = assignment_statement();
                break;
            case WRITELN:
                statement_node = writeln_statement();
                break;
            default:
                statement_node = empty();
                break;
        }
        return statement_node;
    }

    /*
    get from AST and return a variable
     */
    private Var variable() {
        //  if it's a var

        String var_name = (String)current_token.value;
        lexer.eat_token(TokenType.VAR);
        current_token = lexer.get_next_token();
        return new Var(var_name);
    }



    /*
    assignment_statement : variable ASSIGN expr
     */
    private AssignNode assignment_statement() {
        Var left = variable();
        // eat :=
        if (current_token.type == TokenType.ASSIGN) {
            Token assign_token = current_token;
            lexer.eat_token(TokenType.ASSIGN);
            current_token = lexer.get_next_token();
            AST right = expr();
            return new AssignNode(assign_token, left, right);
        } else {
            // what to do else...?
            // pass first
            throw new RuntimeException("Can't found assign after variable " + left.token);
        }

    }

    /*
    factor : PLUS  factor
              | MINUS factor
              | INTEGER
              | LPAREN expr RPAREN
              | variable
     */
    private AST factor() {
        AST to_return;
//        Integer to_return;
        TokenType last_token_type = current_token.type;
        Token last_token = current_token;
        // eat_token before do another
        lexer.eat_token(last_token_type);
        current_token = lexer.get_next_token();

        switch (last_token_type) {
            case INTEGER:
            case REAL:
                to_return = new ValueNode(last_token);
                break;
            case LPAR:
                to_return = expr();
                // 吃掉右括号
                lexer.eat_token(TokenType.RPAR);
                current_token = lexer.get_next_token();
                break;
            case MINUS:
                // 返回带负节点的值
                to_return = new UnaryOp(new Token(TokenType.MINUS, '-'), factor());
                break;
            case PLUS:
                // 什么时候递归深入
                to_return = factor();
                break;
            case VAR:
                to_return = new ValueNode(last_token);
                break;
            default:
//                break;
                throw new RuntimeException("Syntax Error " + current_token);
        }
        return to_return;
    }


    private final static Set<TokenType> FACTOR_VALID_SET = new HashSet<TokenType>(
            Arrays.asList(TokenType.MUL, TokenType.DIV)
    );
    private AST term() {
        // term -> factor((PLUS|MUL)term)*
        AST current_ast = factor();
        while (FACTOR_VALID_SET.contains(this.current_token.type)) {
            Token last_token = this.current_token;
            lexer.eat_token(this.current_token.type);
            current_token = lexer.get_next_token();
            current_ast = new BinaryOp(last_token, current_ast, factor());

        }
        return current_ast;
    }

    private final static Set<TokenType> EXPR_VALID_SET = new HashSet<TokenType>(
            Arrays.asList(TokenType.PLUS, TokenType.MINUS)
    );
    // expr -> term((ADD|MINUS)term)*
    private AST expr() {
        // current_token = lexer.get_next_token();
        AST expr_value = term();
        while (EXPR_VALID_SET.contains(this.current_token.type)) {
            Token last_token = this.current_token;
            lexer.eat_token(this.current_token.type);
            current_token = lexer.get_next_token();
            expr_value = new BinaryOp(last_token, expr_value, term());

        }
        return expr_value;
    }

    private Compound declarations () {
        /*
        VAR
        (var_declaration SEMI)+
         */
        Compound compound = new Compound();
        if (current_token.type == TokenType.VARDECL) {
            lexer.eat_token(TokenType.VARDECL);
            current_token = lexer.get_next_token();

            while (current_token.type == TokenType.VAR) {
                TypeIdentify typeIdentify;
                ArrayList<String> decl_var_names = new ArrayList<>();
                while (current_token.type == TokenType.VAR) {
                    decl_var_names.add((String) current_token.value);

                    lexer.eat_token(TokenType.VAR);
                    current_token = lexer.get_next_token();
                    if (current_token.type == TokenType.COMMA) {
                        lexer.eat_token(current_token.type);
                        current_token = lexer.get_next_token();
                    } else {
                        break;
                    }
                }
                if (current_token.type == TokenType.COLON) {
                    lexer.eat_token(current_token.type);
                    current_token = lexer.get_next_token();
                    if (current_token.type == TokenType.INTTYPE || current_token.type == TokenType.REALTYPE) {
                        typeIdentify = new TypeIdentify(current_token);
                        for (String s:
                                decl_var_names) {
                            typeIdentify.add_var_name(s);
                        }
                    } else {
                        throw new RuntimeException("No INTEGER OR Real here.");
                    }
                    lexer.eat_token(current_token.type);
                    current_token = lexer.get_next_token();
                } else {
                    throw new RuntimeException("Cannot find COLON after var decl");
                }
                // eat semi
                lexer.eat_token(TokenType.SEMI);
                current_token = lexer.get_next_token();
                compound.append_ast(typeIdentify);
            }

        } else {
            // 没有声明，什么都没发生
            return null;
        }
        return compound;
    }


    AST parse() {
        // TODO: 修改此处的程序结构
        // return the parse result of the expression
        return this.program();
    }
}
