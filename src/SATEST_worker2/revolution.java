package SATEST_worker2;


import java.util.Random;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class revolution {

    //遺伝子の長さ 今回は10000人と想定する
    static  int NO_OF_PARAMETERS =100000;
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間



//move_timeとstudy＿periodを進化させる
    //original[NO_OF_PARAMETERS]
    public static int[] revolution_Individual(int[] original,double deviation) {//時間revolution
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

    public static int[] revolution_Individual2(int[] original) {
        int newIndividual[]= new int[NO_OF_PARAMETERS];//前後30分以内の任意値revolution　任意値はGaussian分布に従う　

        for(int i=0;i<original.length;i++) {
            int value = 0;
            Random random = new Random();
            int var=(int)Math.round(random.nextGaussian())*2;
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

    public static int[] revolution_Individual3(int[] original) {
        int newIndividual[]= new int[NO_OF_PARAMETERS];//前後30分以内の任意値revolution　任意値は一様分布に従う　

        for(int i=0;i<original.length;i++) {
            int value = 0;
            //Random random = new Random();
            int var=(int)Math.round(Math.random()*9);
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
