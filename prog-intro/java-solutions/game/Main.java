package game;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter rows, columns count, victory condition: k and games count. To make move, enter the row number then the column number");
        int m = 0;
        int n = 0;
        int k = 0;
        int rounds = 0;
        while (true) {
            try {
                String[] line = sc.nextLine().split(" ");
                if (line.length == 4) {
                    m = Integer.parseInt(line[0]);
                    n = Integer.parseInt(line[1]);
                    k = Integer.parseInt(line[2]);
                    rounds = Integer.parseInt(line[3]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter correct field");
                sc = new Scanner(System.in);
                continue;
            }
            if (m > 0 && n >0 && k > 0 && rounds > 0) {
                break;
            } else {
                System.out.println("Enter correct field");
            }
        }
        int playerOneWins = 0;
        int playerTwoWins = 0;
        int roundCounter = 1;
        Board currentBoard = new mnkBoard(m, n, k);
        Player playerOne = new HumanPlayer();
        Player playerTwo = new RandomPlayer(m, n);
        while (true) {
            Game game = new Game(playerTwo, playerOne);
            if (roundCounter % 2 == 1) {
                game = new Game(playerOne, playerTwo);
            }
            int result;
            result = game.play(currentBoard);
            if (result == 1) {
                if (roundCounter % 2 == 1) {
                    playerOneWins++;
                } else {
                    playerTwoWins++;
                }
                if (Math.max(playerOneWins, playerTwoWins) >= rounds) {
                    break;
                }
            }
            if (result == 2) {
                if (roundCounter % 2 == 0) {
                    playerOneWins++;
                } else {
                    playerTwoWins++;
                }
                if (Math.max(playerOneWins, playerTwoWins) >= rounds) {
                    break;
                }
            }
            roundCounter++;
        }
        if (playerOneWins > playerTwoWins) {
            System.out.println("PLAYER ONE WON");
        } else {
            System.out.println("PLAYER TWO WON");
        }
    }
}
