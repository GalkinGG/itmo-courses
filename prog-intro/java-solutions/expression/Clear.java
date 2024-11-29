package expression;

public class Clear extends BinOperations {

    public Clear(AllExpressions firstOperator, AllExpressions secondOperation) {
        super(firstOperator, secondOperation, "clear");
    }
    @Override
    protected int calc(int firstOperand, int secondOperand) {
        return firstOperand & ~(1 << secondOperand);
    }

    @Override
    protected double calc(double firstOperand, double secondOperand) {
        return 0;
    }
}
