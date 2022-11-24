package km.utils;

/**
 * Условие задачи(вариант 10)
 */
public class TaskDefenition {
    //L,V0,U0 - из условия задачи, W устанавливается для красоты графиков
    public static double L = 4.0;
    public static double V0 = 20.0;
    public static double U0 = 0.0;
    public static double W = 4.0;

    /**
     * Тета-функция Хевисайда
     *
     * @param x
     */
    public static double Hevisaid(double x) {
        return x >= 0 ? 1 : 0;
    }

    /**
     * Потенциальная функция
     *
     * @param x argument
     */
    public static double U(double x) {
        return Math.abs(x) < L ? V0 * Hevisaid(x) : W;
    }
}
