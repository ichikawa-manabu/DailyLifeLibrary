package study_GA;


import java.io.*;

/**
 * Created by jiao.xue on 2017/01/27.
 *
 * 評価関数
 */
public class evaluation {
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間
    //扱うパラメータの数 今回は10000人と想定する
    public static final int NO_OF_PARAMETERS =1000;

    //回す回数
    public static int times=1000 ;

    //答え
    static  int[] solution = new int[defaultGeneLength];

    public static int start_time;//小学生授業は８：３０から



    //初期値
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

    //二つの個体を比較すする
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



    //結果を書き出す
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

    //結果を書き出す
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


    public static void main(String args[]) throws Exception {
        File infile1 = new File("/Users/jiao.xue/Desktop/国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        File infile2 = new File("/Users/jiao.xue/Desktop/国民生活時間調査/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        File out = new File("/Users/jiao.xue/Desktop/国民生活時間調査/小学生学校start時間.csv");

        int sheet_num1 = 57;
        int sheet_num2 = 108;
        start_time=34;//小学生授業は８：３０から
        int T=NO_OF_PARAMETERS;//小学生の平均移動時間
        double fitness;

        int[] BestPopulation = new int[defaultGeneLength];
        int[] Population = new int[defaultGeneLength];

        //初期値の生成
        int[] move_time_set  = new int[NO_OF_PARAMETERS];
        move_time_set=Initialization.move_period_set(infile1, sheet_num1);
        int deviation =Initialization.deviation(infile1, sheet_num1);
        solution = Initialization.standard_study_time(infile2, sheet_num2);
        for(int i=(int)solution.length/2;i<solution.length;i++){
            solution[i]=0;
        }

        int[] new_move_time=new int[NO_OF_PARAMETERS];
        int[] best_move_time=move_time_set;

        for(int i=0;i<times;i++) {
            BestPopulation = Initialization.Student_Population(best_move_time, start_time);
            new_move_time = revolution.revolution_Individual(best_move_time,deviation);

            Population = Initialization.Student_Population(new_move_time, start_time);
            //System.out.println(" best : " + getFitness(BestPopulation)+ "new  " + getFitness(Population));

            BestPopulation = compare(BestPopulation, Population, T);
            if( BestPopulation==Population){
                best_move_time=new_move_time;
            }
            fitness = getFitness(BestPopulation);
            ////System.out.println(" fitness : " + i + "is" + fitness);
        }

        print_percentage(best_move_time,out);


////////////////////////////////////////////////////////////////
        int[] end_time_set  = new int[NO_OF_PARAMETERS];
        end_time_set=Initialization.study_end_time(infile1, sheet_num1,start_time);//帰り時間とstudy＿period


        int deviation2 =Initialization.deviation_study_period(infile1, sheet_num1);//学内いる時間のdeviation
        solution = Initialization.standard_study_time(infile2, sheet_num2);
        for(int i=0;i<solution.length/2;i++){
            solution[i]=0;
        }

        int[] new_end_time=new int[NO_OF_PARAMETERS];
        int[] best_end_time=end_time_set;

        int[] back_time=new int[defaultGeneLength];
        int[] best_back_time=new int[defaultGeneLength];


        for(int i=0;i<times;i++) {
            best_back_time=Initialization.end_Population(best_end_time,best_move_time);
            System.out.println("deviation2 "+deviation2);

        new_end_time = revolution.revolution_Individual2(best_end_time,deviation2);
            ////////////////////////////////////
            for(int j=0;j< new_end_time.length;j++){
                if( new_end_time[j]<=0){
                    System.out.println( "tt" +new_end_time[j]);}

            }
////////////////////////
            back_time = Initialization.end_Population(new_move_time, best_move_time);

            best_back_time = compare(best_back_time, back_time, T);
            if( best_back_time==back_time){
                best_end_time=new_end_time;
            }
            fitness = getFitness(best_back_time);
            System.out.println(" fitness : " + i + "is" + fitness);

        }



       // fitness = getFitness(BestPopulation);


        for(int i=0;i< best_back_time.length;i++){
            System.out.println(  best_back_time[i]);

        }







       /* for(int i=0;i<BestPopulation.length;i++){
            System.out.println(BestPopulation[i]);

        }*/
    }




}
