package expression.generic.calculator;

public interface Calculator<T> {

    T add(T firstOperand, T secondOperand);

    T subtract(T firstOperand, T secondOperand);

    T multiply(T firstOperand, T secondOperand);

    T divide(T firstOperand, T secondOperand);

    T negation(T firstOperand);

    T parseValue(String s);

    T getFromInt(int value);

}
