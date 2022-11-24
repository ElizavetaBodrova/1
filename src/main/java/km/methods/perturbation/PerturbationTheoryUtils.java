package km.methods.perturbation;

import static km.utils.TaskDefenition.L;
import static km.utils.TaskDefenition.U;
import static km.utils.TaskDefenition.W;

public class PerturbationTheoryUtils {

    public static double U_perturbation(double x){
        return Math.abs(x)<L?(-1+(x+L)/(2.0*L)):W;
    }

    public static double V(double x){
        return U_perturbation(x)-U(x);
    }
}
