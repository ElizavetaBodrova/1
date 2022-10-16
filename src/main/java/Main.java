import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jfree.chart.ChartUtils.saveChartAsJPEG;

public class Main {
    /*
    e1=0.1
    e2=0.7 для 1 состояния

    e1=0.01
    e2=0.7 для 0 состояния
    */

    //количество точек
    public static int n = 1002;
    //L,V0,U0 - из условия задачи, W устанавливается для красоты графиков
    public static double L = 4.0;
    public static double V0 = 20.0;
    public static double U0 = 0.0;
    public static double W = 4.0;
    //константы для перевода в атомные единицы Хартри
    // 1 a.u. of length = 0.5292 A
    public static double clength = 0.5292;
    // 1 a.u. of energy = 27.211 eV
    public static double cenergy = 27.212;

    /**
     * Конвертация в атомные единицы Хартри
     */
    public static void сonvertToHartri() {
        L = L / clength;
        V0 = V0 / cenergy;
    }

    /**
     * Тета-функция Хевисайда
     *
     * @param x
     * @return x < 0 : 0
     * x==0 : 1
     * x > 0 : 1
     */
    public static double Hevisaid(double x) {
        return x >= 0 ? 1 : 0;
    }

    /**
     * Потенциальная функция(вар.10)
     *
     * @param x argument
     */
    public static double U(double x) {
        return Math.abs(x) < L ? V0 * Hevisaid(x) : W;
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
    public static double deriv(Double[] Y, double h, int m) {
        return (Y[m - 2] - Y[m + 2] + 8.0 * (Y[m + 1] - Y[m - 1])) / (12 * h);
    }

    /**
     * Вычисление разности производных в узле сшивки
     */
    public static double f_fun(double e, int r, int n) {
        //решение справа налево
        Double[] Psi = new Double[n];
        //решение слева направо
        Double[] Fi = new Double[n];
        //шаг
        double h = 2 * L / (n - 1);
        //численное вычисление решений
        getWaveFunction(Psi, Fi, n, r, e);
        return deriv(Psi, h, r) - deriv(Fi, h, r);

    }

    public static double[] getWaveFunction(Double[] Psi, Double[] Fi, int n, int r, double e) {
        //шаг
        double h = 2 * L / (n - 1);
        //константа для метода Нумерова
        double c = h * h / 12;
        //правая часть
        Double[] F = new Double[n];
        //Вычисление правой части
        for (int i = 0; i < n; i++) {
            F[i] = c * q(e, -L + i * h);
        }
        //Начальные значения по методу Нумерова
        Psi[0] = 0.0;
        Fi[n - 1] = 0.0;
        Psi[1] = 0.01;
        Fi[n - 2] = 0.01;
        //вперед
        for (int i = 1; i < n - 1; i++) {
            double p1 = 2.0 * (1.0 - 5.0 * F[i]) * Psi[i];
            double p2 = (1.0 + F[i - 1]) * Psi[i - 1];
            Psi[i + 1] = (p1 - p2) / (1.0 + F[i + 1]);
        }
        //назад
        for (int i = n - 2; i >= 1; i--) {
            double p1 = 2.0 * (1.0 - 5.0 * F[i]) * Fi[i];
            double p2 = (1.0 + F[i + 1]) * Fi[i + 1];
            Fi[i - 1] = (p1 - p2) / (1.0 + F[i - 1]);
        }
        //математическая нормировка
        double max = Arrays.stream(Psi).peek(x -> Math.abs(x)).max(Double::compareTo).get();
        double min = Arrays.stream(Psi).peek(x -> Math.abs(x)).min(Double::compareTo).get();
        double big = Math.max(max, min);
        for (int i = 0; i < n; i++) {
            Psi[i] /= big;
        }
        double coef = Psi[r] / Fi[r];
        for (int i = 0; i < n; i++) {
            Fi[i] *= coef;
        }
        double[] xx = new double[Psi.length];
        for (int j = 0; j < Psi.length; j++) {
            xx[j] = Psi[j];
        }
        return xx;
    }

    /**
     * Функция метода бисекции для нахождения корней f(E)=0
     *
     * @param x1  левая граница
     * @param x2  правая граница
     * @param eps точность
     * @param r   узел сшивки
     * @param n   количство точек
     */
    public static double methodBisection(double x1, double x2, double eps, int r, int n) {
        if (f_fun(x2, r, n) * f_fun(x1, r, n) > 0.0) {
            return 0.0;
        }
        while (Math.abs(x2 - x1) > eps) {
            double xr = (x1 + x2) / 2;
            if (f_fun(x2, r, n) * f_fun(xr, r, n) < 0.0) {
                x1 = xr;
            } else {
                x2 = xr;
            }
            if (f_fun(x1, r, n) * f_fun(xr, r, n) < 0.0) {
                x2 = xr;
            } else {
                x1 = xr;
            }
        }
        return (x1 + x2) / 2;
    }

    /**
     * Решение интеграла методом трапеций
     *
     * @param Psi волновая функция
     * @param n   количество точек
     * @return значение интеграла
     */
    public static double I(double[] Psi, int n) {
        double h = 2 * L / (n - 1);
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += Psi[i] * Psi[i];
        }
        return h * ((Psi[0] * Psi[0] + Psi[n - 1] * Psi[n - 1]) / 2 + sum);
    }

