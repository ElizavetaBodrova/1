package km;

import java.io.IOException;

import static km.methods.Ritz_method.RitzMethod.findWaveFunctionByMethodRitza;

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
