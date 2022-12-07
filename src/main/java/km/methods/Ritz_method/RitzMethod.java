package km.methods.Ritz_method;

import km.model.WaveFunction;
import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static km.methods.Ritz_method.RitzMethodUtils.M;
import static km.methods.Ritz_method.RitzMethodUtils.psi0;
import static km.methods.Ritz_method.RitzMethodUtils.writeResultFileRitzMethod;
import static km.methods.perturbation.PerturbationTheory.I;
import static km.utils.Chart.printChartRitzMethod;
import static km.utils.Common.N;
import static km.utils.Common.сonvertToHartri;
import static km.utils.TaskDefinition.L;
import static km.utils.TaskDefinition.U;

public class RitzMethod {

    /**
     * Прямой вариационный метод (метод Ритца)
     */
    public static void findWaveFunctionByMethodRitza() throws IOException {
        //перевод в атомные единицы Хартри
        сonvertToHartri();
        System.out.println("Прямой вариационный метод (метод Ритца)");
        //построение матрицы H
        double[][] test = buildH();
        DoubleMatrix matrix = new DoubleMatrix(test);
        //получение собственных векторов и собственных значений
        ComplexDoubleMatrix[] eigenVectors = Eigen.eigenvectors(matrix);
        ComplexDoubleMatrix eigenvalues = Eigen.eigenvalues(matrix);
        //форматирование результата
        List<ComplexDouble[]> list = new ArrayList<>();
        for (int i = 0; i < eigenVectors[0].rows; i++) {
            ComplexDouble[] t = new ComplexDouble[eigenVectors[0].columns];
            for (int j = 0; j < eigenVectors[0].columns; j++) {
                t[j] = eigenVectors[0].get(j, i);
            }
            list.add(t);
        }
        //поиск индекса собственного вектора и собственного значения для основного состояния
        int minIndex = getMinIndex(matrix);
        //преобразование из комплексного типа в double
        ComplexDouble[] C0 = list.get(minIndex);
        double[] c0 = new double[C0.length];
        for (int j = 0; j < C0.length; j++) {
            c0[j] = C0[j].real();
        }
        //вычисление волновой функции основного состояния
        double[] result = new double[N];
        for (int k = 0; k < M; k++) {
            double[] basePsi = psi0(k);
            for (int j = 0; j < N; j++) {
                result[j] += c0[k] * basePsi[j];
            }
        }
        WaveFunction wave = new WaveFunction(result, eigenvalues.get(minIndex).real());
        //формирование файла отчета и графики
        String nameDir = "Прямой вариационный метод";
        File theDir = new File("src/main/resources/" + nameDir);
        if (!theDir.exists()) {
            theDir.mkdirs();
        } else {
            theDir = new File("src/main/resources/" + nameDir + (int) (N + Math.random() * 100 - 1));
            theDir.mkdirs();
        }
        writeResultFileRitzMethod(theDir.getPath(), wave);
        printChartRitzMethod(theDir.getPath(), "test", wave);

    }

    /**
     * Построение матрицы H, где H[i][j]=<i|Ĥ|j>
     *
     * @return матрица H
     */
    public static double[][] buildH() throws IOException {
        double[][] H_EI = new double[M][M];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                H_EI[i][j] = Hmk(i, j);

            }
        }
        return H_EI;
    }

    /**
     * Вычисление значения интеграла m|Ĥ|k
     *
     * @param m индекс возбужденного состояния волновой функции базиса
     * @param k индекс возбужденного состояния волновой функции базиса
     * @return int(Psi_m * Ĥ ( Psi_k))
     */
    public static double Hmk(int m, int k) throws IOException {
        double[] fi_m = psi0(m);
        double[] fi_mfi_k = new double[N];
        double[] H_fi_k = HPsi(k);
        for (int i = 0; i < N; i++) {
            fi_mfi_k[i] = fi_m[i] * H_fi_k[i];
        }
        return I(fi_mfi_k);
    }

    /**
     * Вычисление значения Ĥ(Psi_k)=(T+U)(Psi_k)
     *
     * @param k индекс возбужденного состояния волновой функции базиса
     */
    public static double[] HPsi(int k) throws IOException {
        double[] fi_k = psi0(k);
        double[] result = new double[N];
        double h = (2 * L) / (N - 1);
        double[] derivPsi = secondDeriv(fi_k, h);
        for (int i = 0; i < N; i++) {
            result[i] = derivPsi[i] / (-2) + U(-L + i * h) * fi_k[i];
        }
        return result;
    }

    /**
     * Вычисление второй производной на четырех точечном шаблоне
     *
     * @param y значения исходной функции
     * @param h шаг
     * @return значения второй производной в каждой точке
     */
    public static double[] secondDeriv(double[] y, double h) {
        double[] result = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            if (i == 0) {
                result[i] = (2 * y[i] - 5 * y[i + 1] + 4 * y[i + 2] - y[i + 3]) / (h * h);
            } else {
                if (i == y.length - 1) {
                    result[i] = (-y[i - 3] + 4 * y[i - 2] - 5 * y[i - 1] + 2 * y[i]) / (h * h);
                } else {
                    result[i] = (y[i - 1] - 2 * y[i] + y[i + 1]) / (h * h);
                }
            }
        }
        return result;
    }

    /**
     * Функция поиска индекса минимального элемента
     * среди собственных значений матрицы H
     *
     * @param matrix матрица H
     * @return индекс минимального элемента
     */
    private static int getMinIndex(DoubleMatrix matrix) {
        ComplexDouble[] doubleMatrix = Eigen.eigenvalues(matrix).toArray();
        int minIndex = 0;
        for (int i = 0; i < doubleMatrix.length; i++) {
            double newnumber = doubleMatrix[i].abs();
            if (newnumber < doubleMatrix[minIndex].abs()) {
                minIndex = i;
            }
        }
        return minIndex;
    }

}
