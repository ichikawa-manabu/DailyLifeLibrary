package Life_style;

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
 * Created by jiao on 2017/01/17.
 * 作り中
 */
public class MAIN {
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間
    //扱うパラメータの数 今回は10000人と想定する
    public static final int NO_OF_PARAMETERS =10000;
    /*public static File file_in = new File("/Users/jiao/Desktop/国民生活時間調査/20代女性平日 ④時刻別行為者率（職業別、都市規模別ほか）.csv");
    public static File file_in2 = new File("/Users/jiao/Desktop/国民生活時間調査/20代女性②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.csv");
    //個体数
    public static int populationSize = 10000;
    //個体の長さ
    public static int defaultGeneLength = 96;//24時間

    public static void main(String args[]){
        //睡眠のstart_timeを記録する行列
        int[] unit = new int[populationSize];
        int[] Population = new int[defaultGeneLength];
        Population=initialize.creat(unit,file_in2);
        for(int i=0;i<Population.length;i++){
            System.out.println(Population[i]);
        }


        int[] standard_sleep_time = new int[defaultGeneLength];
        standard_sleep_time=initialize.standard_sleep_time(file_in);
        for(int i=0;i<standard_sleep_time.length;i++){
            System.out.println(standard_sleep_time[i]);
        }

    }
*/

    public static void main(String args[]) throws IOException {
        File file = new File("/Users/jiao/Desktop/国民生活時間調査/②1日の行為者率・行為者平均時間量・全体平均時間量・標準偏差（国民全体、層別）.xls");
        int time;
        int page=2;
        time = sleep_period(file,page);
//二つのファイル対応するページ番号が違います
        int page2=3;
        File file2 = new File("/Users/jiao/Desktop/国民生活時間調査/④時刻別行為者率（職業別、都市規模別ほか）.xls");
        int[] standard_sleep_time = new int[defaultGeneLength];
        standard_sleep_time=standard_sleep_time(file2,page);

    }

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
            if(cellValue.equals("睡　　　　　　 眠")){
                Cell cell_b = row.getCell(3);        //睡眠平均時間を取る
               // cellValue = cell_b.getStringCellValue().trim();
                double cellValue2 = cell_b.getNumericCellValue();//dateの形
                time=(int)(Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15));
                //System.out.println(time + "v"+i);
                break;
            }
        }

         return time;

        /*    for (int k = cellStart; k <= cellEnd; k++) {
                HSSFCell cell = row.getCell(k);
                if (null == cell) continue;
                System.out.print("" + k + "  ");
                //System.out.print("type:"+cell.getCellType());

                switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                        System.out.print(cell.getNumericCellValue()
                                + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_STRING: // String
                        System.out.print(cell.getStringCellValue()
                                + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                        System.out.println(cell.getBooleanCellValue()
                                + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA: // formula
                        System.out.print(cell.getCellFormula() + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_BLANK: // null
                        System.out.println(" ");
                        break;
                    case HSSFCell.CELL_TYPE_ERROR: // dame
                        System.out.println(" ");
                        break;
                    default:
                        System.out.print(" Unavailable  ");//ダメ処理
                        break;
                }

            }
            //System.out.print("\n");*/

    }

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
           if(cell.getCellType()== HSSFCell.CELL_TYPE_BLANK){
            //    cellValue = cell.getStringCellValue().trim();
            }
            //////

          /*  switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                    System.out.print(cell.getNumericCellValue()
                            + "   ");
                    break;
                case HSSFCell.CELL_TYPE_STRING: // String
                    System.out.print(cell.getStringCellValue()
                            + "   ");
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                    System.out.println(cell.getBooleanCellValue()
                            + "   ");
                    break;
                case HSSFCell.CELL_TYPE_FORMULA: // formula
                    System.out.print(cell.getCellFormula() + "   ");
                    break;
                case HSSFCell.CELL_TYPE_BLANK: // null
                    System.out.println(" ");
                    break;
                case HSSFCell.CELL_TYPE_ERROR: // dame
                    System.out.println(" ");
                    break;
                default:
                    System.out.print(" Unavailable  ");//ダメ処理
                    break;
            }*/
            ////////

            if(cellValue.equals("睡　　　　　　 眠")){
                for (int k = 2; k < 2+defaultGeneLength; k++) {
                    Cell cell_b = row.getCell(k);
                    time[k - 2] = (int) (cell_b.getNumericCellValue() * NO_OF_PARAMETERS / 100);//
                    System.out.println(time[k-2]);
                }
             //   Cell cell_b = row.getCell(3);        //睡眠平均時間を取る
                // cellValue = cell_b.getStringCellValue().trim();
               // double cellValue2 = cell_b.getNumericCellValue();//dateの形
                //time=(int)(Math.floor(cellValue2*24)*4+Math.round((cellValue2*24-Math.floor(cellValue2*24))*60/15));
                //System.out.println(time + "v"+i);
                break;
            }
        }

        return time;

        /*    for (int k = cellStart; k <= cellEnd; k++) {
                HSSFCell cell = row.getCell(k);
                if (null == cell) continue;
                System.out.print("" + k + "  ");
                //System.out.print("type:"+cell.getCellType());

                switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                        System.out.print(cell.getNumericCellValue()
                                + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_STRING: // String
                        System.out.print(cell.getStringCellValue()
                                + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                        System.out.println(cell.getBooleanCellValue()
                                + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA: // formula
                        System.out.print(cell.getCellFormula() + "   ");
                        break;
                    case HSSFCell.CELL_TYPE_BLANK: // null
                        System.out.println(" ");
                        break;
                    case HSSFCell.CELL_TYPE_ERROR: // dame
                        System.out.println(" ");
                        break;
                    default:
                        System.out.print(" Unavailable  ");//ダメ処理
                        break;
                }

            }
            //System.out.print("\n");*/

    }
}


