package Utils.Value;

public class NoneValue extends ValueObject {
    public NoneValue() {
        super(new Object());
        this.type = ValueType.NONTYPE;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NoneValue;
    }
}
