package expression.exceptions;

import expression.*;


public class CheckedNegate extends Negation {

    public CheckedNegate(AllExpressions term) {
        super(term);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        if (super.getTerm().evaluate(x, y, z) == Integer.MIN_VALUE) {
            throw new OverflowException("negation to " + getTerm());
        }
        return super.evaluate(x, y, z);
    }
}
