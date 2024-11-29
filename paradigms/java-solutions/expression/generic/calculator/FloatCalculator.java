package expression.generic.calculator;

public class FloatCalculator implements Calculator<Float>{
    @Override
    public Float add(Float firstOperand, Float secondOperand) {
        return firstOperand + secondOperand;
    }

    @Override
    public Float subtract(Float firstOperand, Float secondOperand) {
        return firstOperand - secondOperand;
    }

    @Override
    public Float multiply(Float firstOperand, Float secondOperand) {
        return firstOperand * secondOperand;
    }

    @Override
    public Float divide(Float firstOperand, Float secondOperand) {
        return firstOperand/secondOperand;
    }

    @Override
    public Float negation(Float firstOperand) {
        return -firstOperand;
    }

    @Override
    public Float parseValue(String s) {
        return Float.parseFloat(s);
    }

    @Override
    public Float getFromInt(int value) {
        return (float) value;
    }
}
