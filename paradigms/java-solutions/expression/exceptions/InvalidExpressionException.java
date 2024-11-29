package expression.exceptions;

public class InvalidExpressionException extends ParseException{
    public InvalidExpressionException(String message, int pos) {
        super("Unexpected expression \"" + message + "\"" + " Position: " + pos);
    }
}
