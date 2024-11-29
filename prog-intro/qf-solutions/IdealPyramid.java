import java.util.Scanner;

public class IdealPyramid {

    public static void main(String[] args) {
        int xl, xr, yl, yr;
        xl = yl = Integer.MAX_VALUE;
        xr = yr = Integer.MIN_VALUE;
        Scanner in = new Scanner(System.in);
        int n;
        n = in.nextInt();
        for (int i = 0; i < n; ++i) {
            int x, y, h;
            x = in.nextInt();
            y = in.nextInt();
            h = in.nextInt();
            xl = Math.min(xl, x - h);
            xr = Math.max(xr, x + h);
            yl = Math.min(yl, y - h);
            yr = Math.max(yr, y + h);
        }
        int resX = (xl + xr) / 2;
        int resY = (yl + yr) / 2;
        int resH = (int) Math.ceil((double) (Math.max(xr - xl, yr - yl)) / 2);
        System.out.println(resX + " " + resY + " " + resH);
    }

}
