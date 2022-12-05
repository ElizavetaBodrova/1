package km.methods.perturbation;

import km.model.WaveFunctionPerturbationTheory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static km.methods.target.TargetMethod.I_x;
import static km.methods.target.TargetMethod.I_x_2;
import static km.utils.TaskDefinition.L;
import static km.utils.TaskDefinition.U;
import static km.utils.TaskDefinition.W;

public class PerturbationTheoryUtils {

    /**
     * Потенциальная функция возмущенной системы
     */
    public static double U_perturbation(double x) {
        if (Math.abs(x) < L) {
            if (x <= 0.5 && x >= 0.1) {
                return 3.0;
            } else {
                return U(x);
            }
        } else {
            return W;
        }
    }

    /**
     * Возмущение
     */
    public static double V(double x) {
        return U_perturbation(x) - U(x);
    }

    //запись в файл
    public static void writeResultFilePerturbationTheory(String dir, List<WaveFunctionPerturbationTheory> list) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(dir + "/result.txt"),
                true);
        String s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
        Set<WaveFunctionPerturbationTheory> solves = list.stream().collect(Collectors.toSet());
        for (WaveFunctionPerturbationTheory solve : solves) {
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
            s = "n=" + solve.getNumberOfStation() + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "En0=" + solve.E0 + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "En1=" + solve.E1 + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "En2=" + solve.E2 + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "E=" + solve.E + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "<x>=" + I_x(solve.getX()) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "<x^2>=" + I_x_2(solve.getX()) + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
        }
        s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
    }
}
