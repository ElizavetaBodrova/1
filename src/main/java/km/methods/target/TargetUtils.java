package km.methods.target;

import km.model.WaveFunctionTargetMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static km.methods.target.TargetMethod.I_x;
import static km.methods.target.TargetMethod.I_x_2;
import static km.utils.Common.N;
import static km.utils.TaskDefinition.U0;

public class TargetUtils {
    //границы энергий
    public static double E_LEFT = U0 + 0.2;
    public static double E_RIGHT = 9.0;
    //количество точек для энергий
    public static int ne = 1001;
    //точность
    public static double tol = 1.0e-5;
    //номера узла сшивки
    public static int R = (N - 1) / 2;

    //запись в файл
    public static void writeResultFile(String dir, List<WaveFunctionTargetMethod> list) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(dir + "/result.txt"),
                true);
        String s = "--------------\r\n";
        fileOutputStream.write(s.getBytes());
        Set<WaveFunctionTargetMethod> solves = list.stream().collect(Collectors.toSet());
        for (WaveFunctionTargetMethod solve : solves) {
            s = "*********************\n";
            fileOutputStream.write(s.getBytes());
            s = "E=" + solve.E + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "f(E)=" + solve.fE + "\r\n";
            fileOutputStream.write(s.getBytes());
            s = "n=" + solve.getNumberOfStation() + "\r\n";
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
