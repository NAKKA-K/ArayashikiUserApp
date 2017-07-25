package com.example.snakka.arayashikiuserapp;


/**
 * Created by morikei on 2017/05/24.
 * ユーザー側のナンバー操作クラス
 */

public class SensorNumber {

    // FRONTから時計回りに連番
    public static final int FRONT = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final int END = 0;

    //NORTHから時計回りに連番
    private static final int NORTH = 0;
    private static final int EAST = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;

    private static int[]  drectionNumber = new int[4]; //どの方角に進路があるかを格納：0 無

    private int currentNum; //現在地のNumber
    private int backNum;    //一つ前に通り過ぎたNumber

    //コンストラクターの宣言（初期化
    /*public SensorNumber(){
        currentNum = 0;
        backNum = 0;
        for(int i = 0 ; i < drectionNumber.length ; i++){
            drectionNumber[i] = 12;
        }
    }*/


    //setNum:センサーから送られる情報をセットする
    /*引数
    num センサーから取得したNumber*/
    public void setNum(int num){
        backNum = currentNum;
        currentNum = num;

    }

    //TODO:setnext[方角]:サーバーから送られてくる四方のNumberをdrectionNumber[方角]にセットする
    /*引数
    next[方角] 方角事のNumber センサーが無ければ：0 有れば：各Number
     */
    public static void setNextNorth(int nextNorth) {
        drectionNumber[NORTH] = nextNorth;
    }

    public static void setNextEast(int nextEast) {
        drectionNumber[EAST] = nextEast;
    }

    public static void setNextSouth(int nextSouth) {
        drectionNumber[SOUTH] = nextSouth;
    }

    public static void setNextWest(int nextWest) {
        drectionNumber[WEST] = nextWest;
    }

    //TODO:現在地のナンバーを取得 必要性は薄いかもしれない
    public int getCurrentNum() {
        return currentNum;
    }
    //来た方向のNumberを取得
    public int getBackNum(){
        return backNum;
    }

    //GetNext:進路の先にあるNumberを取得する
    public static int getNextNorth() {
        return drectionNumber[NORTH];
    }

    public static int getNextSouth() {
        return drectionNumber[SOUTH];
    }

    public static int getNextWest() {
        return drectionNumber[WEST];
    }

    public static int getNextEast() {
        return drectionNumber[EAST];
    }


    private int[] getSelectCourse(int backDrection){
        int[] courseStorage = new int[4];
        int courseSelect = FRONT;   //進路の定数が連番になっているので(++)で増やしていく
        int routeDrection = backDrection;
        int courseSelectIdx = 0;

        /*routeDrectionが現在backDrection(後ろ)を示しているのに対して、
        進路の前(FRONT)を示すように変更する
        例：backが SOUTH == 2 の時は、FRONTは NORTH == 0 */
        if ((routeDrection += 2 ) > 3 ){
            routeDrection -= 4;
        }

        //  方角は4つしか定義してないので、初期値０から３までのループ
        for( int i = 0; i <= 3 ; i++){
            //TODO:隣接するNumberがその方角にない場合は０としているが、変更の可能性あり
            if(drectionNumber[routeDrection] != 0 && routeDrection != backDrection) {
                courseStorage[courseSelectIdx++] = courseSelect++;
            }else if(routeDrection != backDrection){
                courseSelect += 1;
            }
            //方角の定数の上限が WEST == 3 なので3を超える時０に戻す
            if((routeDrection + 1) <= 3){
                routeDrection += 1;
            }else {
                routeDrection = 0;
            }
        }
        courseStorage[courseSelectIdx] = END;
        return courseStorage;
    }

    //TODO:NORTHから時計回りにbackNumの指す方角を探す
    private int searchBackDrection(){
        int drection = NORTH;
        for (; drectionNumber[drection] != backNum; drection++) {}
        return drection;
    }

    //進路情報を取得するメゾット
    public int[] getCourse() {
        int backDrection;

        backDrection = searchBackDrection();

        return getSelectCourse(backDrection);
    }
}