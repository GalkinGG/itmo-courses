package search;

public class BinarySearchShift {

    //Pred: a.length > 0 && (a[] получен циклическим сдвигом
    //массива, отсортированного по убыванию, на k) &&
    // -1 <= l < r <= a.length

    //Post: l <= R <= r && (Для всех i на [l, R) < a[a.length-1]) &&
    //(Для всех i на [R, r) > a[a.length-1])
    public static int recursiveCall(int[] a, int l, int r) {
        //Pred
        if (r - l > 1) {
            //Pred && r - l > 1
            //l < (l + r) / 2       &&     (l+r) / 2 < r
            //2l < l + r; r > l+1   &&     l+r < 2r; l < r - 1
            //2l < 2l + 1 - верно   &&     2r - 1 < 2r - верно
            int m = (l + r) / 2;
            //Pred && l < m < r
            if (a[m] < a[a.length-1]) {
                //Pred && l < m < r && a[m] < a[a.length-1]
                // r - m < r - l
                return recursiveCall(a, m, r);
                //l' <= R <= r' && (Для всех i на [l, R) < a[a.length-1]) &&
                //(Для всех i на [R, r) > a[a.length-1])
            } else {
                //Pred && l < m < r && a[m] > a[a.length-1]
                return recursiveCall(a, l, m);
                //l' <= R <= r' && (Для всех i на [l, R) < a[a.length-1]) &&
                //(Для всех i на [R, r) > a[a.length-1])
            }
        }
        //r - l <= 1
        return r;
        //(Для всех i из [0, l']: a[i] < a[a.length-1]) &&
        //(Для всех i из [r', a.length): a[i] > a[a.length-1])
        //-> a[l'] - min на массиве
        // -> r' == k, т.к r - l <= 1
    }

    //Pred: a.length > 0 && (a[] получен циклическим сдвигом
    //массива, отсортированного по убыванию, на k)

    //Post: R - циклический сдвиг массива, R == k
    public static int recursiveBinarySearch(int[] a) {
        //Pred
        return recursiveCall(a,-1, a.length);
        //(Для всех i из [0, l']: a[i] < a[a.length-1]) &&
        //(Для всех i из [r', a.length): a[i] > a[a.length-1])
        //-> a[l'] - min на массиве
        // -> r' == k, т.к r - l <= 1
    }


    //Pred: a.length > 0 && (a[] получен циклическим сдвигом
    //массива на k, отсортированного по убыванию)

    //Post: R - циклический сдвиг массива, R == k
    public static int iterativeBinarySearch(int[] a) {
        //Pred

        //true
        int l = -1;
        int r = a.length;
        int m;
        // l = -1 && r = a.length && m = 0

        //I: Pred && -1 <= l' < r' <= a.length &&
        //l' <= m' <= r' && (Для всех i из [0, l']: a[i] < a[a.length-1]) &&
        //(Для всех i из [r', a.length): a[i] > a[a.length-1])
        while (l < r - 1) {
            //I && l' < r' - 1

            //I && l' < r' - 1
            m = (l + r)/2;
            //I && l' < r' - 1 && l' < m' < r'(док-во в recursive)

            //I && l' < r' - 1 && l' < m' < r'
            //-> 0 <= m' < a.length (из условий I для r' и l')
            if (a[m] < a[a.length-1]) {
                //I && l' < r' - 1 && l' < m' < r' && a[m'] < a[a.length-1]
                l = m;
                //I && l' < r' - 1 && l' = m' &&
                //(Для всех i из [0, l']: a[i] < a[a.length-1])
            } else {
                //I && l' < r' - 1 && l' < m' < r' && a[m'] < a[a.length-1]
                r = m;
                //I && l' < r' - 1 && r' = m' &&
                //(Для всех i из [r', a.length): a[i] > a[a.length-1])
            }
            //I && (Для всех i из [0, l']: a[i] < a[a.length-1]) &&
            //(Для всех i из [r', a.length): a[i] > a[a.length-1])
        }
        //I && r - l <= 1 &&
        //(Для всех i из [0, l']: a[i] < a[a.length-1]) &&
        //(Для всех i из [r', a.length): a[i] > a[a.length-1])
        //-> a[l'] - min на массиве
        // -> r' == k, т.к r - l <= 1
        return r;
    }

    //Pred: args.length > 0 &&
    //(Для всех i из [0, args.length): args[i] in (int)) &&
    //(args получен из массива, отсортированного по убыванию,
    //циклическим сдвигом на k)

    //Post: R - циклический сдвиг массива, R == k
    public static void main(String[] args) {
        int[] array = new int[args.length];
        long sum = 0;
        for (int i = 0; i < args.length; i++) {
            int num = Integer.parseInt(args[i]);
            array[i] = num;
            sum += num;
        }
        int res;
        if (sum % 2 == 0) {
            res = recursiveBinarySearch(array);
        } else {
            res = iterativeBinarySearch(array);
        }
        System.out.println(res);
    }
}
