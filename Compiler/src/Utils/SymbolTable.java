package Utils;

import Utils.Value.BuiltinTypeObject;
import Utils.Value.NoneValue;
import Utils.Value.ValueObject;
import Utils.Value.ValueType;

import java.util.HashMap;
import java.util.Map;

/*
 * 符号表，用于处理语法中的符号
 * 在运行时动态维护
 * 可以在符号表中维护类型和类型的级别，以便于维护类型
 * 提供声明变量变量(declear) 查找(lookup) 更新变量(update)的功能
 */
public class SymbolTable {
    private SymbolTable father_table;                       // 符号表的父表
    private HashMap<String, Variable> syntax_table;      //符号表

    // 最初的是全局变量的符号表
    static final private SymbolTable GLOBAL = new SymbolTable();
    static private SymbolTable current_table = GLOBAL;      // 先初始化为GLOBAL的信息
    static {
        // 添加内置的类型
        current_table.table_insert("INTEGER", new BuiltinTypeObject(new Token(TokenType.TYPE, "INTEGER")));
        current_table.table_insert("REAL", new BuiltinTypeObject(new Token(TokenType.TYPE, "REAL")));
    }

    static public ValueObject lookup(String name) {
        SymbolTable search_table = current_table;       // 搜索中的符号表
        ValueObject find = null;
        while (search_table != null && find == null) {
            find = search_table.table_lookup(name);
            if (find == null) {
                // 链式处理
                search_table = search_table.father_table;
            }
        }
        return find;
    }
    /*
    初始的创建一个作用域, 并初始化一张符号表
     */
    static public void InitializeScope() {
        current_table = new SymbolTable(current_table);
    }

    static public void FinalizeScope() {
        System.out.println(current_table);
        System.out.println("Is being destroyed.\n\n");
        if (current_table.father_table == null) {
            throw new RuntimeException("Call Finalize Scope error.");
        } else {
            current_table = current_table.father_table;
        }

    }

    /*
    访问变量名称并初始化
     */
    static public ValueObject lookup_or_insertnull(String var_name) {
        ValueObject var = lookup(var_name);
        if (var == null) {
            var = new NoneValue();
            current_table.table_insert(var_name, var);
        }
        return var;
    }

    static boolean insert(String key, ValueObject value) {
        return current_table.table_insert(key, value);
    }

    private static boolean found(String key) {
        SymbolTable update_table = current_table;
        boolean updated = false;
        while (update_table != null) {
            updated = update_table.syntax_table.containsKey(key);
            if (updated) {
                break;
            } else {
                update_table = update_table.father_table;
            }
        }
        return updated;
    }

    static boolean update(String key, ValueObject new_value) {
        SymbolTable update_table = current_table;
        boolean updated = false;
        while (update_table != null) {
            updated = update_table.table_update(key, new_value);
            if (updated) {
                break;
            } else {
                update_table = update_table.father_table;
            }
        }
        return updated;
    }

//    static public boolean force_update(String key, ValueObject new_value) {
//        if (update(key, new_value)) {
//            return true;
//        } else {
//            current_table.syntax_table.put(key, new_value);
//            return true;
//        }
//    }

    private SymbolTable() {
        syntax_table = new HashMap<>();
        father_table = null;
    }

    private SymbolTable(SymbolTable father) {
        syntax_table = new HashMap<>();
        father_table = father;
    }

    public boolean table_insert(String key, ValueObject value) {
        Variable last_value = syntax_table.putIfAbsent(key, new Variable(value.getType()));
        return last_value == null;
    }

    /*
    update if exists
    TODO: 搞清楚这个的优化...
     */
    public boolean table_update(String key, ValueObject update) {
        Variable variable = syntax_table.get(key);
        if (variable == null) return false;
        variable.setValue(update);
        return true;
    }

    // Java名字查找还有这样的问题么
    private ValueObject table_lookup(String key) {
        Variable variable = syntax_table.get(key);
        if (variable == null) return null;
        else return variable.getValue();
    }

    /*
    移除变量，我们似乎不需要？
     */
    @Deprecated
    private void _table_remove(String key) {
        syntax_table.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Symbol table:\n");
        for (Map.Entry<String, Variable> entry:
             syntax_table.entrySet()) {

            builder.append("Key: " + entry.getKey() + "    Value" + entry.getValue() + "\n");
        }
        return builder.toString();
    }
}

/*
变量值的类型
能够完成基本的类型转换 类型提升的判断
 */
enum VariableType {
    INTEGER(0),
    REAL(1);

    private int anInt;
    VariableType(int i) {
        anInt = i;
    }
}
