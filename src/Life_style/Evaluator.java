package Life_style;

import ga.core.IIndividual;
import ga.realcode.TRealNumberIndividual;
import ga.realcode.TUndxMgg;
import ga.realcode.TVector;

import java.io.*;
import java.util.List;

/**
 * Created by jiao on 2017/01/18.
 */
public class Evaluator {

    //個体数
    public static final int POPULATION_SIZE = 30;
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
    //睡眠時間
     private static  int Sleep_period;

    public static final int NO_OF_CROSSOVERS = 100;//交叉回数


    public static final double MIN = 0;//閾値
    public static final double MAX = 95;//閾値



    private static double map(double x)//変数の定義域へ写像する。
    {
        return (MAX - MIN) * x + MIN;
    }


    //評価値を計算する
    static double evaluateValue_cal(int[] unit, int Sleep_period) throws IOException {
        int[] population= Initialization.Population(unit,Sleep_period);
        double evaluationValue = 0.0;
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(standard_sleep_time[i]-population[i]))/NO_OF_PARAMETERS),2);
            //System.out.println(evaluationValue+"="+standard_sleep_time[i]+"X"+population[i]);

        }
        return evaluationValue;

    }

    //個体を評価
    private static void evaluateIndividual(TRealNumberIndividual ind) throws IOException {
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

        evaluatonValue=evaluateValue_cal(unit,Sleep_period);
        //System.out.println(evaluatonValue);
        ind.setEvaluationValue(evaluatonValue);
        ind.setStatus(IIndividual.VALID);
    }

    //populationを評価

    public static void evaluatePopulation(List<TRealNumberIndividual> pop) throws IOException {
        for (int i = 0; i < pop.size(); ++i) {
            evaluateIndividual(pop.get(i));
        }
    }




    //結果を書き出す
    public static void print_best_unit(TRealNumberIndividual ind, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

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


    //infile1: 国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls"
   //infile2: 国民生活時間調査/④時刻別行為者率（職業別、都市規模別ほか）.xls")
    //sheet_num1:infile1の中調べたいsheetの番号
    //sheet_num２:infile２の中調べたいsheetの番号　　＃注意：sheet_num1とsheet_num２不一致かも
    //outfile:出力結果
    public static void ga_calculation(File infile1, int sheet_num1, File infile2, int sheet_num2, File outfile) throws IOException {

        int Sleep_period= Initialization.sleep_period(infile1,sheet_num1);
        standard_sleep_time=Initialization.standard_sleep_time(infile2, sheet_num2);
       /* for(int i=0; i<standard_sleep_time.length;i++){
            System.out.println(standard_sleep_time[i]);
        }*/

//GA
        TUndxMgg ga=  new TUndxMgg(true,NO_OF_PARAMETERS,POPULATION_SIZE,NO_OF_CROSSOVERS);
        //   do{
        ga = new TUndxMgg(true,NO_OF_PARAMETERS,POPULATION_SIZE,NO_OF_CROSSOVERS);

        List<TRealNumberIndividual> initialPopulation = ga.getInitialPopulation();
        evaluatePopulation(initialPopulation);
        System.out.println(  ga.getBestEvaluationValue());
        //   } while(ga.getBestEvaluationValue()>10);


        for(int i =0; i<500; ++i)//500回を回す

        //for(int i =0; ga.getBestEvaluationValue()>1; ++i)//回す　until評価値は１より小さい
        {
            List<TRealNumberIndividual> family = ga.selectParentsAndMakeKids();
            evaluatePopulation(family);
            List<TRealNumberIndividual> nextPop = ga.doSelectionForSurvival();
            System.out.println( ga.getIteration() + " " + ga.getBestEvaluationValue() + " " + ga.getAverageOfEvaluationValues());
        }

        System.out.println();
        System.out.println("Best individual");
        print_best_unit(ga.getBestIndividual(),outfile);
    }



    //inDir:国民生活時間調査のフォルダ
    //outDir:出力フォルダ
    //sheet_num1:②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls"の中調べたいsheetの番号
    //sheet_num２:④時刻別行為者率（職業別、都市規模別ほか）.xls"　　＃注意：sheet_num1とsheet_num２不一致かも

    Evaluator(File inDir, File outDir, int sheet_num1, int sheet_num2 ) throws Exception {
      File  perform_time = new File(inDir.getPath() + "/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
      File performer_percentage = new File(inDir.getPath() + "/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        String Name=Initialization.name(perform_time,sheet_num1);//何タイプの人の結果を記録した　例えば：成人男性、２０代女性どか
        System.out.println("Name"+Name);
        File  outFile = new File(outDir.getPath() + "/"+Name+"sleep_start_time.csv");
        if(!outFile.exists()) {
            outFile.createNewFile();
        }
        ga_calculation(perform_time,sheet_num1, performer_percentage,sheet_num2, outFile);
    }

}
