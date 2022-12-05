package km.methods.perturbation;

import km.model.WaveFunctionTargetMethod;
import km.model.WaveFunctionPerturbationTheory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static km.methods.perturbation.PerturbationTheoryUtils.V;
import static km.methods.perturbation.PerturbationTheoryUtils.writeResultFilePerturbationTheory;
import static km.methods.target.TargetMethod.I_x;
import static km.methods.target.TargetMethod.findWaveFunctionByTargetMethod;
import static km.methods.target.TargetMethod.getSolves;
import static km.methods.target.TargetMethod.probabilityDensity;
import static km.utils.Chart.printChartComparePerturbationTheoryVsTargetMethod;
import static km.utils.Chart.printChartPerturbationTheory;
import static km.utils.Chart.printU;
import static km.utils.Chart.printUPerturbation;
import static km.utils.Common.N;
import static km.utils.TaskDefinition.L;

public class PerturbationTheory {

    //Решение, полученное при помощи метода пристрелки (невозмущенная система)
    private static List<WaveFunctionTargetMethod> solvesByTargetMethod;

    /**
     * Численное решение одномерного стационарного
     * уравнение Шредингера в рамках теории возмущений
     * @throws IOException
     */
    public static void findWaveFunctionByPerturbationTheory() throws IOException {
        System.out.println("Метод пристрелки");
        //Поиск решений невозмущенной системы
        findWaveFunctionByTargetMethod();
        solvesByTargetMethod = getSolves();
        //Вывод графиков возмущенной и невозмущенной систем
        printU();
        printUPerturbation();

        System.out.println("Теория возмущений");
        int kmax = solvesByTargetMethod.size();
        List<WaveFunctionPerturbationTheory> solves = new ArrayList<>();
        //Поиск волновой функции и энергии основного состояния
        WaveFunctionPerturbationTheory wave0 = new WaveFunctionPerturbationTheory();
        wave0.x = psi_corr_1(0, kmax);
        wave0.E0 = solvesByTargetMethod.get(0).E;
        wave0.E1 = Vnm(0, 0);
        wave0.E2 = e_corr_2(0, kmax);
        wave0.E = e(0,kmax);
        wave0.x = psi(0, kmax);
        //Добавление решения
        solves.add(wave0);
        //Поиск волновой функции и энергии первого состояния
        WaveFunctionPerturbationTheory wave1 = new WaveFunctionPerturbationTheory();
        wave1.x = psi_corr_1(1, kmax);
        wave1.E0 = solvesByTargetMethod.get(1).E;
        wave1.E1 = Vnm(1, 1);
        wave1.E2 = e_corr_2(1, kmax);
        wave1.E = e(1,kmax);
        wave1.x = psi(1, kmax);
        //Добавление решения
        solves.add(wave1);
        //Проверка кв.-мех. нормировки
        System.out.println("Проверка");
        System.out.println("n=0");
        System.out.println(I(probabilityDensity(wave0.x)));
        System.out.println("n=1");
        System.out.println(I(probabilityDensity(wave1.x)));
        //формирование файла отчета и графики
        String nameDir = "Теория возмущений";
        File theDir = new File("src/main/resources/" + nameDir);
        if (!theDir.exists()) {
            theDir.mkdirs();
        } else {
            theDir = new File("src/main/resources/" + nameDir + (int)(N + Math.random() * 100 - 1));
            theDir.mkdirs();
        }
        writeResultFilePerturbationTheory(theDir.getPath(), solves);
        for (int i = 0; i < solves.size(); i++) {
            printChartPerturbationTheory(theDir.getPath(), "test" + i, solves.get(i));
            printChartComparePerturbationTheoryVsTargetMethod(theDir.getPath(), "testCompare" + i
                    ,solves.get(i),solvesByTargetMethod.get(i));
        }

    }

    /**
     *
     * @param n
     * @param m
     * @return
     */
    public static double Vnm(int n, int m) {
        double[] result = new double[N];
        double[] psi0_n = solvesByTargetMethod.get(n).getX();
        double[] psi0_m = solvesByTargetMethod.get(m).getX();
        double h = 2 * L / (N - 1);
        for (int i = 0; i < N; i++) {
            result[i] = psi0_n[i] * V(-L + i * h) * psi0_m[i];
        }
        return I(result);
    }

    /**
     * Вычисление интеграла методом трапеций
     *
     */
    public static double I(double[] f) {
        double h = 2 * L / (N - 1);
        double sum = 0.0;
        for (int i = 1; i < N - 1; i++) {
            sum += f[i];
        }
        return h * ((f[0] + f[N - 1]) / 2 + sum);
    }

    /**
     * Вторая поправка к энергии k-ого состояния возмущенной систмемы
     *
     */
    public static double e_corr_2(int k, int kmax) {
        double s = 0.0;
        for (int i = 0; i < kmax; i++) {
            if (i != k) {
                double Vnm = Vnm(k, i);
                boolean check=
                        Math.abs(Vnm)<Math.abs(solvesByTargetMethod.get(k).E - solvesByTargetMethod.get(i).E);
                System.out.println(check+" "+Math.abs(Vnm)+" "+Math.abs(solvesByTargetMethod.get(k).E - solvesByTargetMethod.get(i).E));
                s += (Vnm * Vnm) / (solvesByTargetMethod.get(k).E - solvesByTargetMethod.get(i).E);
            }
        }
        return s;
    }

    /**
     * Коэффициенты первой поправки к волновой функции
     * k-ого состояния возмущенной систмемы
     *
     * @return массив коэффициентов
     */
    public static double[] c_psi_corr_1(int k, int kmax) {
        double[] c = new double[kmax];
        for (int i = 0; i < kmax; i++) {
            if (i != k) {
                double Vnm = Vnm(k, i);
                c[i] = Vnm / (solvesByTargetMethod.get(k).E - solvesByTargetMethod.get(i).E);
            }
        }
        return c;
    }

    /**
     * Первая поправка волновой функции
     * k-ого состояния возмущенной систмемы
     *
     */
    public static double[] psi_corr_1(int k, int kmax) {
        double[] c = c_psi_corr_1(k, kmax);
        double[] s = new double[N];
        for (int i = 0; i < kmax; i++) {
            if (i != k) {
                for (int j = 0; j < N; j++) {
                    s[j] += c[i] * solvesByTargetMethod.get(i).getX()[j];
                }
            }
        }
        return s;
    }

    /**
     * Волновая функция k-ого состояния
     *
     */
    public static double[] psi(int k, int kmax) {
        double[] psi_1 = psi_corr_1(k, kmax);
        double[] psi_0 = solvesByTargetMethod.get(k).getX();
        double[] s = new double[N];
        for (int j = 0; j < N; j++) {
            s[j] += psi_1[j] + psi_0[j];
        }
        return s;
    }

    /**
     * Энергия k-ого состояния
     */
    public static double e(int k, int kmax) {
        return solvesByTargetMethod.get(k).E + Vnm(k, k) + e_corr_2(k, kmax);
    }
}
