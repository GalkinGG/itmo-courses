import java.util.Scanner;

public class HighLoadDatabase {

    private int[] tAnswers;
    private int[] querySums;
    private int maxQueryNum = Integer.MIN_VALUE;

    public static void main(String[] args) {
        new HighLoadDatabase().solve();
    }

    public void solve() {
        Scanner in = new Scanner(System.in);
        int n, q;
        n = in.nextInt();
        querySums = new int[n + 1];
        for (int i = 1; i < querySums.length; ++i) {
            int queryNum = in.nextInt();
            maxQueryNum = Math.max(maxQueryNum, queryNum);
            querySums[i] = querySums[i - 1] + queryNum;
        }
        tAnswers = new int[querySums[querySums.length - 1]];
        q = in.nextInt();

        for (int i = 0; i < q; ++i) {
            int t = in.nextInt();
            if (t < maxQueryNum) {
                System.out.println("Impossible");
            } else {
                int res = binarySearch(t, n);
                System.out.println(res);
            }
        }
    }
    private int binarySearch(int a, int n) {
        if (tAnswers[a] != 0) {
            return tAnswers[a];
        }
        int lst = 1;
        while (lst <= n) {
            tAnswers[a] ++;
            int l, r, res;
            l = lst;
            r = n;
            res = -1;
            while (l <= r) {
                int m = (l + r) / 2;
                if (querySums[m] - querySums[lst - 1] <= a) {
                    l = m + 1;
                    res = m;
                } else {
                    r = m - 1;
                }
            }
            lst = res + 1;
        }
        return tAnswers[a];
    }

}
