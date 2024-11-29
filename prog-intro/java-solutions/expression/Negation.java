package expression;

public class Negation implements AllExpressions{

    private final AllExpressions term;

    public Negation(AllExpressions term) {
        this.term = term;
    }
    @Override
    public int evaluate(int var) {
        return new Subtract(new Const(0), term).evaluate(var);
    }

    @Override
    public double evaluate(double var) {
        return new Subtract(new Const(0), term).evaluate(var);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return new Subtract(new Const(0), term).evaluate(x, y, z);
    }

    @Override
    public String toString() {
        return "-" + "(" + term + ")";
    }
}
