/**
 * В теле класса решения разрешено использовать только переменные делегированные в класс RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author : Galkin Gleb
 */
class Solution : MonotonicClock {
    private var c1 by RegularInt(0)
    private var c2 by RegularInt(0)
    private var c3 by RegularInt(0)

    private var r1 by RegularInt(0)
    private var r2 by RegularInt(0)
    private var r3 by RegularInt(0)

    override fun write(time: Time) {
        r1 = time.d1
        r2 = time.d2
        r3 = time.d3

        c3 = r3
        c2 = r2
        c1 = r1
    }

    override fun read(): Time {
        val c_1 = c1
        val c_2 = c2
        val c_3 = c3

        val r_3 = r3
        val r_2 = r2
        val r_1 = r1

        if (Time(c_1, c_2, c_3).compareTo(Time(r_1, r_2, r_3)) == 0) {
            return Time(c_1, c_2, c_3)
        }
        if (c_1 != r_1) {
            return Time(r_1, 0, 0)
        }
        if (c_2 != r_2) {
            return Time(r_1, r_2, 0)
        }
        return Time(r_1, r_2, r_3)
    }
}