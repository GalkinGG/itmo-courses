package search;

public class BinarySearch {

    //Pred: a.length > 0 && : -1 <= l < r <= a.length &&
    //(Существует такой i из [0, a.length), что a[i] <= k) &&
    //(Для всех i на [1, a.length): a[i] <= a[i-1])

    //Post: l <= R <= r && (Для всех i на [l, R) > k) &&
    //(Для всех i на [R, r) <= k)
    public static int recursiveCall(int[] a, int k, int l, int r) {
        //Pred
        if (r - l > 1) {
            //Pred && r - l > 1
            //l < (l + r) / 2       &&     (l+r) / 2 < r
            //2l < l + r; r > l+1   &&     l+r < 2r; l < r - 1
            //2l < 2l + 1 - верно   &&     2r - 1 < 2r - верно
            int m = (l + r) / 2;
            //Pred && l < m < r
            if (a[m] > k) {
                //Pred && l < m < r && a[m] > k
                return recursiveCall(a, k, m, r);
                //l' <= R <= r' && (Для всех i на [l', R) > k) &&
                //(Для всех i на [R, r'] <= k)
            } else {
                //Pred && l < m < r && a[m] <= k
                return recursiveCall(a, k, l, m);
                //l' <= R <= r' && (Для всех i на [l', R) > k) &&
                //(Для всех i на [R, r'] <= k)
            }
        }
        //r - l <= 1
        return r;
        //(Для всех i из [0, r): a[i] > k) &&
        //(Для всех i из [r, a.length): a[i] <= k)

    }

    //Pred: a.length && (Существует такой i из [0, a.length), что a[i] <= k) &&
    //(Для всех i на [1, a.length): a[i] <= a[i-1])

    //Post: (0 <= R < a.length) && (a[R] <= k) && (R = 0 || a[R-1] > k)
    public static int recursiveBinarySearch(int[] a, int k) {
        //Pred
        return recursiveCall(a, k, -1, a.length);
        //(Для всех i из [0, r): a[i] > k) &&
        //(Для всех i из [r, a.length): a[i] <= k)
    }


    //Pred: a.length > 0 && (Для всех i на [1, a.length): a[i] <= a[i-1]) &&
    //(Существует такой i из [0, a.length), что a[i] <= k)

    //Post: (0 <= R < a.length) && (a[R] <= k) && (R = 0 || a[R-1] > k)
    public static int iterativeBinarySearch(int[] a, int k) {
        //Pred

        //true
        int l = -1;
        int r = a.length;
        int m;
        // l = -1 && r = a.length && m = 0


        //I: Pred && -1 <= l' < r' <= a.length &&
        //l' <= m' <= r' && (Для всех i из [0, l']: a[i] > k) &&
        //(Для всех i из [r', a.length): a[i] <= k)
        while (l < r - 1) {
            //I && l' < r' - 1

            //I && l' < r' - 1
            m = (l + r)/2;
            //I && l' < r' - 1 && l' < m' < r'

            //I && l' < r' - 1 && l' < m' < r'
            //-> 0 <= m' < a.length (из условий I для r' и l')
            if (a[m] > k) {
                //I && l' < r' - 1 && l' < m' < r' && a[m'] > k
                l = m;
                //I && l' < r' - 1 && l' = m' &&
                //(Для всех i из [0, l']: a[i] > k)
            } else {
                //I && l' < r' - 1 && l' < m' < r' && a[m'] <= k
                r = m;
                //I && l' < r' - 1 && r' = m' &&
                //(Для всех i из [r', a.length): a[i] <= k)
            }
            //I && (Для всех i из [0, l']: a[i] > k) &&
            //(Для всех i из [r', a.length): a[i] <= k)
        }
        //I && r - l <= 1 &&
        //(Для всех i из [0, l']: a[i] > k) &&
        //(Для всех i из [r', a.length): a[i] <= k)
        //-> a[r'] <= k && a[l'] > k && r - 1 <= l
        //-> a[r'] <= k && (r' == 0 || a[r'-1] > k)
        return r;
    }


    //Pred: args.length > 0 && (Для всех i из [0, args.length): args[i] in (int)) &&
    //(Для всех i из [2, args.length): (int)args[i] < (int)args[i-1])
    //Post: (args.length == 1 && R == 0) || (args.length > 1 &&
    //&& (0 <= R < a.length) && (a[R] <= k) && (R = 0 || a[R-1] > k))
    public static void main(String[] args) {
        int number = Integer.parseInt(args[0]);
        if (args.length == 1) {
            System.out.println(0);
            return;
        }
        int[] array = new int[args.length - 1];
        long sum = 0;
        for (int i = 1; i < args.length; i++) {
            int num =  Integer.parseInt(args[i]);
            array[i-1] = num;
            sum += num;
        }
        int res;
        if (sum % 2 == 0) {
            res = recursiveBinarySearch(array, number);
        } else {
            res = iterativeBinarySearch(array, number);
        }
        System.out.println(res);
    }
}
