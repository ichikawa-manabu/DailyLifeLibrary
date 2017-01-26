package work_start_time;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by jiao on 2017/01/18.
 */
public class Initialization {
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間
    //扱うパラメータの数 今回は10000人と想定する
    public static final int NO_OF_PARAMETERS =100000;


    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
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
            if(cellValue.equals("仕　　　　　　事")){
                Cell cell_b = row.getCell(3);        //睡眠平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                // cellValue = cell_b.getStringCellValue().trim();
                double cellValue2 = cell_b.getNumericCellValue();//dateの形
                time=(int)(Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15));
                System.out.println( "average_work_time:"+ time+"*15min;");
                break;
            }
        }

        return time;

    }


    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:国民生活時間調査から抽出した各タイプの人の時間ごと寝ている確率　(15分単位)　　単位を統一するため、確率*NO_OF_PARAMETERS
    public static int[] standard_sleep_time(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        int[] time = new int[defaultGeneLength];
        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            //for (int i = rowstart; !cellValue.equals("睡　　　　　　 眠"); i++) {//睡眠の行を抽出する
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            HSSFCell cell = row.getCell(0);        //i行第1列
            if (null == cell) continue;
            if(cell.getCellType()== HSSFCell.CELL_TYPE_STRING){
                cellValue = cell.getStringCellValue().trim();
            }
            if(cellValue.equals("仕　　　　　　事")){
                for (int k = 2; k < 2+defaultGeneLength; k++) {
                    Cell cell_b = row.getCell(k);
                    time[k - 2] = (int) (cell_b.getNumericCellValue() * NO_OF_PARAMETERS / 100);//
                    //System.out.println(time[k-2]);
                }
                break;
            }
        }

        return time;

    }

    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    //返し値:sheetの名前：例えば；成人男性どか
    public static String name(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        String name="";
        int time = 0;
        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            Cell cell_a = row.getCell(0);        //i行第1列
            cellValue = cell_a.getStringCellValue().trim();
            if(cellValue.equals("【全国】")){
                Cell cell_b = row.getCell(2);        //睡眠平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                // cellValue = cell_b.getStringCellValue().trim();
                name = cell_b.getStringCellValue();//dateの形
                // System.out.println( "name"+cellValue2);
                break;
            }
        }

        return name;

    }



    // 最初個体を作る
    public static byte[] generateIndividual(int start_time,int sleep_time) {
        //個体が持つ行列
        //個体が持つ睡眠時間 sleep_time
        //例：７時間15分=29
        byte[] genes = new byte[defaultGeneLength];
        //int start_time= (int)(Math.random()*defaultGeneLength);
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

    public static int[] creat(int[] unit, File file,int sheet_number) throws IOException {
        //各人のstart_timeから全ての人の寝る時間の集計
        int Sleep_period= sleep_period(file,sheet_number);
        int[] Population=Population(unit, Sleep_period);
        return Population;

    }

    ///////////////////////////


}
