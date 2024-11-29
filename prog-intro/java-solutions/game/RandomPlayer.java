package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random;
    int m;
    int n;
    public RandomPlayer(final Random random) {
        this.random = random;
    }

    public RandomPlayer(int m, int n) {

        this(new Random());
        this.m = m;
        this.n = n;
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            int r = random.nextInt(1,m+1);
            int c = random.nextInt(1,n+1);
            final Move move = new Move(r, c, cell);
            if (position.isValid(move)) {
                return move;
            }
        }
    }
}
