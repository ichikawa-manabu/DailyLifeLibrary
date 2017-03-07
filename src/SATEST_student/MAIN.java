package SATEST_student;



import java.io.File;

/**
 * Created by jiao.xue on 2017/01/26.
 */
public class MAIN {
    public static void main(String args[]) throws Exception {


       //国民生活時間調査のフォルダー
        File dir = new File("/Users/jiao/Desktop/国民生活時間調査");
        //出力フォルダー
        File outDir = new File(dir.getPath() + "/out_sa_study_time_new");
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
        //小学生中学生高校生
        int[]sheet_num1={57,58,59};
        int[]sheet_num2={108,111,114};

        for(int i=0; i<sheet_num1.length&&i<sheet_num2.length;i++){

            new Evaluation(dir,outDir,sheet_num1[i],sheet_num2[i]);
        }

    }
}
