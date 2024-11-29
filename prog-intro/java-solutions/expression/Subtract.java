package expression;

public class Subtract extends BinOperations {

    public Subtract(AllExpressions firstOperator, AllExpressions secondOperation) {
        super(firstOperator, secondOperation, "-");
    }

    @Override
    protected int calc(int firstOperand, int secondOperand) {
        return firstOperand - secondOperand;
    }

    @Override
    protected double calc(double firstOperand, double secondOperand) {
        return firstOperand - secondOperand;
    }
}
