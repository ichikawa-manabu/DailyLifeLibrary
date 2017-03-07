package SA_sleep;


import java.util.Random;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class revolution {

    //10万人を想定する
    static  int NO_OF_PARAMETERS =100000;
    //15分刻みで1日の長さ
    public static int defaultGeneLength = 96;//24時間

    //睡眠開始時刻の探索範囲(今の時刻の前後1時間（一様分布）)
    public static int[] revolution_Individual(int[] original) {
        int newIndividual[]= new int[NO_OF_PARAMETERS];
        for(int i=0;i<original.length;i++) {
            int value = 0;
            if (Math.random() <= 0.5) {
                value = (original[i] + (int) (Math.random() * 5)) % defaultGeneLength;
            } else {
                value = (original[i] - (int) (Math.random() * 5)+defaultGeneLength) % defaultGeneLength;
            }
            newIndividual[i]=value;
          //  System.out.println("new_value"+i+"is"+value);
        }

      return newIndividual;
    }

    //睡眠時間の探索範囲(今の睡眠時間の前後15min（Gaussian 分布）)
    public static int[] revolution_period(int[] original) {
        int newIndividual[]= new int[NO_OF_PARAMETERS];

        for(int i=0;i<original.length;i++) {
            int value = 0;
            Random random = new Random();
            int var=(int)Math.round(random.nextGaussian()) ;
            //  System.out.println("var"+i+"is"+var);

            //if (Math.random() <= 0.5) {
            value = (original[i] + var)% defaultGeneLength;
            if(value<=0){
                value=(value+defaultGeneLength)% defaultGeneLength;
            }

            newIndividual[i]=value;

        }

        return newIndividual;
    }
    }
