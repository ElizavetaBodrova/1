package km.model;

/**
 * Волновая функция
 */
public class WaveFunction {
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

    public WaveFunction(Double[] x, double E){
        this.E=E;
        this.x=x;
    }

    public double[] getX(){
        double[] xx=new double[x.length];
        for (int i = 0; i < x.length; i++) {
            xx[i]=x[i];
        }
        return xx;
    }

    //вычисление номера состояния для волновой функции
    public int getNumberOfStation(){
        int station=0;
        double[] xx=getX();
        System.out.println("~~~~~");
        for (int i = 1; i < xx.length-1; i++) {
            if(i!=xx.length-2 && Math.signum(xx[i])*Math.signum(xx[i+1])<=0){
                station++;
                System.out.println(i+" "+(i+1));
                System.out.println(xx[i]+" "+xx[i+1]);
            }
        }
        System.out.println(xx[xx.length-1]);
        System.out.println("~~~~~");
        return station;
    }
}
