package expression.exceptions;

import expression.*;

public class CheckedMultiply extends Multiply {

    public CheckedMultiply(AllExpressions firstOperator, AllExpressions secondOperation) {
        super(firstOperator, secondOperation);
    }

    @Override
    protected int calc(int firstOperand, int secondOperand) {
        if (secondOperand != 0 && firstOperand * secondOperand / secondOperand != firstOperand
                || (firstOperand == Integer.MIN_VALUE && secondOperand == -1)
                || (secondOperand == Integer.MIN_VALUE && firstOperand == -1)) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " * " + secondOperand);
        }
        return super.calc(firstOperand, secondOperand);
    }
}
