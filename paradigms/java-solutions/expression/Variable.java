package expression;

import java.util.InputMismatchException;
import java.util.Objects;

public class Variable implements AllExpressions {

    private final String variable;

    public Variable(String variable) {
        this.variable = variable;
    }

    @Override
    public int evaluate(int var) {
        if (variable.equals("x")) {
            return var;
        }
        throw new InputMismatchException("The variable should be called x");
    }

    @Override
    public double evaluate(double var) {
        return var;
    }

    @Override
    public int evaluate(int x, int y, int z) {
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

    @Override
    public String toString() {
        return variable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable exp) {
            return this.variable.equals(exp.variable);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable);
    }
}
