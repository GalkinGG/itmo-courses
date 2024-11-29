package expression;

import java.util.Objects;

public abstract class BinOperations implements AllExpressions {

    private final String operation;

    private final AllExpressions firstOperand;
    private final AllExpressions secondOperand;

    protected BinOperations(AllExpressions firstOperand, AllExpressions secondOperand, String operation) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operation = operation;
    }

    protected abstract int calc(int firstOperand, int secondOperand);

    protected abstract double calc(double firstOperand, double secondOperand);


    @Override
    public int evaluate(int var) {
        return calc(firstOperand.evaluate(var), secondOperand.evaluate(var));
    }

    @Override
    public double evaluate(double var) {
        return calc(firstOperand.evaluate(var), secondOperand.evaluate(var));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calc(firstOperand.evaluate(x, y, z), secondOperand.evaluate(x, y, z));
    }

    public String getOperation() {
        return operation;
    }

    public Expression getFirstOperand() {
        return firstOperand;
    }

    public Expression getSecondOperand() {
        return secondOperand;
    }

    @Override
    public String toString() {
        return "(" + firstOperand.toString() + " " + operation + " " + secondOperand.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()) {
            BinOperations exp = (BinOperations) obj;
            return this.getFirstOperand().equals(exp.getFirstOperand()) && this.getSecondOperand().equals(exp.getSecondOperand())
                    && this.operation.equals(exp.getOperation());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (Objects.hash(operation) * 17 + Objects.hash(firstOperand)) * 49 + Objects.hash(secondOperand) * 343;
    }
}
