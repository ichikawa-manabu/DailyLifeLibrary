package SATEST;

import java.io.File;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class MAIN {
    public static void main(String args[]) throws Exception {


        File dir = new File("/Users/jiao.xue/Desktop/国民生活時間調査");
        File outDir = new File(dir.getPath() + "/out_sleep_time");
        if(!outDir.exists()) {
            outDir.mkdir();
        }
        /*
        //女20代を例としてテスト
        int sheet_num1=22;//女20代
        int sheet_num2=34;//女20代平日
        new Evaluation(dir,outDir,sheet_num1,sheet_num2);
        */


        //sheet_num1とsheet_num２可能な組み合わせを設定する　ファイルの順番はあんまりルールがないので、実際使う時、要確認
        int[]sheet_num1={4,5,6,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,57,58,59,60,61,62,63,64,65};
        int[]sheet_num2={1,4,7,10,13,16,19,22,25,28,31,34,37,40,43,46,49,52,54,57,60,63,66,69,72,75,78,81,84,87,90,93,96,99,102,105,108,111,114,117,120,123,126,129,132};


        for(int i=0; i<sheet_num1.length&&i<sheet_num2.length;i++){

            new Evaluation(dir,outDir,sheet_num1[i],sheet_num2[i]);
        }

    }
}
