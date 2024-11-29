package expression.generic.calculator;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class CheckedIntegerCalculator implements Calculator<Integer>{

    @Override
    public Integer add(Integer firstOperand, Integer secondOperand) {
        if (firstOperand > 0 && Integer.MAX_VALUE - firstOperand < secondOperand
                || firstOperand < 0 && Integer.MIN_VALUE - firstOperand > secondOperand) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " + " + secondOperand);
        }
        return firstOperand + secondOperand;
    }

    @Override
    public Integer subtract(Integer firstOperand, Integer secondOperand) {
        if (secondOperand > 0 && Integer.MIN_VALUE + secondOperand > firstOperand
                || secondOperand < 0 && Integer.MAX_VALUE + secondOperand < firstOperand) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " - " + secondOperand);
        }
        return firstOperand - secondOperand;
    }

    @Override
    public Integer multiply(Integer firstOperand, Integer secondOperand) {
        if (secondOperand != 0 && firstOperand * secondOperand / secondOperand != firstOperand
                || (firstOperand == Integer.MIN_VALUE && secondOperand == -1)
                || (secondOperand == Integer.MIN_VALUE && firstOperand == -1)) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " * " + secondOperand);
        }
        return firstOperand * secondOperand;
    }

    @Override
    public Integer divide(Integer firstOperand, Integer secondOperand) {
        if (firstOperand == Integer.MIN_VALUE && secondOperand == -1) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " / " + secondOperand);
        }
        if (secondOperand == 0) {
            throw new DivisionByZeroException();
        }
        return firstOperand / secondOperand;
    }

    @Override
    public Integer negation(Integer firstOperand) {
        if (firstOperand == Integer.MIN_VALUE) {
            throw new OverflowException("negation to " + firstOperand);
        }
        return -firstOperand;
    }

    @Override
    public Integer parseValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new OverflowException("number is too big: " + s);
        }
    }

    @Override
    public Integer getFromInt(int value) {
        return value;
    }

}
