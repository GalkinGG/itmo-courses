package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CustomScanner implements AutoCloseable {

    private final static int bufferSize = 1024;
    private char[] buffer = new char[bufferSize];
    private String result = "";
    private String resultLine = "";
    private int intResult;
    private String wordResult = "";
    private final Reader input;
    private int readPointer;
    private StringBuilder word = new StringBuilder();
    private StringBuilder line = new StringBuilder();
    private long charCount = 0;
    private char nextChar = 0;
    private final String separator = System.lineSeparator();
    private final char lastSepSymb = separator.charAt(separator.length()-1);

    public CustomScanner(InputStream in) throws IOException {
        input = new InputStreamReader(in,StandardCharsets.UTF_8);
        fillBuf();
    }

    public CustomScanner(String in) throws IOException {
        input = new StringReader(in);
        fillBuf();
    }

    private void fillBuf() throws IOException {
        readPointer = 0;
        charCount = input.read(buffer);
    }

    private boolean hasNextChar() throws IOException {
        if (readPointer == charCount) {
            fillBuf();
        }
        if (charCount == -1) {
            return false;
        }
        nextChar = buffer[readPointer++];
        return true;
    }

    public boolean hasNext() throws IOException {
        boolean wordStarted = false;
        while (hasNextChar()) {
            if (!Character.isWhitespace(nextChar)) {
                word.append(nextChar);
                wordStarted = true;
            } else if (wordStarted) {
                break;
            }
        }
        result = word.toString();
        word = new StringBuilder();
        return result.length() > 0;
    }

    public String next() throws IOException {
        return this.result;
    }

    public boolean hasNextInt() throws IOException {
        if (hasNext()) {
            boolean isInt;
            try {
                intResult = Integer.parseInt(next());
                isInt = true;
            } catch (NumberFormatException e) {
                isInt = false;
            }
            return isInt;
        } else {
            return false;
        }
    }

    public int nextInt() {
        return this.intResult;
    }

    public boolean hasNextLine() throws IOException {
        boolean isSeparator = false;
        while (hasNextChar()) {
            if (nextChar == lastSepSymb) {
                isSeparator = true;
                line.append(nextChar);
                break;
            } else {
                line.append(nextChar);
            }
        }
        if (charCount == -1) {
            if (line.isEmpty()) {
                return false;
            } else {
                resultLine = line.toString();
                line = new StringBuilder();
                return true;
            }
        }
        resultLine = line.toString();
        line = new StringBuilder();
        return isSeparator;
    }

    public String nextLine() {
        return this.resultLine;
    }

    public boolean hasNextWord() throws IOException {
        boolean wordStarted = false;
        while (hasNextChar()) {
            if ((!Character.isWhitespace(nextChar)) &&
                    (Character.isLetter(nextChar) || (nextChar == '\'') ||
                            (Character.getType(nextChar) == Character.DASH_PUNCTUATION))) {
                word.append(nextChar);
                wordStarted = true;
            } else if (wordStarted) {
                break;
            }
        }
        wordResult = word.toString();
        word = new StringBuilder();
        return wordResult.length() > 0;
    }

    public String nextWord() throws IOException {
        return this.wordResult;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

}