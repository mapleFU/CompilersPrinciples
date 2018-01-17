package Utils.Value;

import java.util.Objects;

public class IntegerValue extends DigitValue {
    public IntegerValue(Integer integer) {
        super(integer);
        this.type = ValueType.INTEGER;
    }


    @Override
    public Integer getValue() {
        return (Integer) this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof IntegerValue) {
            return Objects.equals(this.getValue(), ((RealValue) other).getValue());
        }
        return false;
    }

    @Override
    public IntegerValue getNegative() {
        int new_value = -((Integer)this.value);
        return new IntegerValue(new_value);
    }


}
