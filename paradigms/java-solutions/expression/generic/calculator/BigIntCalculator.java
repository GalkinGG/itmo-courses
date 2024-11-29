package expression.generic.calculator;

import expression.exceptions.DivisionByZeroException;

import java.math.BigInteger;

public class BigIntCalculator implements Calculator<BigInteger>{

    @Override
    public BigInteger add(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.add(secondOperand);
    }

    @Override
    public BigInteger subtract(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.subtract(secondOperand);
    }

    @Override
    public BigInteger multiply(BigInteger firstOperand, BigInteger secondOperand) {
        return firstOperand.multiply(secondOperand);
    }

    @Override
    public BigInteger divide(BigInteger firstOperand, BigInteger secondOperand) {
        if (secondOperand.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException();
        }
        return firstOperand.divide(secondOperand);
    }

    @Override
    public BigInteger negation(BigInteger firstOperand) {
        return firstOperand.negate();
    }

    @Override
    public BigInteger parseValue(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigInteger getFromInt(int value) {
        return BigInteger.valueOf(value);
    }
}
