package expression.generic.calculator;

import expression.exceptions.DivisionByZeroException;

public class UncheckedIntegerCalculator implements Calculator<Integer> {

    @Override
    public Integer add(Integer firstOperand, Integer secondOperand) {
        return firstOperand + secondOperand;
    }

    @Override
    public Integer subtract(Integer firstOperand, Integer secondOperand) {
        return firstOperand - secondOperand;
    }

    @Override
    public Integer multiply(Integer firstOperand, Integer secondOperand) {
        return firstOperand * secondOperand;
    }

    @Override
    public Integer divide(Integer firstOperand, Integer secondOperand) {
        if (secondOperand == 0) {
            throw new DivisionByZeroException();
        }
        return firstOperand/secondOperand;
    }

    @Override
    public Integer negation(Integer firstOperand) {
        return -firstOperand;
    }

    @Override
    public Integer parseValue(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public Integer getFromInt(int value) {
        return value;
    }
}
