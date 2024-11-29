package expression.generic.operations;

import expression.generic.*;
import expression.generic.calculator.Calculator;

public class Subtract<T> extends GenericBinOperations<T> {

    public Subtract(GenericExpression<T> firstOperand, GenericExpression<T> secondOperand) {
        super(firstOperand, secondOperand, "-");
    }

    @Override
    protected T calc(T firstOperand, T secondOperand, Calculator<T> calculator) {
        return calculator.subtract(firstOperand, secondOperand);
    }
}
