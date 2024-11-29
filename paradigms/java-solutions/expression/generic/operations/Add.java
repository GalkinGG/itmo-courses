package expression.generic.operations;

import expression.generic.*;
import expression.generic.calculator.Calculator;

public class Add<T> extends GenericBinOperations<T> {

    public Add(GenericExpression<T> firstOperand, GenericExpression<T> secondOperand) {
        super(firstOperand, secondOperand, "+");
    }

    @Override
    protected T calc(T firstOperand, T secondOperand, Calculator<T> calculator) {
        return calculator.add(firstOperand, secondOperand);
    }

}
