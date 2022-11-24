package km.methods.target;

import km.model.WaveFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static km.Main.writeResultFile;
import static km.methods.target.TargetUtils.E_LEFT;
import static km.methods.target.TargetUtils.E_RIGHT;
import static km.methods.target.TargetUtils.R;
import static km.methods.target.TargetUtils.ne;
import static km.methods.target.TargetUtils.tol;
import static km.utils.Chart.printChart;
import static km.utils.Chart.printChartPsiFi;
import static km.utils.Chart.printU;
import static km.utils.Common.N;
import static km.utils.Common.сonvertToHartri;
import static km.utils.TaskDefenition.L;
import static km.utils.TaskDefenition.U;

/**
 * Метод пристрелки
 */
public class TargetMethod {
    private static boolean print = false;

    public static void findWaveFunctionByTargetMethod() throws IOException {
        //перевод в атомные единицы Хартри
        сonvertToHartri();
        printU();
        System.out.println("E_LEFT=" + E_LEFT + " E_RIGHT=" + E_RIGHT);
        //шаг энергий
        double he = (E_RIGHT - E_LEFT) / (ne - 1);
        List<WaveFunction> solves = new ArrayList<>();
        Double[] Psi = new Double[N];
        Double[] Fi = new Double[N];
        //разность производных в узле сшивке для значения E_LEFT
        double e = E_LEFT;
        double af = f_fun(e);
        //поиск простых корней f(E) на отрезке [E_LEFT,E_RIGHT]
        for (int i = 1; i < ne; i++) {
            double e_next = E_LEFT + i * he;
            double af_next = f_fun(e_next);
            if (af * af_next < 0.0) {
                e = E_LEFT + (i - 1) * he;
                double eval = methodBisection(e, e_next, tol);
                //если найден корень методом бисекции
                if (eval != 0.0) {
                    System.out.println("eval=" + eval);
                    print = true;
                    f_fun(e);
                    double f = f_fun(eval);
                    System.out.println("f(e)=" + f);
                    f_fun(e_next);
                    print = false;
                    WaveFunction wave = getWaveFunction(Psi, Fi, eval);
                    wave.fE=f;
                    //квантово-механическая нормировка
                    norm(wave);
                    //добавляем найденную функцию к решениям
                    solves.add(wave);
                }
            }
            af=af_next;
        }
        //формирование файла отчета и графики
        String nameDir = "E_LEFT=" + E_LEFT + " E_RIGHT=" + E_RIGHT;
        File theDir = new File("src/main/resources/" + nameDir);
        if (!theDir.exists()) {
            theDir.mkdirs();
        } else {
            theDir = new File("src/main/resources/" + nameDir);
            theDir.mkdirs();
        }
        writeResultFile(theDir.getPath(), solves);
        for (int i = 0; i < solves.size(); i++) {
            printChart(theDir.getPath(), "test" + i, solves.get(i));
        }
    }

    /**
     * Функция q(e,x)
     */
    public static double q(double e, double x) {
        return 2.0 * (e - U(x));
    }

    /**
     * Численное вычисление производной
     */
    public static double deriv(Double[] Y, double h) {
        return (Y[R - 2] - Y[R + 2] + 8.0 * (Y[R + 1] - Y[R - 1])) / (12 * h);
    }

    /**
     * Вычисление разности производных в узле сшивки
     */
    public static double f_fun(double e) throws IOException {
        //решение справа налево
        Double[] Psi = new Double[N];
        //решение слева направо
        Double[] Fi = new Double[N];
        //шаг
        double h = 2 * L / (N - 1);
        //численное вычисление решений
        getWaveFunction(Psi, Fi, e);
        return deriv(Psi, h) - deriv(Fi, h);
    }

