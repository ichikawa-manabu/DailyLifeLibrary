package GATEST;

import java.io.*;

/**
 * Created by jiao on 2017/01/18.
 */
public class FitnessCalc {
    //個体総数
    public static final int POPULATION_SIZE = 50;
    //遺伝子の長さ 今回は10000人と想定する
    static  int NO_OF_PARAMETERS =100000;
    //個体から生成された個体の長さ
    public static final int defaultGeneLength = 96;//24時間
    //睡眠時間
    public static int Sleep_period ;
    //回す回数
    public static int times=100 ;

    static  int[] solution = new int[NO_OF_PARAMETERS];

    public static void setSolution(File infile2, int sheet_num2) throws IOException {
        solution = Initialization.standard_sleep_time(infile2, sheet_num2);

    }


    //個体を評価する
    static double getFitness(Individual individual) throws IOException {
        int[] unit = new int[NO_OF_PARAMETERS];
        for (int i = 0; i < individual.size() ; i++) {
            unit[i]=individual.getGene(i);
        }
        //int Sleep_period= Initialization.sleep_period(infile1,sheet_num1);
        int[] population= Initialization.Population(unit,Sleep_period);
        double evaluationValue = 0.0;
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(solution[i]-population[i]))/NO_OF_PARAMETERS),2);
            //System.out.println(evaluationValue+"="+standard_sleep_time[i]+"X"+population[i]);

        }
        return evaluationValue;

    }

    //populationを評価する
    //適応値が一番小さい方のfitnessを記録する
    static double getBestFitness(Population population) throws IOException {
        Individual individual;
        double BestFitness=999999;
        for(int i=0;i<population.size();i++){
            individual= population.getIndividual(i);
            if(getFitness(individual)<BestFitness){
                BestFitness=getFitness(individual);
            }
        }

        return BestFitness;
    }

    //populationを評価する
    //適応値が一番小さい方のfitnessを記録する
    static Individual getBestIndividual(Population population) throws IOException {
        Individual individual;
        Individual Bestindividual= population.getIndividual(0);
        double BestFitness=999999;
        for(int i=0;i<population.size();i++){
            individual= population.getIndividual(i);
            if(getFitness(individual)<BestFitness){
                BestFitness=getFitness(individual);
                Bestindividual=individual;
            }
        }

        return Bestindividual;
    }

    //結果を書き出す
    public static void print_best_unit(Individual individual, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + individual.getFitness());

            double x = 0;
            output.write("result");
            for(int i = 0; i<individual.size(); ++i) {
                x = individual.getGene(i);
                output.write("\n"+change((int)Math.round(x)));//四捨五入

              //  System.out.println("result"+i+" is "+change((int)Math.round(x)));

            }
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //結果を書き出す
    public static void print_percentage(Individual individual, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + individual.getFitness());

            int[] recorder = new int[defaultGeneLength];
            int x = 0;
            output.write("time,percentage");
            for(int i = 0; i<individual.size(); ++i) {
                x = individual.getGene(i);
                recorder[x]=recorder[x]+1;
            }
            for(int j=0;j<defaultGeneLength;j++){
                output.write("\n"+change((int)Math.round(j))+","+(double)recorder[j]/NO_OF_PARAMETERS);//四捨五入
              //  System.out.println("percentage"+change((int)Math.round(j))+" is "+(double)recorder[j]/NO_OF_PARAMETERS);
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

    public static void ga_calculation(File infile1, int sheet_num1, File infile2, int sheet_num2, File outfile) throws IOException {

        Sleep_period= Initialization.sleep_period(infile1,sheet_num1);
        solution=Initialization.standard_sleep_time(infile2, sheet_num2);

        // 初期化
        Population myPop = new Population(POPULATION_SIZE, true);
        // リサイクル
        int generationCount = 0;
        while(generationCount<times){//500回
       // while (FitnessCalc.getBestIndividual(myPop).getFitness() > 1) { //fitnessは1以下ならbest individual
            generationCount++;
          //  System.out.println("Generation: " + generationCount + " Fittest: "
            //        + FitnessCalc.getBestIndividual(myPop).getFitness());
            myPop = Algorithm.evolvePopulation(myPop);
        }

       // System.out.println();
       // System.out.println("Best individual");
        //print_best_unit(FitnessCalc.getBestIndividual(myPop),outfile);
        print_percentage(FitnessCalc.getBestIndividual(myPop),outfile);

    }


    FitnessCalc(File inDir, File outDir, int sheet_num1, int sheet_num2 ) throws Exception {
        File  perform_time = new File(inDir.getPath() + "/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        File performer_percentage = new File(inDir.getPath() + "/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        String Name=Initialization.name(perform_time,sheet_num1);//何タイプの人の結果を記録した　例えば：成人男性、２０代女性どか
        System.out.println("Name"+Name);
        //File  outFile = new File(outDir.getPath() + "/"+Name+"sleep_start_time.csv");
        File  outFile = new File(outDir.getPath() + "/"+Name+"sleep_start_time_percentage.csv");
        if(!outFile.exists()) {
            outFile.createNewFile();
        }
        ga_calculation(perform_time,sheet_num1, performer_percentage,sheet_num2, outFile);
    }



}
