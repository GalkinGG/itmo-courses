package game;

public interface Board {
    Position getPosition();
    Cell getCell();
    Result makeMove(Move move);

    Cell getTurn();

    void clearBoard();


}
