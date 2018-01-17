package Utils.Value;

import java.util.Objects;



abstract public class ValueObject {
    protected Object value;
    protected ValueType type;

    public ValueObject(Object value) {
        this.value = value;
    }

    public ValueType getType() {
        return type;
    }

    // 获得存储的值，根据类型作出不同的返回
    abstract public Object getValue();

    @Override
    public boolean equals(Object other) {
        return Objects.equals(this, other);
    }
}





