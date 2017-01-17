package test_population;

import ga.core.IIndividual;
import ga.realcode.TRealNumberIndividual;
import ga.realcode.TVector;

import java.io.*;
import java.util.List;


/**
 * Created by jiao.xue on 2017/01/16.
 */
public class evaluator {

    //個体数
    public static int populationSize = 10000;
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間
    public static final double MIN = 0;//閾値
    public static final double MAX = 95;//閾値
    //個体が持つ睡眠時間
   // private static int sleep_time; //７時間15分
    //国民生活時間調査から抽出した睡眠時間
    public static int [] standard_sleep_time=new int[defaultGeneLength];
    //睡眠のstart_timeを記録する行列
    public static int[] unit = new int[populationSize];
    //unitから生成する睡眠時間の集計値
    private static int[] Population = new int[defaultGeneLength];

    private static double map(double x)//変数の定義域へ写像する。
    {
        return (MAX - MIN) * x + MIN;
    }

   public static File file_in = new File("/Users/jiao/Desktop/国民生活時間調査/20代女性平日 ④時刻別行為者率（職業別、都市規模別ほか）.csv");
    public static File file_in2 = new File("/Users/jiao/Desktop/国民生活時間調査/20代女性②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.csv");
    static File file = new File("/Users/jiao/Desktop/国民生活時間調査/test_out.csv");



       public static void main(String args[]) throws FileNotFoundException {
//test

           try {

               BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Shift_JIS"));
               String line = "";
               //結果を評価する
               int i=0;
               line=input.readLine();
               while ((line=input.readLine()) != null) {
                   String[] s = line.split(",");
                   unit[i]= Integer.parseInt(s[0]);
                   System.out.println(unit[i]);
                   i++;

               }

           Population=Initialization.creat(unit,file_in2);
           for( i=0;i<Population.length;i++){
               System.out.println("population"+i+" is "+Population[i]);
           }
        //   double evaluationValue = evaluateValue_cal(unit);
          // System.out.println(evaluationValue);

           }
           catch (IOException e) {
               e.printStackTrace();
           }


       }






    static double evaluateValue_cal(int[] unit) {
        //評価値を計算する
        int[] population= Initialization.creat(unit,file_in2);
        double evaluationValue = 0.0;
        standard_sleep_time=standard_sleep_time(file_in);
        for(int i=0;i<defaultGeneLength;i++){
            evaluationValue+=Math.pow((((double)(standard_sleep_time[i]-population[i]))/populationSize),2);
            //System.out.println(evaluationValue+"="+standard_sleep_time[i]+"X"+population[i]);

        }
        return evaluationValue;

    }



    private static void evaluateIndividual(TRealNumberIndividual ind) {
        TVector v = ind.getVector();
        double evaluatonValue = 0.0;
        for (int i = 0; i < v.getDimension(); ++i) {
            double x = map(v.getData(i));

            //System.out.println(x);
            if (x < MIN || x > MAX) {
                ind.setStatus(IIndividual.INVALID);
                return;
            }
            unit[i]=(int)Math.round(x);
            //System.out.println("X"+unit[i]);
        }
        int[] population= new int[defaultGeneLength];
        population=Initialization.creat(unit,file_in2);//
        for(int i=0;i<population.length;i++){
            System.out.println("population"+i+" is "+population[i]);
        }

        evaluateValue_cal(unit);
        ind.setEvaluationValue(evaluatonValue);
        ind.setStatus(IIndividual.VALID);
    }

    /*
        private static void evaluateIndividual(TRealNumberIndividual ind) {
        TVector v = ind.getVector();
        double evaluationValue = 0.0;

        for (int i = 0; i < v.getDimension(); ++i) {
            double x = map(v.getData(i));
            if (x < MIN || x > MAX) {
                ind.setStatus(IIndividual.INVALID);
                return;
            }
        }

       // Population=Initialization.creat(unit,file_in2);

        evaluationValue = evaluateValue_cal(unit);

        ind.setEvaluationValue(evaluationValue);
        ind.setStatus(IIndividual.VALID);
    }
*/

       public static void evaluatePopulation(List<TRealNumberIndividual> pop) {
        for (int i = 0; i < pop.size(); ++i) {
            evaluateIndividual(pop.get(i));
        }
     }


    public static void print_best_unit(TRealNumberIndividual ind)  {

try{
        PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), "Shift_JIS"));//結合した結果を新しいファイル'out'に保存する

        System.out.println("Evaluation value: " + ind.getEvaluationValue());
        TVector v= ind.getVector();
        double x = 0;
    output.write("result");
    for(int i = 0; i<v.getDimension(); ++i) {
            x = map(v.getData(i));
        output.write("\n"+x);
            System.out.println(x);

        }
    output.close();
        }catch (IOException e) {
    e.printStackTrace();
}
    }



    public static int[] standard_sleep_time(File file_in) {
           //file_in は国民生活時間調査の[時刻別行為者率（職業別、都市規模別ほか）.xls]ファイル
           //返し値:国民生活時間調査から抽出した睡眠時間
           try {

               BufferedReader input1 = new BufferedReader(new InputStreamReader(new FileInputStream(file_in), "Shift_JIS"));
                   String line = "";
               //睡眠時間の抽出
                int count=0;
                   while ((line=input1.readLine()) != null) {
                       count++;
                      // if (s[0].equals("睡　　　　　　 眠")) {
                       if (count==6) { //睡眠は第6行目に記録されてます
                           String[] s = line.split(",");
                           for(int i=0;i<defaultGeneLength && (i+2)<s.length;i++) {
                               int j = i + 2;
                               standard_sleep_time[i] = (int)(Double.parseDouble(s[j])*populationSize/100);//単位を統一
                               //System.out.println(standard_sleep_time[i]);
                           }
                       }
                       if(count>6){
                           break;
                       }
                   }

             return standard_sleep_time;
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();

           }
           catch (IOException e) {
                   e.printStackTrace();
               }
           return new int[0];
       }

}
