package expression.generic;

import expression.generic.calculator.Calculator;

public interface GenericExpression<T> {

    T evaluate (T x, Calculator<T> calculator);

    T evaluate (T x, T y, T z, Calculator<T> calculator);

    public String toString();
}
