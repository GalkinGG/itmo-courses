package expression.generic.calculator;

public class DoubleCalculator implements Calculator<Double>{

    @Override
    public Double add(Double firstOperand, Double secondOperand) {
        return firstOperand + secondOperand;
    }

    @Override
    public Double subtract(Double firstOperand, Double secondOperand) {
        return firstOperand - secondOperand;
    }

    @Override
    public Double multiply(Double firstOperand, Double secondOperand) {
        return firstOperand * secondOperand;
    }

    @Override
    public Double divide(Double firstOperand, Double secondOperand) {
        return firstOperand/secondOperand;
    }

    @Override
    public Double negation(Double firstOperand) {
        return -firstOperand;
    }

    @Override
    public Double parseValue(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public Double getFromInt(int value) {
        return (double) value;
    }
}
