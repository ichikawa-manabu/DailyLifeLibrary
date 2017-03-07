package SA_sleep;

import java.io.*;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class Evaluation {
    //10万人を想定する
    static  int NO_OF_PARAMETERS =100000;
    //15分刻みで1日の長さ
    public static final int defaultGeneLength = 96;//24時間
    //平均睡眠時間
    public static int Sleep_period ;


    //10万人個々の人の睡眠時間の行列；　一人一人の睡眠時間〜N(平均睡眠時間,睡眠時間の標準偏差)
    public static int[] sleep_period ;


    //回す回数
    public static int times=5000 ;

    //比較対象(時刻別睡眠行為の割合)
    static  int[] solution = new int[defaultGeneLength];

    //睡眠開始時刻の割合
    static  int[] initial_seed = new int[NO_OF_PARAMETERS];

    //評価関数
    //unit:個々の人の睡眠開始時刻の行列
    //sleep_period:個々の人の睡眠時間
    static double getFitness(int unit[], int sleep_period[]) throws IOException {
        int[] population= Initialization.Population2(unit,sleep_period);
        double evaluationValue = 0.0;
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(solution[i]-population[i]))/NO_OF_PARAMETERS),2);
           // System.out.println(solution[i]+" and "+population[i]);

        }
        return evaluationValue;

    }

    //二つの個体（個々の人の睡眠開始時刻）を比較する
    //返し値：評価値低い個体
    public static int[] compare(int[] individual1,int[] individual2,int[] sleep_period1,int[] sleep_period2) throws IOException {
        double comparation =getFitness(individual2,sleep_period2)-getFitness(individual1,sleep_period1);
        if(comparation<0) {
            return individual2;
        }
        else {
            //Tを平均睡眠時間と設定する
           if(Math.random()> Math.exp(-comparation / Sleep_period)){
                return individual2;
            }
        }

        return individual1;
    }


    //結果(個体)を書き出す
    public static void print_best_unit(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + getFitness(best_seed,sleep_period));

            double x = 0;
            output.write("result");
            for(int i = 0; i<best_seed.length; ++i) {
                x = best_seed[i];
                output.write("\n"+change((int)Math.round(x)));//四捨五入

                //System.out.println("result"+i+" is "+change((int)Math.round(x)));

            }
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //結果(割合)を書き出す
    public static void print_percentage(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する
            int[] recorder = new int[defaultGeneLength];
            int x = 0;
            output.write("time,percentage");
            for(int i = 0; i<best_seed.length;++i) {
                x = best_seed[i];
                recorder[x]=recorder[x]+1;
            }
            for(int j=0;j<defaultGeneLength;j++){
                output.write("\n"+change((int)Math.round(j))+","+(double)recorder[j]/NO_OF_PARAMETERS);//四捨五入
                //System.out.println("percentage"+change((int)Math.round(j))+" is "+(double)recorder[j]/NO_OF_PARAMETERS);
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




   // public static void main(String args[]) throws Exception {
        public static void sa_calculation(File infile1, int sheet_num1, File infile2, int sheet_num2, File outfile1, File outfile2) throws IOException {

       //// File infile1 = new File("/Users/jiao.xue/Desktop/国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        ////File infile2 = new File("/Users/jiao.xue/Desktop/国民生活時間調査/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        ////int sheet_num1 = 6;
        ////int sheet_num2 = 7;

        String Name = Initialization.name(infile1, sheet_num1);//何タイプの人の結果を記録した　例えば：成人男性、２０代女性どか


            ///////////////////////////////////////////////////////////////////////////////
            //各々の人の睡眠時間の計算
        sleep_period = Initialization.sleep_period_set(infile1, sheet_num1);

            int[] best_sleep_period=sleep_period ;

            Sleep_period= Initialization.sleep_period(infile1, sheet_num1);//平均睡眠時間
        solution = Initialization.standard_sleep_time(infile2, sheet_num2);



         //最初の個体
        initial_seed= Individual.generateIndividual();
            //評価値
        double fitness=getFitness(initial_seed,best_sleep_period);
        //System.out.println("original fitness : " + fitness);

        int[] best_seed = initial_seed;
        int[] new_seed = new int[NO_OF_PARAMETERS];

        for(int i=0;i<times;i++) {
            new_seed = revolution.revolution_Individual(best_seed);
            int[] new_period = revolution.revolution_Individual(best_sleep_period);
            best_seed = compare(best_seed, new_seed,best_sleep_period,new_period);
           if(best_seed==new_seed){
               best_sleep_period=new_period;
            }
            fitness = getFitness(best_seed,best_sleep_period);
            System.out.println(" fitness : " + i +"is" + fitness);
        }
            sleep_period=best_sleep_period;
            print_best_unit(best_seed,outfile1);
            print_percentage(best_seed,outfile2);
            int[] best_result= Initialization.Population2(best_seed,sleep_period);
            for(int i = 0; i<best_result.length; ++i) {
            System.out.println((double)best_result[i]/NO_OF_PARAMETERS*100);
            }

    }


    Evaluation(File inDir, File outDir, int sheet_num1, int sheet_num2 ) throws Exception {
        File  perform_time = new File(inDir.getPath() + "/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        File performer_percentage = new File(inDir.getPath() + "/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        String Name= Initialization.name(perform_time,sheet_num1);//何タイプの人の結果を記録した　例えば：成人男性、２０代女性どか
        System.out.println("Name"+Name);
        File  outFile1 = new File(outDir.getPath() + "/"+Name+"sleep_start_time.csv");
        File  outFile2 = new File(outDir.getPath() + "/"+Name+"sleep_start_time_percentage.csv");
        if(!outFile1.exists()) {
            outFile1.createNewFile();
        }
        sa_calculation(perform_time,sheet_num1, performer_percentage,sheet_num2, outFile1,outFile2);
    }


}
