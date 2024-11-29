package expression.generic;

import expression.exceptions.*;
import expression.generic.operations.*;
import expression.generic.calculator.Calculator;

import java.util.Arrays;
import java.util.List;

public final class ExpressionParser<T> implements TripleParser<T> {


    public GenericExpression<T> parse(final String source, Calculator<T> calculator) {
        int bracketBalance = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '(') {
                bracketBalance++;
            } else if (source.charAt(i) == ')') {
                bracketBalance--;
            }
            if (bracketBalance < 0) {
                throw new BracketsCountException("extra ')' found");
            }
        }
        if (bracketBalance != 0) {
            throw new BracketsCountException("too many '(' found");
        }
        return parse(new StringSource(source), calculator);
    }

    public GenericExpression<T> parse(final CharSource source, Calculator<T> calculator) {
        return new ExpressionParser<T>.Parser(source, calculator).parse();
    }
    private class Parser extends BaseParser {

        private final Calculator<T> calculator;
        private final List<Character> allowedSymbols = Arrays.asList('x', 'y', 'z', '+', '-', '*', '/', '(', ')', ' ');

        protected Parser(CharSource source, Calculator<T> calculator) {
            super(source);
            this.calculator = calculator;
        }

        private WrapRes<T> getOperand() {
            skipWhitespace();
            StringBuilder op = new StringBuilder();
            GenericExpression<T> res;
            takeNumber(op);
            if (op.length() > 0 && !(op.length() == 1 && !Character.isDigit(op.charAt(0)))) {
                T num = calculator.parseValue(op.toString());
                res = new Const<>(num);
            }
            else {
                skipWhitespace();
                if (test('x') || test('y') || test('z')) {
                    res = new Variable<>(Character.toString(take()));
                } else {
                    if (isWhitespacePosition() || eof()) {
                        int pos = getPos();
                        throw new InvalidOperandException("No last operand" + " Position: " + (pos+1));
                    } else {
                        int pos = getPos();
                        String invalidPart = getInvalidPart();
                        if (invalidPart.equals("clear")) {
                            pos--;
                        }
                        throw new InvalidOperandException("Expected operand. Received: \"" + invalidPart + "\""
                                                          + " Position: " + pos);
                    }
                }
            }
            return new WrapRes<>(res);
        }

        private WrapRes<T> addSubtract() {
            skipWhitespace();
            WrapRes<T> current = multiplyDivide();
            GenericExpression<T> res = current.result;
            skipWhitespace();
            while (!eof()) {
                if (test('+') || test('-')) {

                    char operation = take();
                    skipWhitespace();

                    GenericExpression<T> secondOperand = multiplyDivide().result;
                    skipWhitespace();

                    if (operation == '+') {
                        res = new Add<>(res, secondOperand);
                    } else {
                        res = new Subtract<>(res, secondOperand);
                    }
                    current.result = res;
                } else {
                    break;
                }
            }
            return new WrapRes<>(res);
        }

        private WrapRes<T> multiplyDivide() {
            skipWhitespace();
            WrapRes<T> current = negationCount();
            GenericExpression<T> res = current.result;
            skipWhitespace();
            while (!eof()) {
                if (test('*') || test('/')) {
                    char operation = take();
                    skipWhitespace();
                    GenericExpression<T> secondOperand = negationCount().result;
                    skipWhitespace();

                    if (operation == '*') {
                        res = new Multiply<>(res, secondOperand);
                    } else {
                        res = new Divide<>(res, secondOperand);
                    }
                    current.result = res;
                } else {
                    break;
                }
            }
            return new WrapRes<>(res);
        }

        private WrapRes<T> negationCount() {
            skipWhitespace();
            boolean unaryMinus = test('-');
            if (unaryMinus) {
                take();
            }
            boolean isNegation = unaryMinus && (isWhitespacePosition() ||
                    test('(') ||
                    Character.isAlphabetic(getCurrent()) ||
                    test('-'));
            if (isNegation) {
                return new WrapRes<>(new Negation<>(negationCount().result));
            } else {
                if (unaryMinus) {
                    putBack('-');
                }
                return bracket();
            }
        }

        private WrapRes<T> bracket() {
            if (test('(')) {
                take();
                skipWhitespace();
                WrapRes<T> res = addSubtract();
                if (!test(')')) {
                    throw error("Expected ')'");
                }
                take();
                return res;
            }
            return getOperand();
        }

        private String getInvalidPart() {
            StringBuilder invalidPart = new StringBuilder();
            while (!isWhitespacePosition() && !eof()) {
                invalidPart.append(take());
            }
            return invalidPart.toString();
        }

        public GenericExpression<T> parse() {
            WrapRes<T> result = addSubtract();
            return result.result;
        }
    }

}
