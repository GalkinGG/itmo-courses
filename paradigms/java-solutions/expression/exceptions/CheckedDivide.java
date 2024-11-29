package expression.exceptions;

import expression.*;

public class CheckedDivide extends Divide {
    public CheckedDivide(AllExpressions firstOperator, AllExpressions secondOperation) {
        super(firstOperator, secondOperation);
    }

    @Override
    protected int calc(int firstOperand, int secondOperand) {
        if (firstOperand == Integer.MIN_VALUE && secondOperand == -1) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " / " + secondOperand);
        } else if (secondOperand == 0) {
            throw new DivisionByZeroException();
        }
        return super.calc(firstOperand, secondOperand);
    }
}
