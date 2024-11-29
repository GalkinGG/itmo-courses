import java.util.Scanner;

public class JustTheLastDigit {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());
        char[][] temp = new char[n][n];
        int[][] a = new int[n][n];
        for (int c = 0; c < n; c++) {
            temp[c] = sc.nextLine().toCharArray();
        }
        for (int c = 0; c < n; c++) {
            for (int l = 0; l < n; l++) {
                a[c][l] = Character.getNumericValue(temp[c][l]);
            }
        }

        for (int k = 0; k < n; k++) {
            for (int j = k + 1; j < n; j++) {
                if (a[k][j] == 1) {
                    for (int i = j + 1; i < n; i++) {
                        a[k][i] = (a[k][i] - a[j][i] + 10) % 10;
                    }
                }
            }
        }

        for (int[] line : a) {
            for (int number : line) {
                System.out.print(number);
            }
            System.out.println();
        }
    }
}
