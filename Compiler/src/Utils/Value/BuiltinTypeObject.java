package Utils.Value;

import Utils.Token;

public class BuiltinTypeObject extends ValueObject {
    public BuiltinTypeObject(Token value) {
        super(value);
        this.type = ValueType.BUILTINTYPE;
    }

    /*
    return token for this type
     */
    @Override
    public Token getValue() {
        return (Token) this.value;
    }

    public String get_type() {
        Token value = getValue();
        return (String)value.value;
    }
}
