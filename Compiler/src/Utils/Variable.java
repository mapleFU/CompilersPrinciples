package Utils;

import Utils.Value.*;

import java.util.Objects;


/*
store variable with valueobject and it's name
 */
public class Variable {
    //    private ArrayList<String> variable_names;
    private ValueObject value;

    public ValueType getType() {
        return type;
    }

    private ValueType type;
    public Variable(ValueType type) {
        this.type = type;
    }
    public Variable(ValueObject value) {
        this.type = value.getType();
        this.value = value;
    }

    public ValueObject getValue() {
        return value;
    }

    // TODO: update this to make it's type specific
    public void setValue(ValueObject value) {
        if (value == null) {
            throw new RuntimeException("value in setValue can't be null.");
        }
        if (!(value instanceof DigitValue)) {
            throw new RuntimeException("Value " + value + " is not a digit value.");
        }
        DigitValue digitValue = (DigitValue)value;
        if (type == ValueType.INTEGER) {
            if (digitValue.getClass() == RealValue.class) {
                double v = (Double)value.getValue();
                int i = (int)(v);
                this.value = new IntegerValue(i);
            } else {
                this.value = new IntegerValue((int)digitValue.getValue());
            }
        } else if (type == ValueType.REAL) {
            // 假设VALUETYPE可以被提升类型
            // cast integer to double
            if (digitValue.getClass() == IntegerValue.class) {
                int i = (Integer)value.getValue();
                double v = (double)(i);
                this.value = new RealValue(v);
            } else {
                this.value = new RealValue((double)digitValue.getValue());
            }
        }
    }


    // shenmegui
    @Override
    public boolean equals(Object other_var) {
        if (other_var instanceof Variable) {
            if (((Variable) other_var).type == this.type) {
                if (Objects.equals(((Variable) other_var).value, this.value))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Variable(" + type + ", " + value + ")";
    }
}