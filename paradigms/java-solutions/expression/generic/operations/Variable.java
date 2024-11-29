package expression.generic.operations;

import expression.generic.*;
import expression.generic.calculator.Calculator;

import java.util.InputMismatchException;

public class Variable<T> implements GenericExpression<T> {

    private final String variable;

    public Variable(String variable) {
        this.variable = variable;
    }

    @Override
    public T evaluate(T x, Calculator<T> calculator) {
        if (variable.equals("x")) {
            return x;
        }
        throw new InputMismatchException("The variable should be called x");
    }

    @Override
    public T evaluate(T x, T y, T z, Calculator<T> calculator) {
        switch (variable) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
        }
        throw new InputMismatchException("Unknown variable. Available variables: x, y, z");
    }
}
