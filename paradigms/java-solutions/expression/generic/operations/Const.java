package expression.generic.operations;

import expression.generic.*;
import expression.generic.calculator.Calculator;

public class Const<T> implements GenericExpression<T>{

    private final T value;

    public Const(T value) {
        this.value = value;
    }

    @Override
    public T evaluate(T x, Calculator<T> calculator) {
        return value;
    }

    @Override
    public T evaluate(T x, T y, T z, Calculator<T> calculator) {
        return value;
    }
}
