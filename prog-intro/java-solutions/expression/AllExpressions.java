package expression;

public interface AllExpressions extends Expression, TripleExpression, DoubleExpression {
    @Override
    int evaluate(int var);

    @Override
    double evaluate(double var);

    @Override
    int evaluate(int x, int y, int z);
}
