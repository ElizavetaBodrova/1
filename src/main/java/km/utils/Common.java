package km.utils;

import static km.utils.TaskDefinition.L;
import static km.utils.TaskDefinition.V0;

public class Common {
    //константы для перевода в атомные единицы Хартри
    // 1 a.u. of length = 0.5292 A
    public static double clength = 0.5292;
    // 1 a.u. of energy = 27.211 eV
    public static double cenergy = 27.212;

    //количество точек
    public static int N = 100001;

    /**
     * Конвертация в атомные единицы Хартри
     */
    public static void сonvertToHartri() {
        L = L / clength;
        V0 = V0 / cenergy;
    }
}
