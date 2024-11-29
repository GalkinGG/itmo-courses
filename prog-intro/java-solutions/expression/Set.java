package expression;

public class Set extends BinOperations {

    public Set(AllExpressions firstOperator, AllExpressions secondOperation) {
        super(firstOperator, secondOperation, "set");
    }
    @Override
    protected int calc(int firstOperand, int secondOperand) {
        return firstOperand | (1 << secondOperand);
    }

    @Override
    protected double calc(double firstOperand, double secondOperand) {
        return 0;
    }
}
