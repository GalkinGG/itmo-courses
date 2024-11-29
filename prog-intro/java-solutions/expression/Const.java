package expression;

import java.util.Objects;

public class Const implements AllExpressions {

    private int intValue;

    private double doubleValue;

    private boolean valueType = false;
    
    public Const(int value) {
        this.intValue = value;
        this.valueType = true;
    }

    public Const(double value) {
        this.doubleValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    @Override
    public int evaluate(int var) {
        return intValue;
    }

    @Override
    public double evaluate(double var) {
        return doubleValue;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return intValue;
    }

    @Override
    public String toString() {
        if (valueType) {
            return Integer.toString(intValue);
        } else {
            return Double.toString(doubleValue);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Const exp) {
            if (valueType) {
                return this.getIntValue() == exp.getIntValue();
            } else {
                return this.getDoubleValue() == exp.getDoubleValue();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intValue) + Objects.hash(doubleValue);
    }
}
