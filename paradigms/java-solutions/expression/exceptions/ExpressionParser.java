package expression.exceptions;

import expression.*;

import java.util.Arrays;
import java.util.List;

public final class ExpressionParser implements TripleParser {


    public TripleExpression parse(final String source) {
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
        return parse(new StringSource(source));
    }

    public TripleExpression parse(final CharSource source) {
        return new ExpressionParser.Parser(source).parse();
    }
    private static class Parser extends BaseParser {

        private final List<Character> allowedSymbols = Arrays.asList('x', 'y', 'z', '+', '-', '*', '/', '(', ')', ' ');

        protected Parser(CharSource source) {
            super(source);
        }

        private WrapRes getOperand() {
            skipWhitespace();
            StringBuilder op = new StringBuilder();
            AllExpressions res;
            takeInteger(op);
            if (op.length() > 0 && !(op.length() == 1 && !Character.isDigit(op.charAt(0)))) {
                try {
                    int num = Integer.parseInt(op.toString());
                    res = new Const(num);
                } catch (NumberFormatException e) {
                    int pos = getPos();
                    throw new OverflowException("number is too big: " + op + " Position: " + (pos-op.length()+1));
                }
            }
            else {
                skipWhitespace();
                if (test('x') || test('y') || test('z')) {
                    res = new Variable(Character.toString(take()));
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
            return new WrapRes(res);
        }

        private WrapRes setClear() {
            skipWhitespace();
            WrapRes current = addSubtract();
            AllExpressions res = current.result;
            skipWhitespace();
            while (!eof()) {
                if (test('s') && !Character.isDigit(getPreviousSymbol())) {
                    take();
                    if (expect("et")) {
                        res = new Set(current.result, addSubtract().result);
                        current.result = res;
                    } else {
                        int pos = getPos();
                        throw new InvalidExpressionException(getInvalidPart(), pos);
                    }
                } else if (test('c') && !Character.isDigit(getPreviousSymbol())) {
                    take();
                    if (expect("lear")) {
                        res = new Clear(current.result, addSubtract().result);
                        current.result = res;
                    } else {
                        int pos = getPos();
                        throw new InvalidExpressionException(getInvalidPart(), pos);
                    }
                } else if ((test('s') || test('c')) && Character.isDigit(getPreviousSymbol())) {
                    throw new InvalidOperationException("Missed whitespace before " + (test('s') ? "set" : "clear"));
                } else if (!allowedSymbols.contains(getCurrent())) {
                    int pos = getPos();
                    throw new InvalidExpressionException(getInvalidPart(), pos);
                } else {
                    break;
                }
            }
            skipWhitespace();
            return new WrapRes(res);
        }
        private WrapRes addSubtract() {
            skipWhitespace();
            WrapRes current = multiplyDivide();
            AllExpressions res = current.result;
            skipWhitespace();
            while (!eof()) {
                if (test('+') || test('-')) {

                    char operation = take();
                    skipWhitespace();

                    AllExpressions secondOperand = multiplyDivide().result;
                    skipWhitespace();

                    if (operation == '+') {
                        res = new CheckedAdd(res, secondOperand);
                    } else {
                        res = new CheckedSubtract(res, secondOperand);
                    }
                    current.result = res;
                } else {
                    break;
                }
            }
            return new WrapRes(res);
        }

        private WrapRes multiplyDivide() {
            skipWhitespace();
            WrapRes current = negationCount();
            AllExpressions res = current.result;
            skipWhitespace();
            while (!eof()) {
                if (test('*') || test('/')) {
                    char operation = take();
                    skipWhitespace();
                    AllExpressions secondOperand = negationCount().result;
                    skipWhitespace();

                    if (operation == '*') {
                        res = new CheckedMultiply(res, secondOperand);
                    } else {
                        res = new CheckedDivide(res, secondOperand);
                    }
                    current.result = res;
                } else {
                    break;
                }
            }
            return new WrapRes(res);
        }

        private WrapRes negationCount() {
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
                return new WrapRes(new CheckedNegate(negationCount().result));
            } else {
                if (unaryMinus) {
                    putBack('-');
                }
                if (test('c')) {
                    take();
                    if (test('o')) {
                        take();
                        if (expect("unt")) {
                            if (isWhitespacePosition() || getCurrent() == '(' || getCurrent() == '\0') {
                                return new WrapRes(new Count(negationCount().result));
                            } else {
                                int pos = getPos();
                                throw new InvalidOperationException("Missed whitespace after count." + " Position: " + pos);
                            }
                        } else {
                            int pos = getPos();
                            throw new InvalidExpressionException(getInvalidPart(), pos);
                        }
                    } else {
                        putBack('c');
                        return bracket();
                    }
                } else {
                    return bracket();
                }
            }
        }

        private WrapRes bracket() {
            if (test('(')) {
                take();
                skipWhitespace();
                WrapRes res = setClear();
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
            /*if (!(Character.isWhitespace(getPreviousSymbol()) || Character.isSpaceChar(getPreviousSymbol()) ||
                    getPreviousSymbol() == 0xffff)) {
                invalidPart.append(getPreviousSymbol());
            }*/
            while (!isWhitespacePosition() && !eof()) {
                invalidPart.append(take());
            }
            return invalidPart.toString();
        }

        public AllExpressions parse() {
            WrapRes result = setClear();
            return result.result;
        }
    }

}
