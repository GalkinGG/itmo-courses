package expression.exceptions;

import expression.*;

public class CheckedSubtract extends Subtract{

    public CheckedSubtract(AllExpressions firstOperator, AllExpressions secondOperation) {
        super(firstOperator, secondOperation);
    }

    @Override
    protected int calc(int firstOperand, int secondOperand) {
        if (secondOperand > 0 && Integer.MIN_VALUE + secondOperand > firstOperand
                || secondOperand < 0 && Integer.MAX_VALUE + secondOperand < firstOperand) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " - " + secondOperand);
        }
        return super.calc(firstOperand, secondOperand);
    }
}
