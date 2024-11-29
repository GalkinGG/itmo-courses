package expression.generic.operations;

import expression.generic.*;
import expression.generic.calculator.Calculator;

public class Negation<T> implements GenericExpression<T>{

    private final GenericExpression<T> term;

    public Negation(GenericExpression<T> term) {
        this.term = term;
    }

    @Override
    public T evaluate(T x, Calculator<T> calculator) {
        return calculator.negation(term.evaluate(x, calculator));
    }

    @Override
    public T evaluate(T x, T y, T z, Calculator<T> calculator) {
        return calculator.negation(term.evaluate(x, y, z, calculator));
    }
}
