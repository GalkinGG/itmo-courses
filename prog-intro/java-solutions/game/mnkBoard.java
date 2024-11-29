package game;

import java.util.Arrays;
import java.util.Map;

public class mnkBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;

    private final int m;
    private final int n;
    private final int k;

    private int filled = 0;
    public mnkBoard(int m, int n, int k) {
        this.m = m;
        this.n = n;
        this.k = k;
        this.cells = new Cell[m+1][n+1];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    public void clearBoard() {
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public Cell getTurn() {
        return turn;
    }

    @Override
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow()][move.getColumn()] = move.getValue();

        System.out.println(turn + "'s" + " move: row = " + move.getRow() + " column = " + move.getColumn());

        int inDiag1 = 0;
        int inDiag2 = 0;
        int inRow = 0;
        int inColumn = 0;
        int leftRow = move.getRow() - k + 1;
        int leftCol = move.getColumn() - k + 1;
        int rightRow = move.getRow() - k + 1;
        int rightCol = move.getColumn() + k - 1;

        Result res = Result.UNKNOWN;
        Cell nextTurn = turn == Cell.X ? Cell.O : Cell.X;;

        for (int i = Math.max(move.getColumn() - k, 1); i < Math.min(move.getColumn() + k, n) + 1; i++) {
            if (cells[move.getRow()][i] == turn) {
                inRow++;
            } else {
                inRow = 0;
            }
            if (inRow == k) {
                filled = 0;
                System.out.println(turn + " won game");
                this.clearBoard();
                return Result.WIN;
            }
            if (move.getColumn() > 1 && move.getColumn() < m) {
                if (inRow >= 4 && ((cells[move.getRow()][move.getColumn() - 1] == turn)
                        || (cells[move.getRow()][move.getColumn() + 1] == turn))) {
                    res = Result.EXTRAMOVE;
                    nextTurn = turn;
                }
            }
        }

        for (int i = Math.max(move.getRow() - k, 1); i < Math.min(move.getRow() + k, m) + 1; i++) {
            if (cells[i][move.getColumn()] == turn) {
                inColumn++;
            } else {
                inColumn = 0;
            }
            if (inColumn == k) {
                filled = 0;
                System.out.println(turn + " won game");
                this.clearBoard();
                return Result.WIN;
            }
            if (move.getRow() > 1 && move.getRow() < n) {
                if (inColumn >= 4 && ((cells[move.getRow()-1][move.getColumn()] == turn)
                        || (cells[move.getRow()+1][move.getColumn()] == turn))) {
                    res = Result.EXTRAMOVE;
                    nextTurn = turn;
                }
            }
        }

        while (leftRow < move.getRow() + k) {
            if (leftRow > 0 && leftCol > 0 && leftRow < m+1 && leftCol < n + 1) {
                if (cells[leftRow][leftCol] == turn) {
                    inDiag1++;
                } else {
                    inDiag1 = 0;
                }
            }
            leftRow++;
            leftCol++;
            if (inDiag1 == k) {
                filled = 0;
                System.out.println(turn + " won game");
                this.clearBoard();
                return Result.WIN;
            }
            if (move.getRow() > 1 && move.getRow() < n && move.getColumn() > 1 && move.getColumn() < m) {
                if (inDiag1 >= 4 && ((cells[move.getRow()-1][move.getColumn()-1] == turn)
                        || (cells[move.getRow()+1][move.getColumn()+1] == turn))) {
                    res = Result.EXTRAMOVE;
                    nextTurn = turn;
                }
            }
        }

        while (rightRow < move.getRow() + k) {
            if (rightRow > 0 && rightCol > 0 && rightRow < m+1 && rightCol < n+1) {
                if (cells[rightRow][rightCol] == turn) {
                    inDiag2++;
                } else {
                    inDiag2 = 0;
                }
            }
            rightRow++;
            rightCol--;
            if (inDiag2 == k) {
                filled = 0;
                System.out.println(turn + " won game");
                this.clearBoard();
                return Result.WIN;
            }
            if (move.getRow() > 1 && move.getRow() < n && move.getColumn() > 1 && move.getColumn() < m) {
                if (inDiag2 >= 4 && ((cells[move.getRow()-1][move.getColumn()+1] == turn)
                        || (cells[move.getRow()+1][move.getColumn()-1] == turn))) {
                    res = Result.EXTRAMOVE;
                    nextTurn = turn;
                }
            }
        }
        filled++;
        if (filled == m * n) {
            filled = 0;
            this.clearBoard();
            return Result.DRAW;
        }
        turn = nextTurn;
        return res;
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 < move.getRow() && move.getRow() < m+1
                && 0 < move.getColumn() && move.getColumn() < n+1
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("   ");
        for (int l = 1; l < cells[0].length; l++) {
            sb.append(l).append("  ");
        }
        for (int r = 1; r < cells.length; r++) {
            sb.append("\n");
            sb.append(r).append("  ");
            for (int c = 1; c < cells[0].length; c++) {
                sb.append(SYMBOLS.get(cells[r][c])).append("  ");
            }
        }
        return sb.toString();
    }
}

