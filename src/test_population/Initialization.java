package test_population;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jiao on 2017/01/15.
 */
public class Initialization {

    //個体数
    public static int populationSize = 10000;
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間
    //個体が持つ行列
    private static byte[] genes = new byte[defaultGeneLength];
    //全ての個体が持つ行列の足し算
    private static int[] Population = new int[defaultGeneLength];
    //個体が持つ睡眠時間
    private static int sleep_time; //例：７時間15分
    //睡眠のstart_timeを記録する行列
    public static int[] unit = new int[populationSize];

    // 最初個体を作る
    public static void generateIndividual(int start_time) {
        //int start_time= (int)(Math.random()*defaultGeneLength);
        int i=0;
        while(i< sleep_time){
            genes[start_time] = 1;
            start_time=(start_time+1)%defaultGeneLength;
            i++;
        }

    }



    public static void Population(int[] unit,int populationSize) {
        //全ての個体が持つ行列の足し算、GAの種を作る
        for(int i=0;i<populationSize;i++){
            int start_time= unit[i];
            generateIndividual(unit[i]);
            for(int j=0;j<genes.length;j++) {
                int t=(int)genes[j];
                //System.out.println(t);
                Population[j] = (int)genes[j] + Population[j];
                genes[j]=0;
            }

        }
    }

    public static int[] creat(int[] unit, File file_in) {
        //各人のstart_timeから全ての人の寝る時間の集計
        sleep_time= Initialization.sleep_period(file_in);
        Population(unit,populationSize);
        return Population;

    }


    public static int sleep_period(File file_in) {
        //file_in は国民生活時間調査の[②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.csv]ファイル
        //返し値:国民生活時間調査から抽出した睡眠期間
        try {

            BufferedReader input1 = new BufferedReader(new InputStreamReader(new FileInputStream(file_in), "Shift_JIS"));
            String line = "";
            //睡眠時間の抽出
            int count=0;
            while ((line=input1.readLine()) != null) {
                count++;
                // if (s[0].equals("睡　　　　　　 眠")) {
                if (count==25) { //睡眠は第6行目に記録されてます
                    String[] s = line.split(",");
                    //String time=s[3];
                    //for(int i=0;i<time.length();i++){
                      //  int
                    //}
                    Calendar cal= Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                    SimpleDateFormat sp= new SimpleDateFormat("HH:mm");
                    sp.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date dt=sp.parse(s[3]);
                    cal.setTime(dt);
                   int hour=cal.get(Calendar.HOUR);
                    int minute=cal.get(Calendar.MINUTE);
                    sleep_time=hour*4+(int)Math.round((double)minute/60*4);
                   // System.out.println(sleep_time);

                }
                if(count>25){
                    break;
                }
            }

            return sleep_time;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


       /* public static void main(String args[]) {

            creat(unit);
            for(int i=0;i<Population.length;i++){
             System.out.println(Population[i]);
         }
     }
*/


}
