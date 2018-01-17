package Utils.Value;


import java.util.function.BiFunction;

abstract public class DigitValue extends ValueObject {
    public DigitValue(Number digit) {
        super(digit);
    }

    static boolean hasReal(DigitValue d1, DigitValue d2) {
        if (d1.getClass().equals(RealValue.class)) {
            return true;
        } else if (d2.getClass().equals(RealValue.class)) {
            return true;
        }
        return false;
    }



    private static Double getDoubles(DigitValue d1, DigitValue d2, BiFunction<Double, Double, Double> function) {
        double v1, v2;
        if (d1.getClass().equals(IntegerValue.class)) {
            int i1 = (Integer)d1.getValue();
            v1 = (double)(i1);
        } else {
            v1 = (Double)d1.getValue();
        }
        // d2
        if (d2.getClass().equals(IntegerValue.class)) {
            // if it's integer
            int i2 = (Integer)d2.getValue();
            v2 = (double)(i2);
        } else {
            v2 = (Double)d2.getValue();
        }
        return function.apply(v1, v2);
    }

    public static DigitValue minus(DigitValue d1, DigitValue d2) {
        return add(d1, d2.getNegative());
    }

    public static DigitValue division(DigitValue d1, DigitValue d2) {
        if (!hasReal(d1, d2)) {
            return new IntegerValue((Integer) d1.getValue() / (Integer) d2.getValue());
        } else {
            Double real_value = getDoubles(d1, d2, (Double v1, Double v2) -> v1 / v2);
            return new RealValue(real_value);
        }
    }

    public static DigitValue multiply(DigitValue d1, DigitValue d2) {
        if (!hasReal(d1, d2)) {
            return new IntegerValue((Integer) d1.getValue() * (Integer) d2.getValue());
        } else {
            Double real_value = getDoubles(d1, d2, (Double v1, Double v2) -> v1 * v2);
            return new RealValue(real_value);
        }
    }

    public static DigitValue add(DigitValue d1, DigitValue d2) {
        if (!hasReal(d1, d2)) {
            return new IntegerValue((Integer) d1.getValue() + (Integer) d2.getValue());
        } else {
            Double real_value = getDoubles(d1, d2, (Double v1, Double v2) -> v1 + v2);
            return new RealValue(real_value);
        }
    }

    @Override
    public Number getValue() {
        return (Number)this.value;
    }

    /*
    取反，默认的实现是抛出异常的
     */
    public void negative() {
        throw new UnsupportedOperationException("This ValueObject is " + type + ", it doesn't support negative");
    }

    public DigitValue getNegative() {
        throw new UnsupportedOperationException("This ValueObject is " + type + ", it doesn't support negative");
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    static public void main(String[] args) {
        RealValue real = new RealValue(2.73);
        IntegerValue integer = new IntegerValue(4);
        IntegerValue integer2 = new IntegerValue(5);
        System.out.println(add(integer2, integer).value);
        System.out.println(add(real, integer).value);
    }
}
