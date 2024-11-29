package expression.generic.operations;

import expression.generic.*;
import expression.generic.calculator.Calculator;

public abstract class GenericBinOperations<T> implements GenericExpression<T>{

    private final String operation;

    private final GenericExpression<T> firstOperand;

    private final GenericExpression<T> secondOperand;

    protected GenericBinOperations(GenericExpression<T> firstOperand,
                                   GenericExpression<T> secondOperand, String operation) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operation = operation;
    }

    protected abstract T calc(T firstOperand, T secondOperand, Calculator<T> calculator);

    @Override
    public T evaluate(T x, Calculator<T> calculator) {
        return calc(firstOperand.evaluate(x, calculator), secondOperand.evaluate(x, calculator), calculator);
    }

    @Override
    public T evaluate(T x, T y, T z, Calculator<T> calculator) {
        return calc(firstOperand.evaluate(x, y, z, calculator), secondOperand.evaluate(x, y, z, calculator), calculator);
    }

    @Override
    public String toString() {
        return "(" + firstOperand.toString() + " " + operation + " " + secondOperand.toString() + ")";
    }
}
