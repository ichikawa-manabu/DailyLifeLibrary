package study_SA;


import java.io.File;
import java.io.IOException;

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


    public static void main(String args[]) throws Exception {
        File infile1 = new File("/Users/jiao.xue/Desktop/国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        File infile2 = new File("/Users/jiao.xue/Desktop/国民生活時間調査/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        int sheet_num1 = 57;
        int sheet_num2 = 108;
        int start_time=34;//小学生授業は８：３０から
        int T=NO_OF_PARAMETERS;//小学生の平均移動時間
        double fitness;

        int[] BestPopulation = new int[defaultGeneLength];
        int[] Population = new int[defaultGeneLength];

        //初期値の生成
        int[] move_time_set  = new int[NO_OF_PARAMETERS];
        move_time_set=Initialization.move_period_set(infile1, sheet_num1);
        int deviation =Initialization.deviation(infile1, sheet_num1);
        solution = Initialization.standard_study_time(infile2, sheet_num2);

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
            System.out.println(" fitness : " + i + "is" + fitness);
        }

        for(int i=0;i<BestPopulation.length;i++){
            System.out.println(BestPopulation[i]);

        }
    }




}
