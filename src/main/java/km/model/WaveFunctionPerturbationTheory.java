package km.model;

public class WaveFunctionPerturbationTheory {

    //набор точек, полученный численным методом (с кв.- мех. нормировкой)
    public double[] x;
    //энергия
    public double E;
    //энергия невозмущенной системы
    public double E0;
    //поправка 1 порядка
    public double E1;
    //поправка 2 порядка
    public double E2;
    public double[] getX(){
        return x;
    }

    //вычисление номера состояния для волновой функции
    public int getNumberOfStation(){
        int station=0;
        double[] xx=getX();
        for (int i = 1; i < xx.length-1; i++) {
            if(i!=xx.length-2 && Math.signum(xx[i])*Math.signum(xx[i+1])<=0){
                station++;
            }
        }
        return station;
    }
}
