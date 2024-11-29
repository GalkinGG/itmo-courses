import java.util.Scanner;

public class AccurateMovement {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int ans;
        double a = sc.nextInt();
        double b = sc.nextInt();
        double n = sc.nextInt();
        ans = (int) (2 * Math.ceil((n - b) / (b - a)) + 1);
        System.out.println(ans);
    }
}
