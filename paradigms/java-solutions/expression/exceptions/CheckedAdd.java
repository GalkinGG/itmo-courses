package expression.exceptions;

import expression.*;

public class CheckedAdd extends Add {

    public CheckedAdd(AllExpressions firstOperand, AllExpressions secondOperand) {
        super(firstOperand, secondOperand);
    }

    @Override
    protected int calc(int firstOperand, int secondOperand) {
        if (firstOperand > 0 && Integer.MAX_VALUE - firstOperand < secondOperand
                || firstOperand < 0 && Integer.MIN_VALUE - firstOperand > secondOperand) {
            throw new OverflowException("Overflow during calculation " + firstOperand + " + " + secondOperand);
        }
        return super.calc(firstOperand, secondOperand);
    }
}
