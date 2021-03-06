package SATEST;


import java.util.Random;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class revolution {

    //遺伝子の長さ 今回は10000人と想定する
    static  int NO_OF_PARAMETERS =100000;
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間

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
