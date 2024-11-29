import java.util.HashMap;
import java.util.Scanner;

public class ManagingDifficulties {
    public static void main(String args[]) {
        HashMap<Integer, Integer> diff = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        int cases = Integer.parseInt(sc.nextLine());
        int current_case = 1;
        int n;
        int count;
        while (current_case <= cases) {
            diff = new HashMap<>();
            n = Integer.parseInt(sc.nextLine());
            String[] s = new String[n];
            s = sc.nextLine().split(" ");
            int[] a = new int[n];
            for (int c = 0; c < n; c++) {
                a[c] = Integer.parseInt(s[c]);
            }
            count = 0;
            for (int j = n - 1; j > 0; j--) {
                for (int i = 0; i < j; i++) {
                    count += diff.getOrDefault(2 * a[j] - a[i], 0);
                }
                diff.put(a[j], diff.getOrDefault(a[j], 0) + 1);
            }
            System.out.println(count);
            current_case++;
        }
    }
}
