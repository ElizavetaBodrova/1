package km.model;

public class WaveFunction {
    public double[] x;
    public double E;

    public WaveFunction(double[] x, double E) {
        this.x = x;
        this.E = E;
    }

    //вычисление номера состояния для волновой функции
    public int getNumberOfStation() {
        int station = 0;
        double[] xx = x;
        for (int i = 1; i < xx.length - 1; i++) {
            if (i != xx.length - 2 && Math.signum(xx[i]) * Math.signum(xx[i + 1]) <= 0) {
                station++;
            }
        }
        return station;
    }
}
