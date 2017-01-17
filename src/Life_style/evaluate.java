package Life_style;


import ga.core.IIndividual;
import ga.realcode.TRealNumberIndividual;
import ga.realcode.TUndxMgg;
import ga.realcode.TVector;

import java.io.*;
import java.util.List;

/**
 * Created by jiao on 2017/01/17.
 * GAを使って、本のexcelから抽出した一枚のファイル(一つタイプ)の人間の寝る時間を計算する
 * 例：20代女性平日
 */
public class evaluate {
//excelからcsvに変換したファイルを入力し、結果がoutfileに保存されます

    private static File file_in = new File("/Users/jiao/Desktop/国民生活時間調査/20代女性平日 ④時刻別行為者率（職業別、都市規模別ほか）.csv");
    private static File file_in2 = new File("/Users/jiao/Desktop/国民生活時間調査/20代女性②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.csv");
    private static File file = new File("/Users/jiao/Desktop/国民生活時間調査/test_out.csv");

    //個体数
    public static final int POPULATION_SIZE = 10000;
    //個体の長さ
    public static final int defaultGeneLength = 96;//24時間
    //扱うパラメータの数 今回は10000人と想定する
    public static final int NO_OF_PARAMETERS =10000;
    //睡眠のstart_timeを記録する行列
    private static int[] unit = new int[NO_OF_PARAMETERS];
    //unitから生成する睡眠時間の集計値
    private static int[] population = new int[defaultGeneLength];
    //国民生活時間調査から抽出した睡眠時間
    public static int [] standard_sleep_time=new int[defaultGeneLength];



    public static final int NO_OF_CROSSOVERS = 100;//交叉回数


    public static final double MIN = 0;//閾値
    public static final double MAX = 95;//閾値



    private static double map(double x)//変数の定義域へ写像する。
    {
        return (MAX - MIN) * x + MIN;
    }



    private static void evaluateIndividual(TRealNumberIndividual ind) {
        TVector v = ind.getVector();
        double evaluatonValue = 0.0;
        for (int i = 0; i < v.getDimension(); ++i) {
            double x = map(v.getData(i));
            if (x < MIN || x > MAX) {
                ind.setStatus(IIndividual.INVALID);
                return;
            }
            unit[i]=(int)Math.round(x);
            //System.out.println("individual"+unit[i]);
        }

        evaluatonValue=evaluateValue_cal(unit,file_in2,standard_sleep_time);
        //System.out.println(evaluatonValue);
        ind.setEvaluationValue(evaluatonValue);
        ind.setStatus(IIndividual.VALID);
    }



    public static void evaluatePopulation(List<TRealNumberIndividual> pop) {
        for (int i = 0; i < pop.size(); ++i) {
            evaluateIndividual(pop.get(i));
        }
    }


    //評価値を計算する
    static double evaluateValue_cal(int[] unit, File file_in2, int[] standard_sleep_time) {
        int[] population= initialize.creat(unit,file_in2);
        double evaluationValue = 0.0;
       // standard_sleep_time=standard_sleep_time(file_in);
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(standard_sleep_time[i]-population[i]))/NO_OF_PARAMETERS),2);
            //System.out.println(evaluationValue+"="+standard_sleep_time[i]+"X"+population[i]);

        }
        return evaluationValue;

    }

    //結果を書き出す
    public static void print_best_unit(TRealNumberIndividual ind)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + ind.getEvaluationValue());
            TVector v= ind.getVector();
            double x = 0;
            output.write("result");
            for(int i = 0; i<v.getDimension(); ++i) {
                x = map(v.getData(i));
                output.write("\n"+change((int)Math.round(x)));//四捨五入

                System.out.println("result"+i+" is "+change((int)Math.round(x)));

            }
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

//数字から時間への変換
    public static String change(int a){
        int HH=(int)a/4;
        int mm=a%4*15;
        String s=String.valueOf(HH)+":"+String.valueOf(mm);
        return s;
    }


    public static void main(String args[]){
    //public static void calculation(){

        standard_sleep_time=initialize.standard_sleep_time(file_in);
       /* for(int i=0;i<standard_sleep_time.length;i++){
            System.out.println(standard_sleep_time[i]);
        }*/

//GA
            TUndxMgg ga = new TUndxMgg(true,NO_OF_PARAMETERS,POPULATION_SIZE,NO_OF_CROSSOVERS);
            List<TRealNumberIndividual> initialPopulation = ga.getInitialPopulation();
            evaluatePopulation(initialPopulation);

           // for(int i =0; i<500; ++i)//500回を回す

         for(int i =0; ga.getBestEvaluationValue()>1; ++i)//回す　until評価値は１より小さい
            {
                List<TRealNumberIndividual> family = ga.selectParentsAndMakeKids();
                evaluatePopulation(family);
                List<TRealNumberIndividual> nextPop = ga.doSelectionForSurvival();
                System.out.println( ga.getIteration() + " " + ga.getBestEvaluationValue() + " " + ga.getAverageOfEvaluationValues());
            }

            System.out.println();
            System.out.println("Best individual");
            print_best_unit(ga.getBestIndividual());


            //  for(int i=0;i<Population.length;i++){
            //  System.out.println(unit[i]);
            //}




    }


}
