package SATEST_student;


import java.util.Random;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class revolution {

    //10万人を想定する
    static  int NO_OF_PARAMETERS =100000;
    //15分刻みで1日の長さ
    public static int defaultGeneLength = 96;//24時間




//通学時間を新しい値であげる 通学時間〜 N(average,deviation)
    public static int[] revolution_Individual(int[] original,double deviation) {
        int newIndividual[]= new int[NO_OF_PARAMETERS];

        for(int i=0;i<original.length;i++) {
            int value = 0;
            Random random = new Random();
            int var=(int)Math.round(deviation * random.nextGaussian()) ;
            //  System.out.println("var"+i+"is"+var);

            if (Math.random() <= 0.5) {
                value = (original[i] + var) % defaultGeneLength;
            } else {
                value = (original[i] - var) % defaultGeneLength;
                if(value<0){//移動時間を０にする
                    value=0;

                }
            }
            newIndividual[i]=value;
           // System.out.println("new_value"+i+"is"+value);
        }

      return newIndividual;
    }

    //探索範囲(今の時刻の前後15min（Gaussian分布）)

    public static int[] revolution_Individual2(int[] original) {
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
