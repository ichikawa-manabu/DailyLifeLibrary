package SA_sleep;


/**
 * Created by jiao on 2017/01/18.
 * 評価個体を作る
 */
public class Individual {

    //10万人を想定する
     static  int NO_OF_PARAMETERS =100000;

    //一つの個体
    private int[] genes = new int[NO_OF_PARAMETERS];

    // 個体の適応値
    private double fitness = 0;

    public static final double MIN = 0;//閾値
    public static final double MAX = 96;//閾値

    public static double map(double x)//変数の定義域へ写像する。
    {
        return (MAX - MIN) * x + MIN;
    }

    ////////////////////////////
    // 最初遺伝子個体を作る


    //ランダム
    /*public void generateIndividual() {
        for (int i = 0; i < size(); i++) {
            int gene = (int) Math.floor(map(Math.random()));
            genes[i] = gene;
        }
    }
    */
    //

    //23:00ぐらい、寝る人が多いと考え、その間の数値を初期値として設定する
    public static int[] generateIndividual() {
        int[] genes = new int[NO_OF_PARAMETERS];
        //int[] possible_time={0,1,2,3,4,5,6,7,8,89,90,91,92,93,94,95}; //可能な時間22:00から4：00
        int[] possible_time={92,93}; //可能な時間22:00から4：00
        int b;
        for (int i = 0; i < NO_OF_PARAMETERS; i++) {
            b=(int)(Math.random()*possible_time.length);
            int gene = possible_time[b];
            genes[i] = gene;
        }
        return genes;
    }

    ////////////////////////////

    /* Getters and setters */
    // 個体の長さが違ったら、lengthの値にします
    public static void setDefaultGeneLength(int length) {
        NO_OF_PARAMETERS  = length;
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int value) {
        genes[index] = value;
        fitness = 0;
    }


    /* Public methods */
    //個体のサイズを測る
    public int size() {
        return genes.length;
    }



    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i);
        }
        return geneString;
    }

}
