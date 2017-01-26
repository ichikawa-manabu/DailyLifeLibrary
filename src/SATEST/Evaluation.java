package SATEST;

import java.io.*;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class Evaluation {
    //遺伝子の長さ 今回は10000人と想定する
    static  int NO_OF_PARAMETERS =100000;
    //個体から生成された個体の長さ
    public static final int defaultGeneLength = 96;//24時間
    //睡眠時間
    public static int Sleep_period ;
    //回す回数
    public static int times=300 ;

    //答え
    static  int[] solution = new int[defaultGeneLength];

    //初期値
    static  int[] initial_seed = new int[NO_OF_PARAMETERS];

    //個体を評価する
    static double getFitness(int unit[]) throws IOException {
        int[] population= Initialization.Population(unit,Sleep_period);
        double evaluationValue = 0.0;
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(solution[i]-population[i]))/NO_OF_PARAMETERS),2);
           // System.out.println(solution[i]+" and "+population[i]);

        }
        return evaluationValue;

    }

    //二つの個体を比較すする
    public static int[] compare(int[] individual1,int[] individual2) throws IOException {
        double comparation =getFitness(individual2)-getFitness(individual1);
        if(comparation<0) {
            return individual2;
        }
        else {
           if(Math.random()> Math.exp(-comparation / Sleep_period)){
                return individual2;
            }
        }

        return individual1;
    }


    //結果を書き出す
    public static void print_best_unit(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + getFitness(best_seed));

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

    //結果を書き出す
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
                System.out.println("percentage"+change((int)Math.round(j))+" is "+(double)recorder[j]/NO_OF_PARAMETERS);
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
        System.out.println("Name" + Name);


        Sleep_period = Initialization.sleep_period(infile1, sheet_num1);
        solution = Initialization.standard_sleep_time(infile2, sheet_num2);
        //System.out.println("Sleep_period"+Sleep_period);
        //for(int i=0;i<solution.length;i++) {
          //  System.out.println("solution" + i+ ":  "+ solution[i]);
        //}


        initial_seed=Individual.generateIndividual();
        double fitness=getFitness(initial_seed);
        System.out.println("original fitness : " + fitness);

        int[] best_seed = initial_seed;
        int[] new_seed = new int[NO_OF_PARAMETERS];

        for(int i=0;i<times;i++) {
            new_seed = revolution.revolution_Individual(best_seed);
            best_seed = compare(best_seed, new_seed);
            fitness = getFitness(best_seed);
            System.out.println(" fitness : " + i +"is" + fitness);
        }

            print_best_unit(best_seed,outfile1);
            print_percentage(best_seed,outfile2);


    }


    Evaluation(File inDir, File outDir, int sheet_num1, int sheet_num2 ) throws Exception {
        File  perform_time = new File(inDir.getPath() + "/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        File performer_percentage = new File(inDir.getPath() + "/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        String Name=Initialization.name(perform_time,sheet_num1);//何タイプの人の結果を記録した　例えば：成人男性、２０代女性どか
        System.out.println("Name"+Name);
        File  outFile1 = new File(outDir.getPath() + "/"+Name+"sleep_start_time.csv");
        File  outFile2 = new File(outDir.getPath() + "/"+Name+"sleep_start_time_percentage.csv");
        if(!outFile1.exists()) {
            outFile1.createNewFile();
        }
        sa_calculation(perform_time,sheet_num1, performer_percentage,sheet_num2, outFile1,outFile2);
    }


}
