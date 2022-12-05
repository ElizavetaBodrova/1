package km;

import java.io.IOException;

import static km.methods.method_ritza.MethodRitza.findWaveFunctionByMethodRitza;
import static km.methods.perturbation.PerturbationTheory.findWaveFunctionByPerturbationTheory;
import static km.methods.target.TargetMethod.findWaveFunctionByTargetMethod;

public class Main {

    public static void main(String[] args) throws IOException {
        //1 - метод пристрелки
        //findWaveFunctionByTargetMethod();
        //2 - теория возмущений
        //findWaveFunctionByPerturbationTheory();
        //3 - прямой вариационный метод
        findWaveFunctionByMethodRitza();
    }

}
