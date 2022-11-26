package km.methods.target;

import static km.utils.Common.N;
import static km.utils.TaskDefinition.U0;

public class TargetUtils {
    //границы энергий
    public static double E_LEFT = U0 + 0.01;
    public static double E_RIGHT = 0.8;
    //количество точек для энергий
    public static int ne = 1001;
    //точность
    public static double tol = 1.0e-7;
    //номера узла сшивки
    public static int R = (N - 1) / 2;
}
