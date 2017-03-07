package SATEST_student;

import java.io.*;

/**
 * Created by jiao on 2017/01/29.
 */
public class Evaluation {
    //10万人をと想定する
    public static int defaultGeneLength = 96;//24時間
    //15分刻みで1日の長さ
    public static final int NO_OF_PARAMETERS =100000;

    //回す回数
    public static int times=1000 ;

    //時刻別学内活動の割合
    static  int[] solution = new int[defaultGeneLength];

    public static int start_time;//初期値: 小学生授業開始時刻を８：３０と設定する



    //初期個体
    static  int[] initial_seed = new int[NO_OF_PARAMETERS];

    //個体を評価する
    static double getFitness(int population[]) throws IOException {
        //int[] population= study_start_time.Initialization.Population(unit,Sleep_period);
        double evaluationValue = 0.0;
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(solution[i]-population[i]))/NO_OF_PARAMETERS),2);
            // System.out.println(solution[i]+" and "+population[i]);

        }
        return evaluationValue;

    }

    //二つの個体を比較する　評価値小さい方を返す
    public static int[] compare(int[] individual1,int[] individual2,  double T)throws IOException {
        double comparation =getFitness(individual2)-getFitness(individual1);
        if(comparation<0) {
            return individual2;
        }
        else {
            if(Math.random()> Math.exp(-comparation / T)){
                return individual2;
            }
        }
        return individual1;
    }



    //結果を書き出す(10万人の行動時刻)
    public static void print_best_unit(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + getFitness(best_seed));

            double x = 0;
            output.write("result");
            for(int i = 0; i<best_seed.length; ++i) {
                x = start_time-best_seed[i];
                output.write("\n"+change((int)Math.round(x)));//四捨五入

                //System.out.println("result"+i+" is "+change((int)Math.round(x)));

            }
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //結果を書き出す(10万人時刻別行動を行った割合)
    public static void print_percentage(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する
            int[] recorder = new int[defaultGeneLength];
            int x = 0;
            output.write("time,percentage");
            for(int i = 0; i<best_seed.length;++i) {
                x = start_time-best_seed[i];
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


    //帰り時間結果を書き出す
    public static void print_best_endunit(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

            System.out.println("Evaluation value: " + getFitness(best_seed));

            double x = 0;
            output.write("result");
            for(int i = 0; i<best_seed.length; ++i) {
                x = (best_seed[i]+defaultGeneLength)%defaultGeneLength;
                output.write("\n"+change((int)Math.round(x)));//四捨五入

                //System.out.println("result"+i+" is "+change((int)Math.round(x)));

            }
            output.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //結果を書き出す
    public static void print_end_percentage(int [] best_seed, File outfile)  {

        try{
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する
            int[] recorder = new int[defaultGeneLength];
            int x = 0;
            output.write("time,percentage");
            for(int i = 0; i<best_seed.length;++i) {
                x = (best_seed[i]+defaultGeneLength)%defaultGeneLength;
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

   // public static void main(String args[]) throws Exception {
   public static void sa_calculation(File infile1, int sheet_num1, File infile2, int sheet_num2, File out, File out2, File out3, File out4, File out5, File out6) throws Exception {
       // File infile1 = new File("/Users/jiao/Desktop/国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        //File infile2 = new File("/Users/jiao/Desktop/国民生活時間調査/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        //File out = new File("/Users/jiao/Desktop/国民生活時間調査/小学生学校行く時間.csv");
        //File out2 = new File("/Users/jiao/Desktop/国民生活時間調査/小学生学校から帰る時間.csv");
        //File out3 = new File("/Users/jiao/Desktop/国民生活時間調査/小学生学校から家に着く時間.csv");

        //int sheet_num1 = 57;
        //int sheet_num2 = 108;
        start_time = 34;//小学生授業は８：３０から
        int T = NO_OF_PARAMETERS;//小学生の平均移動時間
        double fitness;

        int[] BestPopulation = new int[defaultGeneLength];
        int[] Population = new int[defaultGeneLength];


        //初期値の生成
        int[] move_time_set  = new int[NO_OF_PARAMETERS];
        move_time_set=Initialization.move_period_set(infile1, sheet_num1);
        int deviation =Initialization.move_deviation(infile1, sheet_num1);
        solution = Initialization.standard_study_time(infile2, sheet_num2);
        for(int i=(int)solution.length/2;i<solution.length;i++){
            solution[i]=0;
        }

        int[] new_move_time=new int[NO_OF_PARAMETERS];
        int[] best_move_time=move_time_set;

        for(int i=0;i<times;i++) {
            BestPopulation = Initialization.generateStartPopulation(best_move_time, start_time);
           new_move_time = revolution.revolution_Individual(best_move_time,deviation);

            Population = Initialization.generateStartPopulation(new_move_time, start_time);
            //System.out.println(" best : " + getFitness(BestPopulation)+ "new  " + getFitness(Population));

            BestPopulation = compare(BestPopulation, Population, T);
            if( BestPopulation==Population){
                best_move_time=new_move_time;
            }
            fitness = getFitness(BestPopulation);
            //System.out.println(" fitness : " + i + "is" + fitness);
        }

        print_percentage(best_move_time,out);
       print_best_unit(best_move_time,out4);

      /*  for(int i=0;i<BestPopulation.length;i++){
            System.out.println(BestPopulation[i]);

        }*/
        ////////////////////////////////////////
        //初期値の生成
        int[] end_time_set  = new int[NO_OF_PARAMETERS];
        end_time_set=Initialization.study_end_time(infile1, sheet_num1,start_time);//帰り時間とstudy＿period
        //int deviation2 =Initialization.study_deviation(infile1, sheet_num1);//学内いる時間のdeviation
        solution = Initialization.standard_study_time(infile2, sheet_num2);
        for(int i=0;i<solution.length/2;i++){
            solution[i]=0;
        }

        int[] new_end_time=new int[NO_OF_PARAMETERS];
        int[] best_end_time=end_time_set;



        int[] back_time=new int[defaultGeneLength];
        int[] best_back_time=new int[defaultGeneLength];

        for(int i=0;i<times;i++) {
            best_back_time=Initialization.generateEndPopulation(best_end_time,best_move_time);
            //System.out.println("deviation2 "+deviation2);

            new_end_time = revolution.revolution_Individual2(best_end_time);

            back_time = Initialization.generateEndPopulation(new_end_time, best_move_time);

            best_back_time = compare(best_back_time, back_time, T);
            if( best_back_time==back_time){
                best_end_time=new_end_time;
            }
            fitness = getFitness(best_back_time);
           // System.out.println(" fitness : " + i + "is" + fitness);

        }

        print_end_percentage(best_end_time,out2);
       print_best_endunit(best_end_time,out5);

        int[] arrive_time=new int[NO_OF_PARAMETERS];
        for(int i=0;i<arrive_time.length;i++){
            arrive_time[i]=best_end_time[i]+best_move_time[i];

        }
        print_end_percentage(arrive_time,out3);
       print_best_endunit(best_end_time,out6);

       /* for(int i=0;i<best_back_time.length;i++){
            System.out.println(best_back_time[i]);

        }*/

    }

    Evaluation(File inDir, File outDir, int sheet_num1, int sheet_num2 ) throws Exception {
        File  perform_time = new File(inDir.getPath() + "/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        File performer_percentage = new File(inDir.getPath() + "/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        String Name=Initialization.name(perform_time,sheet_num1);//何タイプの人の結果を記録した　例えば：成人男性、２０代女性どか
        System.out.println("Name"+Name);
        File  outFile1 = new File(outDir.getPath() + "/"+Name+"学校行く時刻percentage.csv");
        File  outFile2 = new File(outDir.getPath() + "/"+Name+"学校から帰る時刻percentage.csv");
        File  outFile3 = new File(outDir.getPath() + "/"+Name+"学校から家に着く時刻percentage.csv");
        File  outFile4 = new File(outDir.getPath() + "/"+Name+"学校行く時刻.csv");
        File  outFile5 = new File(outDir.getPath() + "/"+Name+"学校から帰る時刻.csv");
        File  outFile6 = new File(outDir.getPath() + "/"+Name+"学校から家に着く時刻.csv");
        if(!outFile1.exists()) {
            outFile1.createNewFile();
        }
        sa_calculation(perform_time,sheet_num1, performer_percentage,sheet_num2, outFile1,outFile2,outFile3,outFile4,outFile5,outFile6);
    }

}
