package km.methods.perturbation;

import java.io.IOException;

import static km.methods.perturbation.PerturbationTheoryUtils.V;
import static km.utils.Common.N;
import static km.utils.TaskDefenition.L;

public class PerturbationTheory {



    public static void findWaveFunctionByPerturbationTheory() throws IOException {

    }

    public static double[] funct(double[] psi0_left, double[] psi0_right) {
        double[] result = new double[N];
        double h = 2 * L / (N - 1);
        for (int i = 0; i < N; i++) {
            result[i]=psi0_left[i]*V(-L+i*h)*psi0_right[i];
        }
        return result;
    }

    /**
     * Вычисление интеграла методом трапеций
     * @param f
     * @return
     */
    public static double I(double[] f) {
        double h = 2 * L / (N - 1);
        double sum = 0.0;
        for (int i = 0; i < N; i++) {
            sum += f[i];
        }
        return h * ((f[0] + f[N - 1]) / 2 + sum);
    }

    /**
     * Вторая поправка к энергии основного состояния возмущенной систмемы
     * @return
     */
    public static double e_corr_2(int kmax){
        double s=0;
        for (int i = 2; i < kmax+1; i++) {
            double[] psi0_left=new double[N];
            double[] psi0_right=new double[N];
            double Vmn=I(funct(psi0_left,psi0_right));
            s+=Vmn*Vmn/12;
        }
        return s;
    }
}
