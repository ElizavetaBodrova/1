package km.methods.Ritz_method;

import km.model.WaveFunction;
import km.model.WaveFunctionPerturbationTheory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static km.methods.perturbation.PerturbationTheory.I;
import static km.methods.target.TargetMethod.I_x;
import static km.methods.target.TargetMethod.I_x_2;
import static km.methods.target.TargetMethod.probabilityDensity;
import static km.utils.Common.N;
import static km.utils.TaskDefinition.L;

public class RitzMethodUtils {

    //размерность матрицы H
    //(количество слагаемых в разложении волновой функции)
    public static int M = 21;

    /**
     * Базисный набор - волновые функции частицы в
     * одномерной прямоугольной яме с бесконечными стенками
     *
     * @param k индекс возбужденного состояния
     * @return волновую функцию
     */
    public static double[] psi0(int k) {
        double[] result = new double[N];
        double h = (2 * L) / (N - 1);
        for (int i = 0; i < N; i++) {
            double arg = (Math.PI * (k + 1) * (-L + i * h)) / (2 * L);
            if ((k + 1) % 2 == 0) {
                result[i] = Math.sin(arg) / Math.sqrt(L);
            } else {
                result[i] = Math.cos(arg) / Math.sqrt(L);
            }
        }
        return result;
    }

    public static void writeResultFileRitzMethod(String dir,
                                                 WaveFunction solve) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(dir + "/result.txt"),
                true);
        String s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
            s = "n=" + solve.getNumberOfStation() + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "E=" + solve.E + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "Проверка: I=" + I(probabilityDensity(solve.x)) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "<x>=" + I_x(solve.x) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "<x^2>=" + I_x_2(solve.x) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
        s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
    }

}
