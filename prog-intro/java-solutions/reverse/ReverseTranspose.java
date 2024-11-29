import java.util.Scanner;
import java.lang.System;
import java.util.Arrays;

public class ReverseTranspose {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int currentLine = 0;
        int maxLength = 0;
        int[][] input = new int[10][];
        while (in.hasNextLine()) {
            if (currentLine >= input.length) {
                input = Arrays.copyOf(input, (input.length) * 2);
            }
            Scanner numbers = new Scanner(in.nextLine());
            int currentNumber = 0;
            int[] ints = new int[10];
            while (numbers.hasNextInt()) {
                if (currentNumber >= ints.length) {
                    ints = Arrays.copyOf(ints, (ints.length) * 2);
                }
                ints[currentNumber] = numbers.nextInt();
                currentNumber++;
            }
            ints = Arrays.copyOf(ints, currentNumber);
            if (ints.length > maxLength) {
                maxLength = ints.length;
            }
            input[currentLine] = ints;
            currentLine++;
        }
        input = Arrays.copyOf(input, currentLine);
        int j = 0;
        while (j < maxLength) {
            int i = 0;
            while (i < input.length) {
                if (j < input[i].length) {
                    System.out.print(input[i][j] + " ");
                }
                i++;
            }
            System.out.println();
            j++;
        }
    }
}