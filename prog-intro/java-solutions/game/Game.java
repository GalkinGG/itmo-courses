package game;

public class Game {
    private final Player player1, player2;

    public Game(final Player player1, final Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public int play(Board board) {
        while (true) {
            while (true) {
                final int result1 = move(board, player1, 1);
                if (result1 == -2) {
                    continue;
                }
                if (result1 != -1) {
                    return result1;
                }
                break;
            }
            while (true) {
                final int result2 = move(board, player2, 2);
                if (result2 == -2) {
                    continue;
                }
                if (result2 != -1) {
                    return result2;
                }
                break;
            }
        }
    }

    private int move(final Board board, final Player player, final int no) {
        try {
            final Move move = player.move(board.getPosition(), board.getCell());
            final Result result = board.makeMove(move);
            if (result == Result.WIN) {
                return no;
            } else if (result == Result.LOSE) {
                return 3 - no;
            } else if (result == Result.DRAW) {
                System.out.println("DRAW");
                return 0;
            } else if (result == Result.EXTRAMOVE){
                return -2;
            } else {
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Player " + board.getTurn() + " lose. Player error");
            board.clearBoard();
            return 3 - no;
        }
    }
}
