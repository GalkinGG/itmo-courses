package expression;

public class Count implements AllExpressions {

    private final AllExpressions term;

    public Count(AllExpressions term) {
        this.term = term;
    }

    @Override
    public int evaluate(int var) {
        return Integer.bitCount(term.evaluate(var));
    }

    @Override
    public double evaluate(double var) {
        return 0;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return Integer.bitCount(term.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "count(" + term + ")";
    }
}
