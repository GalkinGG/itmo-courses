package expression.parser;
public class BaseParser {
    private static final char END = '\0';
    private final CharSource source;
    private char ch = 0xffff;
    private boolean symbolPuttedBack = false;
    private char puttedBackChar = 0xffff;

    protected BaseParser(final CharSource source) {
        this.source = source;
        take();
    }

    protected char take() {
        if (symbolPuttedBack) {
            symbolPuttedBack = false;
            return puttedBackChar;
        }
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected char takeCurrent() {
        return ch;
    }

    protected void putBack(char ch) {
        symbolPuttedBack = true;
        puttedBackChar = ch;
    }

    protected boolean test(final char expected) {
        return ch == expected;
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