    /**
     * Поиск волновой функции
     *
     * @param Psi волновая функция, решение "вперед"
     * @param Fi  волновая функция, решение "назад"
     * @param e   энергия
     * @return волновая функция, Psi, Fi
     * @throws IOException
     */
    public static WaveFunction getWaveFunction(Double[] Psi, Double[] Fi, double e) throws IOException {
        //шаг
        double h = 2 * L / (N - 1);
        //константа для метода Нумерова
        double c = h * h / 12;
        //правая часть
        Double[] F = new Double[N];
        //Вычисление правой части
        for (int i = 0; i < N; i++) {
            F[i] = c * q(e, -L + i * h);
        }
        //Начальные значения по методу Нумерова
        Psi[0] = 0.0;
        Fi[N - 1] = 0.0;
        Psi[1] = 1e-3;
        Fi[N - 2] = 1e-3;
        //вперед
        for (int i = 1; i < N - 1; i++) {
            double p1 = 2.0 * (1.0 - 5.0 * F[i]) * Psi[i];
            double p2 = (1.0 + F[i - 1]) * Psi[i - 1];
            Psi[i + 1] = (p1 - p2) / (1.0 + F[i + 1]);
        }
        //назад
        for (int i = N - 2; i >= 1; i--) {
            double p1 = 2.0 * (1.0 - 5.0 * F[i]) * Fi[i];
            double p2 = (1.0 + F[i + 1]) * Fi[i + 1];
            Fi[i - 1] = (p1 - p2) / (1.0 + F[i - 1]);
        }
        //математическая нормировка
        double max = Arrays.stream(Psi).peek(x -> Math.abs(x)).max(Double::compareTo).get();
        for (int i = 0; i < N; i++) {
            Psi[i] /= max;
        }
        double coef = Psi[R] / Fi[R];
        for (int i = 0; i < N; i++) {
            Fi[i] *= coef;
        }
        //вспомогательные преобразования
        double[] xx = new double[Psi.length];
        double[] xx1 = new double[Fi.length];
        for (int j = 0; j < Psi.length; j++) {
            xx[j] = Psi[j];
            xx1[j] = Fi[j];
        }
        //если решение найдено, отрисовывается график Psi,Fi
        if (print)
            printChartPsiFi(e, xx, xx1);
        WaveFunction wave = new WaveFunction(Arrays.copyOf(Psi, Psi.length), e);
        wave.Psi = xx;
        wave.Fi = xx1;
        return wave;
    }

    /**
     * Функция метода бисекции для нахождения корней f(E)=0
     *
     * @param left  левая граница
     * @param right правая граница
     * @param eps   точность
     */
    public static double methodBisection(double left, double right, double eps) throws IOException {
        if (f_fun(right) * f_fun(left) > 0.0) {
            return 0.0;
        }
        while (Math.abs(right - left) > eps) {
            double xr = (left + right) / 2;
            if (f_fun(right) * f_fun(xr) < 0.0) {
                left = xr;
            } else {
                right = xr;
            }
            if (f_fun(left) * f_fun(xr) < 0.0) {
                right = xr;
            } else {
                left = xr;
            }
        }
        return (left + right) / 2;
    }

    /**
     * Решение интеграла методом трапеций
     *
     * @param Psi волновая функция
     * @return значение интеграла
     */
    public static double I(double[] Psi) {
        double h = 2 * L / (N - 1);
        double sum = 0.0;
        for (int i = 1; i < N-1; i++) {
            sum += Psi[i] * Psi[i];
        }
        return h * ((Psi[0] * Psi[0] + Psi[N - 1] * Psi[N - 1]) / 2 + sum);
    }

    /**
     * Вычисление квантово-механической величины <x>
     *
     * @param Psi волновая функция
     * @return значение интеграла
     */
    public static double I_x(double[] Psi) {
        double h = 2 * L / (N - 1);
        double sum = 0.0;
        for (int i = 1; i < N-1; i++) {
            sum += (-L + i * h) * Psi[i] * Psi[i];
        }
        return h * ((L * L * Psi[0] * Psi[0] + L * L * Psi[N - 1] * Psi[N - 1]) / 2 + sum);
    }

    /**
     * Вычисление квантово-механической величины <x^2>
     *
     * @param Psi волновая функция
     * @return значение интеграла
     */
    public static double I_x_2(double[] Psi) {
        double h = 2 * L / (N - 1);
        double sum = 0.0;
        for (int i = 1; i < N-1; i++) {
            sum += (-L + i * h) * (-L + i * h) * Psi[i] * Psi[i];
        }
        return h * ((L * L * L * L * Psi[0] * Psi[0] + L * L * L * L * Psi[N - 1] * Psi[N - 1]) / 2 + sum);
    }

    /**
     * Квантово-механическая нормировка
     *
     * @param wave волновая функция
     * @throws IOException
     */
    public static void norm(WaveFunction wave) throws IOException {
        double[] waveFunction = wave.getX();
        double C = I(waveFunction);
        System.out.println("C=" + C);
        for (int j = 0; j < waveFunction.length; j++) {
            wave.x[j] /= Math.sqrt(C);
            waveFunction[j] /= Math.sqrt(C);
        }
        System.out.println("I=" + I(waveFunction));
    }

    /**
     * Вычисление |Psi(x)|^2 плотности вероятности
     */
    public static double[] probabilityDensity(double[] Psi) {
        double[] xx = new double[Psi.length];
        for (int i = 0; i < Psi.length; i++) {
            xx[i] = Psi[i] * Psi[i];
        }
        return xx;
    }

}