    /**
     * <x>
     *
     * @param Psi волновая функция
     * @param n   количество точек
     * @return значение интеграла
     */
    public static double I_x(double[] Psi, int n) {
        double h = 2 * L / (n - 1);
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += (-L + i * h) * Psi[i] * Psi[i];
        }
        return h * ((L * L * Psi[0] * Psi[0] + L * L * Psi[n - 1] * Psi[n - 1]) / 2 + sum);
    }

    /**
     * <x^2>
     *
     * @param Psi волновая функция
     * @param n   количество точек
     * @return значение интеграла
     */
    public static double I_x_2(double[] Psi, int n) {
        double h = 2 * L / (n - 1);
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += (-L + i * h) * (-L + i * h) * Psi[i] * Psi[i];
        }
        return h * ((L * L * L * L * Psi[0] * Psi[0] + L * L * L * L * Psi[n - 1] * Psi[n - 1]) / 2 + sum);
    }

    public static void norm(Double[] Psi,Double[] Fi,int r,double eval){
        double[] waveFunction = getWaveFunction(Psi, Fi, n + 1, r, eval);
        double C = I(waveFunction, n);
        System.out.println("C=" + C);
        for (int j = 0; j < waveFunction.length; j++) {
            Psi[j] /= Math.sqrt(C);
        }
    }

    public static void main(String[] args) throws IOException {
        //перевод в атомные единицы Хартри
        сonvertToHartri();
        //номера узла сшивки
        int r = (n - 1) / 2;
        //граничные значения энергий
        double e1 = U0 + 0.1;
        double e2 = 0.7;
        System.out.println("e1=" + e1 + " e2=" + e2);
        //количество точек для энергий
        int ne = 1001;
        //шаг энергий
        double he = (e2 - e1) / (ne - 1);
        //пороговое значение
        double porog = 5.0;
        //точность
        double tol = 1.0e-7;
        List<WaveFunction> solves = new ArrayList<>();
        Double[] Psi = new Double[n + 1];
        Double[] Fi = new Double[n + 1];
        //разность производных в узле сшивке для значения e1
        double e = e1;
        double af = f_fun(e, r, n + 1);
        //поиск простых корней f(E) на отрезке [e1,e2]
        for (int i = 1; i < ne; i++) {
            double e_next = e1 + i * he;
            double af_next = f_fun(e_next, r, n + 1);
            if (af * af_next < 0.0 && Math.abs(af - af_next) < porog) {
                e = e1 + (i - 1) * he;
                double eval = methodBisection(e, e_next, tol, r, n + 1);
                //если найден корень методом бисекции
                if (eval != 0.0) {
                    System.out.println("eval=" + eval);
                    //квантово-механическая нормировка
                    norm(Psi, Fi, r, eval);
                    //добавляем найденную функцию к решениям
                    solves.add(new WaveFunction(Arrays.copyOf(Psi, Psi.length), eval));
                }
            }
        }
        //формирование файла отчета и графики
        String nameDir = "e1=" + e1 + " e2=" + e2;
        File theDir = new File("src/main/resources/" + nameDir);
        if (!theDir.exists()) {
            theDir.mkdirs();
        } else {
            theDir = new File("src/main/resources/" + nameDir + " " + porog);
            theDir.mkdirs();
        }
        writeResultFile(theDir.getPath(), solves);
        for (int i = 0; i < solves.size(); i++) {
            printChart(theDir.getPath(), "test" + i, solves.get(i).E,  solves.get(i).getX());
        }
    }

    /**
     * Сохранить график в формате jpg
     *
     */
    public static void printChart(String dir, String name, double E, double[] x) throws IOException {
        double[] U = new double[n + 1];
        double[] t = new double[n + 1];
        for (int i = 0; i < n + 1; i++) {
            t[i] = -L + i * (2 * L / n);
            U[i] = U(t[i]);
        }
        XYSeries series = new XYSeries("e=" + String.format("%.3f", E));
        XYSeries U_chart = new XYSeries("U");
        for (int i = 0; i < x.length; i++) {
            series.add(t[i], x[i]);
            U_chart.add(t[i], U[i]);
        }
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(series);
        xyDataset.addSeries(U_chart);
        JFreeChart chart = ChartFactory
                .createXYLineChart(name, "t", "x",
                        xyDataset,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        ChartFactory.createXYLineChart("true", "t", "x",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        saveChartAsJPEG(new File(dir + "/" + name + ".jpeg"), (float) 1.0, chart,
                800, 600);
    }

    //запись в файл
    public static void writeResultFile(String dir, List<WaveFunction> list) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(dir + "/result.txt"),
                true);
        String s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
        Set<WaveFunction> solves = list.stream().collect(Collectors.toSet());
        for (WaveFunction solve : solves) {
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
            s = "E=" + solve.E + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "n=" + solve.getNumberOfStation() + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "<x>=" + I_x(solve.getX(),n) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "<x^2>=" + I_x_2(solve.getX(),n) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
        }
        s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
    }
}
