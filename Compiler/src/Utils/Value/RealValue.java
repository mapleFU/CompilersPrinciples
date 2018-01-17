package Utils.Value;


import java.util.Objects;

public class RealValue extends DigitValue {
    public RealValue(Double real) {
        super(real);
        this.type = ValueType.REAL;
    }


    @Override
    public Double getValue() {
        return (Double) this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RealValue) {
            return Objects.equals(this.getValue(), ((RealValue) other).getValue());
        }
        return false;
    }

    @Override
    public void negative() {
        this.value = -((Double)this.value);
    }

    @Override
    public RealValue getNegative() {
        double new_value = -((Double)this.value);
        return new RealValue(new_value);
    }
}
