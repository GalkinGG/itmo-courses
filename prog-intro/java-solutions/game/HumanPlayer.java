package game;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            try {
                out.println("Position");
                out.println(position);
                out.println(cell + "'s move");
                out.println("Enter row and column");
                int row = -1;
                int column = -1;
                String[] line = in.nextLine().split(" ");
                if (line.length == 2) {
                    row = Integer.parseInt(line[0]);
                    column = Integer.parseInt(line[1]);
                }
                final Move move = new Move(row, column, cell);
                if (position.isValid(move)) {
                    return move;
                } else {
                    out.println("Move is invalid. Enter correct row and column");
                }
            } catch (NumberFormatException e) {
                out.println("Move is invalid. Enter correct row and column");
            }
        }
    }
}
