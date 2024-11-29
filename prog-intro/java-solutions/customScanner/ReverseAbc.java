import java.io.IOException;
import java.lang.System;
import java.util.Arrays;

public class ReverseAbc {

    public static void main(String[] args) {
        int currentLine = 0;
        String[][] input = new String[1024][];
        try {
            CustomScanner in = new CustomScanner(System.in);
            try {
                while (in.hasNextLine()) {
                    if (currentLine >= input.length) {
                        input = Arrays.copyOf(input, (input.length) * 2);
                    } try {
                        CustomScanner numbers = new CustomScanner(in.nextLine());
                        try {
                            int currentNumber = 0;
                            String[] ints = new String[1024];
                            while (numbers.hasNextWord()) {
                                if (currentNumber >= ints.length) {
                                    ints = Arrays.copyOf(ints, (ints.length) * 2);
                                }
                                ints[currentNumber] = numbers.nextWord();
                                currentNumber++;
                            }
                            ints = Arrays.copyOf(ints, currentNumber);
                            input[currentLine] = ints;
                            currentLine++;
                        } finally {
                            numbers.close();
                        } 
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        for (int i = currentLine - 1; i >= 0; i--) {
            for (int j = input[i].length - 1; j >= 0; j--) {
                System.out.print(input[i][j] + " ");
            }
            System.out.println();
        }
    }
}