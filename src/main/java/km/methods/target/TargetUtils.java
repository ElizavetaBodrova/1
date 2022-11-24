package km.methods.target;

import static km.utils.Common.N;
import static km.utils.TaskDefenition.U0;

public class TargetUtils {
    //границы энергий
    public static double E_LEFT = U0 + 0.1;
    public static double E_RIGHT = 0.5;
    //количество точек для энергий
    public static int ne = 1001;
    //точность
    public static double tol = 1.0e-7;
    //номера узла сшивки
    public static int R = (N - 1) / 2;
}
