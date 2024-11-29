package expression.exceptions;

public class DivisionByZeroException extends CalculationException{
    public DivisionByZeroException() {
        super("division by zero");
    }
}
