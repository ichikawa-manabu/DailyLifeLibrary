package Life_style;

import java.io.File;

/**
 * Created by jiao on 2017/01/17.
 * 使い方: 「dir:国民生活時間調査のフォルダ」 を入力したら、その下のoutのフォルダに出力ファイルcsvが出てきます。
 *    //sheet_num1:②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls"の中調べたいsheetの番号
 *    //sheet_num２:④時刻別行為者率（職業別、都市規模別ほか）.xls"　　
 *    ＃注意：sheet_num1とsheet_num２不一致かも
 *
 */
public class MAIN {

    public static void main(String args[]) throws Exception {

        File dir = new File("/Users/jiao/Desktop/国民生活時間調査");
        File outDir = new File(dir.getPath() + "/out");
        if(!outDir.exists()) {
            outDir.mkdir();
        }
        int sheet_num1=22;//女20代平日
        int sheet_num2=34;//20代
        new Evaluator(dir,outDir,sheet_num1,sheet_num2);
    }


}


