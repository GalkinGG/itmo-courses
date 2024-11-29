package expression.exceptions;

public interface CharSource {
    boolean hasNext();
    char next();
    IllegalArgumentException error(String message);

    int getPos();
}
