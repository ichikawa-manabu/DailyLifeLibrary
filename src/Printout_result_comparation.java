import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jiao on 2017/02/22.
 * 睡眠の計算結果は元の統計に対して、どれくらいの違いがあるか
 */
public class Printout_result_comparation {
    static  int NO_OF_PARAMETERS =100000;
    public static int defaultGeneLength = 96;//24時間

    // 入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:国民生活時間調査から抽出した睡眠期間
    public static int sleep_period(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        int time = 0;
        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            //for (int i = rowstart; !cellValue.equals("睡　　　　　　 眠"); i++) {//睡眠の行を抽出する
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            Cell cell_a = row.getCell(0);        //i行第1列
            cellValue = cell_a.getStringCellValue().trim();
            if(cellValue.equals("睡　　　　　　 眠")){
                Cell cell_b = row.getCell(3);        //睡眠平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                // cellValue = cell_b.getStringCellValue().trim();
                double cellValue2 = cell_b.getNumericCellValue();//dateの形
                time=(int)(Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15));
                //System.out.println( "average_sleep_time:"+ time+"*15min;");
                break;
            }
        }

        return time;

    }

    public static int[] file_record(File inFile1) {
        int[] genes = new int[NO_OF_PARAMETERS];
        try {
            BufferedReader distance_info = new BufferedReader(new InputStreamReader(new FileInputStream(inFile1), "SHIFT_JIS"));

            String line;
            line = distance_info.readLine();
            int i=0;
            while ((line = distance_info.readLine()) != null) {
                String pair[] = line.split(",");

                SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
                Date dt2 = sdf.parse(pair[0]);
                int h=dt2.getHours();
                int m=dt2.getMinutes();
                int lTime = h*4+m/15;
                genes[i]=lTime;

                System.out.println(genes[i] );
                i++;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return genes;
    }
    // 最初個体を作る
    public static byte[] generateIndividual(int start_time,int sleep_time) {
        //個体が持つ行列
        //個体が持つ睡眠時間 sleep_time
        //例：７時間15分=29
        byte[] genes = new byte[defaultGeneLength];

        start_time=(start_time+defaultGeneLength-1)%defaultGeneLength;//行列は0から始まるので、係数の調整が必要
        int i=0;
        while(i< sleep_time){
            genes[start_time] = 1;
            start_time=(start_time+1)%defaultGeneLength;
            i++;
        }
        return genes;
    }

    //全ての個体が持つ行列の足し算、GAの種を作る
    public static int[] Population(int[] unit,int sleep_time) {
        int[] Population = new int[defaultGeneLength];
        for(int i=0;i<unit.length;i++){
            byte[] genes = new byte[defaultGeneLength];
            int start_time= unit[i];
            genes=generateIndividual(unit[i],sleep_time);
            for(int j=0;j<genes.length;j++) {
                int t=(int)genes[j];
                //System.out.println(t);
                Population[j] = (int)genes[j] + Population[j];
                genes[j]=0;
            }

        }
        return Population;
    }
    public static void main(String args[]) throws IOException {
        File  perform_time = new File("/Users/jiao/Desktop/国民生活時間調査/out_sa/成人全体sleep_start_time.csv");//計算結果
        //File  perform_time = new File("/Users/jiao/Desktop/国民生活時間調査/out_ga/成人全体sleep_start_time.csv");//計算結果

        File  period = new File("/Users/jiao/Desktop/国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        int sleep_time=sleep_period(period,1);//成人全体を指定する
        int[] genes = new int[NO_OF_PARAMETERS];
        genes=file_record(perform_time);
        int[] result = new int[defaultGeneLength];
        result=Population(genes,sleep_time);
        for(int i=0;i<defaultGeneLength;i++) {
            System.out.println((double)result[i]/NO_OF_PARAMETERS*100);
        }
    }
}
