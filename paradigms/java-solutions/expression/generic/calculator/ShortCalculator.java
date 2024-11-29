package expression.generic.calculator;

import expression.exceptions.DivisionByZeroException;

public class ShortCalculator implements Calculator<Short>{
    @Override
    public Short add(Short firstOperand, Short secondOperand) {
        return  (short) (firstOperand + secondOperand);
    }

    @Override
    public Short subtract(Short firstOperand, Short secondOperand) {
        return (short) (firstOperand - secondOperand);
    }

    @Override
    public Short multiply(Short firstOperand, Short secondOperand) {
        return (short) (firstOperand * secondOperand);
    }

    @Override
    public Short divide(Short firstOperand, Short secondOperand) {
        if (secondOperand == 0) {
            throw new DivisionByZeroException();
        }
        return (short) (firstOperand/secondOperand);
    }

    @Override
    public Short negation(Short firstOperand) {
        return (short) -firstOperand;
    }

    @Override
    public Short parseValue(String s) {
        return Short.valueOf(s);
    }

    @Override
    public Short getFromInt(int value) {
        return (short) value;
    }
}
