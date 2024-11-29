package expression.parser;

import expression.*;

public final class ExpressionParser implements TripleParser {

    public TripleExpression parse(final String source) {
        return parse(new StringSource(source));
    }

    public TripleExpression parse(final CharSource source) {
        return new ExpressionParser.Parser(source).parse();
    }
    private static class Parser extends BaseParser {

        protected Parser(CharSource source) {
            super(source);
        }

        private boolean isWhitespacePosition() {
            char ch = takeCurrent();
            return Character.isWhitespace(ch) || Character.isSpaceChar(ch);
        }

        private void skipWhitespace() {
            while (isWhitespacePosition()) {
                take();
            }
        }

        private void takeDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(take());
            }
        }

        private void takeInteger(final StringBuilder sb) {
            if (take('-')) {
                sb.append('-');
            }
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                takeDigits(sb);
            }
        }

        private WrapRes getOperand() {
            skipWhitespace();
            StringBuilder op = new StringBuilder();
            AllExpressions res;
            takeInteger(op);
            skipWhitespace();
            if (op.length() > 0) {
                int num = Integer.parseInt(op.toString());
                res = new Const(num);
            }
            else {
                if (test('x') || test('y') || test('z')) {
                    res = new Variable(Character.toString(take()));
                } else {
                    throw error("Unknown operand");
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
                if (take('s')) {
                    expect("et");
                    res = new Set(current.result, addSubtract().result);
                    current.result = res;
                } else if (take('c')) {
                    expect("lear");
                    res = new Clear(current.result, addSubtract().result);
                    current.result = res;
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
                        res = new Add(res, secondOperand);
                    } else {
                        res = new Subtract(res, secondOperand);
                    }
                    current.result = res;
                } else {
                    break;
                }
            }
            skipWhitespace();
            return new WrapRes(res);
        }

        private WrapRes multiplyDivide() {
            skipWhitespace();
            WrapRes current = negation();
            AllExpressions res = current.result;
            skipWhitespace();
            while (!eof()) {
                if (test('*') || test('/')) {
                    char operation = take();
                    skipWhitespace();
                    AllExpressions secondOperand = negation().result;
                    skipWhitespace();

                    if (operation == '*') {
                        res = new Multiply(res, secondOperand);
                    } else {
                        res = new Divide(res, secondOperand);
                    }
                    current.result = res;
                } else {
                    break;
                }
            }
            skipWhitespace();
            return new WrapRes(res);
        }

        private WrapRes negation() {
            skipWhitespace();
            boolean unaryMinus = test('-');
            if (unaryMinus) {
                take();
            }
            boolean isNegation = unaryMinus && (isWhitespacePosition() ||
                    test('(') ||
                    Character.isAlphabetic(takeCurrent()) ||
                    test('-'));
            skipWhitespace();
            if (isNegation) {
                return new WrapRes(new Negation(negation().result));
            } else {
                if (unaryMinus) {
                    putBack('-');
                }
                return bracket();
            }
        }

        private WrapRes bracket() {
            if (test('(')) {
                take();
                skipWhitespace();
                WrapRes res = setClear();
                if (!test(')')) {
                    throw error("Except )");
                }
                take();
                return res;
            }
            skipWhitespace();
            return getOperand();
        }

        public AllExpressions parse() {
            WrapRes result = setClear();
            return result.result;
        }
    }

}
