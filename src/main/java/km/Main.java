package km;

import km.model.WaveFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static km.methods.target.TargetMethod.I_x;
import static km.methods.target.TargetMethod.I_x_2;
import static km.methods.target.TargetMethod.findWaveFunctionByTargetMethod;

public class Main {
    /*
    e1=0.1
    e2=0.7 для 1 состояния

    e1=0.01
    e2=0.7 для 0 состояния
    */

    public static void main(String[] args) throws IOException {
        findWaveFunctionByTargetMethod();
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
