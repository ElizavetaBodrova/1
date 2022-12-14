package km.model;

/**
 * Волновая функция
 */
public class WaveFunctionTargetMethod {

    //набор точек, полученный численным методом (с кв.- мех. нормировкой)
    public Double[] x;
    //набор точек, полученный численным методом "вперед"
    public double[] Psi;
    //набор точек, полученный численным методом "назад"
    public double[] Fi;
    //энергия
    public double E;
    //Разность производных в узле сшивки
    public double fE;

    public WaveFunctionTargetMethod(Double[] x, double E) {
        this.E = E;
        this.x = x;
    }

    public WaveFunctionTargetMethod() {
    }

    public double[] getX() {
        double[] xx = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i];
        }
        return xx;
    }

    //вычисление номера состояния для волновой функции
    public int getNumberOfStation() {
        int station = 0;
        double[] xx = getX();
        for (int i = 1; i < xx.length - 1; i++) {
            if (i != xx.length - 2 && Math.signum(xx[i]) * Math.signum(xx[i + 1]) <= 0) {
                station++;
            }
        }
        return station;
    }
}
