package km.utils;

import km.model.WaveFunction;
import km.model.WaveFunctionTargetMethod;
import km.model.WaveFunctionPerturbationTheory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;

import static km.methods.perturbation.PerturbationTheoryUtils.U_perturbation;
import static km.methods.target.TargetMethod.probabilityDensity;
import static km.utils.Common.N;
import static km.utils.TaskDefinition.L;
import static km.utils.TaskDefinition.U;
import static org.jfree.chart.ChartUtils.saveChartAsJPEG;

/**
 *
 */
public class Chart {
    private static int i_image = 0;

    /**
     * Сохранение промежуточных графиков Psi/Fi
     */
    public static void printChartPsiFi(double E, double[] Psi,
                                       double[] Fi) throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        XYSeries U_chart = new XYSeries("U");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / (N - 1));
            U[i] = U(t[i]);
            U_chart.add(t[i], U[i]);
        }
        XYSeries psi = new XYSeries("Psi");
        XYSeries fi = new XYSeries("Fi");
        for (int i = 0; i < N; i++) {
            psi.add(t[i], Psi[i]);
            fi.add(t[i], Fi[i]);
            U_chart.add(t[i], U[i]);
        }
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(psi);
        xyDataset.addSeries(fi);
        xyDataset.addSeries(U_chart);
        String nameChart = "Волновые функции, полученные интегрированием `вперед` и `назад` для " +
                "Е=" + String.format("%.5f", E);
        JFreeChart chart = ChartFactory
                .createXYLineChart(nameChart, "х", "Psi(x),Fi(x),U(x)",
                        xyDataset,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        ChartFactory.createXYLineChart("true", "х", "Psi(x),Fi(x),U(x)",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        saveChartAsJPEG(new File("src/main/resources/temp/" + (i_image++) + " " + String.format("%" +
                        ".3f", E) + ".jpeg"),
                (float) 1.0,
                chart,
                800, 600);
    }

    /**
     * Сохранить 2х графиков в формате jpg:
     * Psi/Fi и волновая функция с плотностью вероятности
     */
    public static void printChartTargetMethod(String dir, String name, WaveFunctionTargetMethod solve) throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        double[] Psi_2 = probabilityDensity(solve.getX());
        XYSeries series_x = new XYSeries("Psi e=" + String.format("%.5f", solve.E));
        XYSeries series_Psi = new XYSeries("Psi");
        XYSeries series_Fi = new XYSeries("Fi");
        XYSeries U_chart = new XYSeries("U");
        XYSeries probabilityDensity = new XYSeries("|Psi|²");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / N);
            U[i] = U(t[i]);
            U_chart.add(t[i], U[i]);
        }
        for (int i = 0; i < N; i++) {
            series_Psi.add(t[i], solve.Psi[i]);
            series_Fi.add(t[i], solve.Fi[i]);
            series_x.add(t[i], solve.x[i]);
            probabilityDensity.add(t[i], Psi_2[i]);
        }
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(series_x);
        result.addSeries(U_chart);
        result.addSeries(probabilityDensity);
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(series_Psi);
        xyDataset.addSeries(series_Fi);
        xyDataset.addSeries(U_chart);
        String nameChart = solve.getNumberOfStation() == 0
                ? "Волновая функция основного состояния"
                : "Волновая функция " + solve.getNumberOfStation() + "ого возбужденного состояния";
        JFreeChart chartResult = ChartFactory
                .createXYLineChart(nameChart, "x", "Psi(x), |Psi(x)|², U(x)",
                        result,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        ChartFactory.createXYLineChart("true", "x", "Psi(x), |Psi(x)|², U(x)",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        saveChartAsJPEG(new File(dir + "/" + name + ".jpeg"), (float) 1.0, chartResult,
                800, 600);
        JFreeChart chart = ChartFactory
                .createXYLineChart(nameChart, "x", "Psi(x),Fi(x),U(x)",
                        xyDataset,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        ChartFactory.createXYLineChart("true", "x", "Psi(x),Fi(x),U(x)",
                xyDataset,
                PlotOrientation.VERTICAL,
                true, true, true);
        saveChartAsJPEG(new File(dir + "/PsiFi " + name + ".jpeg"), (float) 1.0, chart,
                800, 600);
    }

    /**
     *  Сохранить график в формате jpg потенциальной функции невозмущенной системы
     */
    public static void printU() throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        XYSeries U_chart = new XYSeries("U");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / N);
            U[i] = U(t[i]);
            U_chart.add(t[i], U[i]);
        }
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(U_chart);
        String nameChart = "Потенциальная функция";
        JFreeChart chartResult = ChartFactory
                .createXYLineChart(nameChart, "x", "U(x)",
                        result,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        saveChartAsJPEG(new File("U.jpeg"), (float) 1.0, chartResult,
                800, 600);
    }

    /**
     *  Сохранить график в формате jpg потенциальной функции возмущенной системы
     */
    public static void printUPerturbation() throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        XYSeries U_chart = new XYSeries("U");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / N);
            U[i] = U_perturbation(t[i]);
            U_chart.add(t[i], U[i]);
        }
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(U_chart);
        String nameChart = "Потенциальная функция возмущенной системы";
        JFreeChart chartResult = ChartFactory
                .createXYLineChart(nameChart, "x", "U(x)",
                        result,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        saveChartAsJPEG(new File("UPerturbation.jpeg"), (float) 1.0, chartResult,
                800, 600);
    }

    /**
     * Сохранить график в формате jpg волновых функций, полученных при помощи возмущенной системы
     */
    public static void printChartPerturbationTheory(String dir, String name, WaveFunctionPerturbationTheory solve) throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        double[] Psi_2 = probabilityDensity(solve.getX());
        XYSeries series_x = new XYSeries("Psi e=" + String.format("%.5f", solve.E));
        XYSeries U_chart = new XYSeries("U");
        XYSeries probabilityDensity = new XYSeries("|Psi|²");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / N);
            U[i] = U(t[i]);
            U_chart.add(t[i], U[i]);
        }
        for (int i = 0; i < N; i++) {
            series_x.add(t[i], solve.x[i]);
            probabilityDensity.add(t[i], Psi_2[i]);
        }
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(series_x);
        result.addSeries(U_chart);
        result.addSeries(probabilityDensity);
        String nameChart = solve.getNumberOfStation() == 0
                ? "Волновая функция основного состояния"
                : "Волновая функция " + solve.getNumberOfStation() + "ого возбужденного состояния";
        JFreeChart chartResult = ChartFactory
                .createXYLineChart(nameChart, "x", "Psi(x), |Psi(x)|², U(x)",
                        result,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        saveChartAsJPEG(new File(dir + "/" + name + ".jpeg"), (float) 1.0, chartResult,
                800, 600);
    }

    /**
     * Сохраняет график сравнения волновых функций, полученных методом пристрелки и при помощи
     * возмущенной системы
     */
    public static void printChartComparePerturbationTheoryVsTargetMethod(String dir, String name,
                                                                         WaveFunctionPerturbationTheory solvePer,
                                                                         WaveFunctionTargetMethod solveTarget) throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        XYSeries series_target = new XYSeries("Psi методом пристрелки e=" + String.format("%.5f",
                solveTarget.E));
        XYSeries series_per = new XYSeries("Psi теория возмущений e=" + String.format("%.5f",
                solvePer.E));
        XYSeries U_chart = new XYSeries("U");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / N);
            U[i] = U(t[i]);
            U_chart.add(t[i], U[i]);
        }
        for (int i = 0; i < N; i++) {
            series_target.add(t[i], solveTarget.x[i]);
            series_per.add(t[i], solvePer.x[i]);
        }
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(series_target);
        result.addSeries(series_per);
        result.addSeries(U_chart);
        String nameChart = solvePer.getNumberOfStation() == 0
                ? "Волновая функция основного состояния"
                : "Волновая функция " + solvePer.getNumberOfStation() + "ого возбужденного состояния";
        JFreeChart chartResult = ChartFactory
                .createXYLineChart(nameChart, "x", "Psi(x), |Psi(x)|², U(x)",
                        result,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        saveChartAsJPEG(new File(dir + "/" + name + ".jpeg"), (float) 1.0, chartResult,
                800, 600);
    }

    public static void printChartMethodRitza(String dir, String name,
                                             WaveFunction solve) throws IOException {
        double[] U = new double[N + 1];
        double[] t = new double[N + 1];
        double[] Psi_2 = probabilityDensity(solve.x);
        XYSeries series_x = new XYSeries("Psi e=" + String.format("%.5f", solve.E));
        XYSeries U_chart = new XYSeries("U");
        //XYSeries probabilityDensity = new XYSeries("|Psi|²");
        for (int i = 0; i < N + 1; i++) {
            t[i] = -L + i * (2 * L / N);
            U[i] = U(t[i]);
            U_chart.add(t[i], U[i]);
        }
        for (int i = 0; i < N; i++) {
            series_x.add(t[i], solve.x[i]);
          //  probabilityDensity.add(t[i], Psi_2[i]);
        }
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(series_x);
        result.addSeries(U_chart);
        //result.addSeries(probabilityDensity);
        String nameChart = solve.getNumberOfStation() == 0
                ? "Волновая функция основного состояния"
                : "Волновая функция " + solve.getNumberOfStation() + "ого возбужденного состояния";
        JFreeChart chartResult = ChartFactory
                .createXYLineChart(nameChart, "x", "Psi(x), |Psi(x)|², U(x)",
                        result,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        saveChartAsJPEG(new File(dir + "/" + name + ".jpeg"), (float) 1.0, chartResult,
                800, 600);
    }

}
