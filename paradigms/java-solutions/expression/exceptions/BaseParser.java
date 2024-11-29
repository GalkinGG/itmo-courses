package expression.exceptions;
public class BaseParser {
    private static final char END = '\0';
    private final CharSource source;
    private char ch = 0xffff;
    private boolean symbolPuttedBack = false;
    private char puttedBackChar = 0xffff;

    private char previousSymbol = 0xffff;

    protected BaseParser(final CharSource source) {
        this.source = source;
        take();
    }

    protected char take() {
        if (symbolPuttedBack) {
            symbolPuttedBack = false;
            previousSymbol = puttedBackChar;
            return puttedBackChar;
        }
        final char result = ch;
        previousSymbol = result;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected char getCurrent() {
        return ch;
    }

    protected int getPos() {
        return source.getPos();
    }

    protected char getPreviousSymbol() {
        return previousSymbol;
    }

    protected void putBack(char ch) {
        symbolPuttedBack = true;
        puttedBackChar = ch;
    }

    protected boolean test(final char expected) {
        return !symbolPuttedBack && ch == expected;
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean expect(final char expected) {
        return take(expected);
    }

    protected boolean expect(final String value) {
        for (final char c : value.toCharArray()) {
            if (!expect(c)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isWhitespacePosition() {
        char ch = getCurrent();
        return Character.isWhitespace(ch) || Character.isSpaceChar(ch);
    }

    protected void skipWhitespace() {
        while (isWhitespacePosition()) {
            take();
        }
    }

    protected void takeDigits(final StringBuilder sb) {
        while (between('0', '9')) {
            sb.append(take());
        }
    }

    protected void takeInteger(final StringBuilder sb) {
        if (take('-')) {
            sb.append('-');
        }
        if (take('0')) {
            sb.append('0');
        } else if (between('1', '9')) {
            takeDigits(sb);
        }
    }

    protected void takeNumber(final StringBuilder sb) {
        if (take(',')) {
            sb.append(',');
        }
        if (take('-')) {
            sb.append('-');
        }
        if (take('0')) {
            sb.append('0');
        } else if (between('1', '9')) {
            takeDigits(sb);
        }
    }

    protected boolean eof() {
        return take(END);
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }
}
