package Contest;

import java.util.Scanner;

public class BadTreap {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int count = 0;
        for (int i = -710 * 25000; i < 710 * 25000; i += 710) {
            if (count < n) {
                System.out.println(i);
                count++;
            }
        }
    }
}
