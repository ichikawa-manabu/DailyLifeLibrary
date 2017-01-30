package SATEST_worker;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by jiao on 2017/01/18.
 */
public class Initialization {
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間
    //扱うパラメータの数 今回は10000人と想定する
    public static final int NO_OF_PARAMETERS =100000;



    public static int move_period(int average, int deviation) throws IOException {

                int time;
                Random random = new Random();
                 time=(int)Math.round(deviation * random.nextGaussian() + average);
               // System.out.println( average+"  "+ deviation+"MOVE period:"+ time+"*15min;");
        return time;

    }


    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:平均通学時間
    public static int move_average(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        int average = 0;

        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            //for (int i = rowstart; !cellValue.equals("睡　　　　　　 眠"); i++) {//行を抽出する
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            Cell cell_a = row.getCell(0);        //i行第1列
            cellValue = cell_a.getStringCellValue().trim();
            if(cellValue.equals("通　　　　　 　勤")){
                Cell cell_b = row.getCell(3);        //通学平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                // cellValue = cell_b.getStringCellValue().trim();
                double cellValue2 = cell_b.getNumericCellValue();//dateの形
                // average=(int)Math.ceil((Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15))/2);
                average=(int)((Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15))/2);//往復時間/2

                break;
            }
        }

        return average;

    }

    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:通学時間の標準偏差
    public static int move_deviation(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        int deviation=0;
        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            //for (int i = rowstart; !cellValue.equals("睡　　　　　　 眠"); i++) {//行を抽出する
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            Cell cell_a = row.getCell(0);        //i行第1列
            cellValue = cell_a.getStringCellValue().trim();
            if(cellValue.equals("通　　　　　 　勤")){

                Cell cell_c = row.getCell(5);        //睡眠平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                double cellValue3 = cell_c.getNumericCellValue();//dateの形
                deviation=(int)((Math.floor(cellValue3*24)*4+Math.round((cellValue3*24-Math.floor(cellValue3*24))*60/15))/2);

                break;
            }
        }
        return deviation;
    }

    //移動時間のset

    //

    public static int[] move_period_set(File file, int sheet_number) throws IOException {
        int[] move_period_set = new int[NO_OF_PARAMETERS];
        int average=move_average(file, sheet_number);
        int deviation=move_deviation(file, sheet_number);
        for(int i=0; i<NO_OF_PARAMETERS;i++){
            move_period_set[i] = move_period(average, deviation);
        }
        return move_period_set;
    }


    // 最初個体を作る
    public static byte[] generateStartseed(int start_time,int move_time) {
        //個体が持つ行列
        //個体が持つ移動時間move_time
        //例：７時間15分=29


        byte[] genes = new byte[defaultGeneLength];
        int i=0;
        start_time=(start_time-move_time+defaultGeneLength)%defaultGeneLength;
       if(move_time==0) {//移動時間は１５MIN単位で取ってるので　15分以内なら　今の時間を開始時間にする　移動時間は０
            genes[start_time] = 1;
        }

        else {
           while (i < Math.abs(move_time)) {//
               genes[start_time] = 1;
                   start_time=(start_time+1)%defaultGeneLength;//

               i++;
           }
       }
        return genes;
    }


    //全ての個体が持つ行列の足し算、種を作る

    public static int[] generateStartPopulation(int[] move_time_set,int start_time[]) {
        byte[] genes = new byte[defaultGeneLength];
        int[] Population = new int[defaultGeneLength];
        for(int i=0;i<NO_OF_PARAMETERS;i++) {
            genes = generateStartseed(start_time[i], move_time_set[i]);
            //System.out.println("start_time " +start_time[i]+"and"+i);

            for(int j=0;j<defaultGeneLength;j++) {
                Population[j] += genes[j];
            }

        }
        return Population;
    }



    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:国民生活時間調査から抽出した各タイプの人の時間ごと寝ている確率　(15分単位)　　単位を統一するため、確率*NO_OF_PARAMETERS
    public static int[] standard_study_time(File file, int sheet_number) throws IOException {

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
            if(cellValue.equals("通　　　　　 　勤")){
                for (int k = 2; k < 2+(int)(defaultGeneLength); k++) {
                    Cell cell_b = row.getCell(k);
                    time[k - 2] = (int) (cell_b.getNumericCellValue() * NO_OF_PARAMETERS / 100);//
                   // System.out.println(time[k-2]);
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


    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:学校にいる時間
    public static int study_average(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        int average = 0;

        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            //for (int i = rowstart; !cellValue.equals("睡　　　　　　 眠"); i++) {//行を抽出する
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            Cell cell_a = row.getCell(0);        //i行第1列
            cellValue = cell_a.getStringCellValue().trim();
            if(cellValue.equals("仕　　　　　　事")){
                Cell cell_b = row.getCell(3);        //通学平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                // cellValue = cell_b.getStringCellValue().trim();
                double cellValue2 = cell_b.getNumericCellValue();//dateの形
                // average=(int)Math.ceil((Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15))/2);
                average=(int)((Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15)));//往復時間/2

                break;
            }
        }

        return average;

    }

    //入力ファイル：②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls;
    //検索したいsheet_numberを入力
    // 返し値:学内にいる時間の標準偏差
    public static int study_deviation(File file, int sheet_number) throws IOException {

        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet_number);

        int deviation=0;
        int rowstart = hssfSheet.getFirstRowNum();
        int rowEnd = hssfSheet.getLastRowNum();
        String cellValue = "";
        for (int i = rowstart; i <= rowEnd; i++) {
            //for (int i = rowstart; !cellValue.equals("睡　　　　　　 眠"); i++) {//行を抽出する
            HSSFRow row = hssfSheet.getRow(i);//第i行
            if (null == row) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            Cell cell_a = row.getCell(0);        //i行第1列
            cellValue = cell_a.getStringCellValue().trim();
            if(cellValue.equals("仕　　　　　　事")){

                Cell cell_c = row.getCell(5);        //睡眠平均時間を取る　平日のみを抽出する　土曜日なら Cell cell_b = row.getCell(8);日曜日なら Cell cell_b = row.getCell(13);
                double cellValue3 = cell_c.getNumericCellValue();//dateの形
                deviation=(int)((Math.floor(cellValue3*24)*4+Math.round((cellValue3*24-Math.floor(cellValue3*24))*60/15)));

                break;
            }
        }
        return deviation;
    }


    public static int[] study_period_set(File file, int sheet_number) throws IOException {
        int[] study_period_set = new int[NO_OF_PARAMETERS];
        int average=study_average(file, sheet_number);
        int deviation=study_deviation(file, sheet_number);
        for(int i=0; i<NO_OF_PARAMETERS;i++){

            study_period_set[i] = move_period(average, deviation);//共有できるmethod
        }
        return study_period_set;
    }

    public static int[] study_end_time(File file, int sheet_number, int start_time[]) throws IOException {
        int[] study_end_time_set = study_period_set(file, sheet_number);
        for(int i=0; i<NO_OF_PARAMETERS;i++){
            study_end_time_set[i]=(study_end_time_set[i]+start_time[i])%defaultGeneLength;
        }
        return study_end_time_set;
    }

    ///////////////////////////

    // end個体を作る
    public static byte[] generateEndseed(int end_time,int move_time) {
        //個体が持つ行列
        //例：７時間15分=29


        byte[] genes = new byte[defaultGeneLength];
        int i=0;
        //start_time=(end_time-move_time)%defaultGeneLength;
        if(move_time==0) {//移動時間は１５MIN単位で取ってるので　15分以内なら　今の時間を開始時間にする　移動時間は０
            genes[(end_time+defaultGeneLength)%defaultGeneLength] = 1;
        }

        while(i<Math.abs(move_time)&&end_time<defaultGeneLength){//

            genes[(end_time+defaultGeneLength)%defaultGeneLength] = 1;//////////
            end_time=(end_time+1)%defaultGeneLength;//
            i++;
        }
        return genes;
    }


    //全ての個体が持つ行列の足し算、種を作る

    public static int[] generateEndPopulation(int[] end_time_set,int[] move_time_set) {
        byte[] genes = new byte[defaultGeneLength];
        int[] Population = new int[defaultGeneLength];
        for(int i=0;i<NO_OF_PARAMETERS;i++) {
            genes = generateEndseed(end_time_set[i], move_time_set[i]);
            //System.out.println("start_time " +start_time+"and"+i);

            for(int j=0;j<defaultGeneLength;j++) {
                Population[j] += genes[j];
            }

        }
        return Population;
    }

}
