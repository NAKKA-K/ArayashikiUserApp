package com.example.snakka.arayashikiuserapp;

/**
 * Created by morikei on 2017/05/24.
 * ユーザー側のナンバー操作クラス
 */

public class NumberOperation {

    public static final int FRONT = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final int END = 0;

    private final int NORTH = 0;
    private final int EAST = 1;
    private final int SOUTH = 2;
    private final int WEST = 3;

    private int[]  drectionNumber = new int[4]; //どの方角に進路があるかを格納：0 無  -1 backNum

    private int currentNum; //現在地のNumber
    /*private int nextNorth, nextSouth,  //進路方向にある次のNumber北南西東
            nextWest, nextEast;*/
    private int backNum;    //一つ前に通り過ぎたNumber

    //コンストラクターの宣言（初期化
    public NumberOperation (){
        currentNum = 0;
        /*nextNorth = 0;// N北
        nextEast = 0;// E東
        nextSouth = 0;// S南
        nextWest = 0;// W西*/
        backNum = 0;
        for(int i = 0 ; i < drectionNumber.length ; i++){
            drectionNumber[i] = 0;
        }
    }


    //setNum:センサーから送られる情報をセットする
    /*引数
    num センサーから取得したNumber*/
    public void setNum(int num){
        backNum = currentNum;
        currentNum = num;

    }

    //TODO:setnext[方角]:サーバーから送られてくる四方のNumberをdrectionNumber[方角]にセットする
    /*引数
    next[方角] 方角事のNumber センサーが無ければ：0 有れば：非0
     */
    public void setNextNorth(int nextNorth) {
        if (nextNorth == backNum) { //backNumとnext
            drectionNumber[NORTH] = -1;
        }else{
            drectionNumber[NORTH] = nextNorth;
        }
    }

    public void setNextEast(int nextEast) {
        if (nextEast == backNum) {
            drectionNumber[EAST] = -1;
        }else {
            drectionNumber[EAST] = nextEast;
        }
    }

    public void setNextSouth(int nextSouth) {
        if (nextSouth == backNum) {
            drectionNumber[SOUTH] = -1;
        }else {
            drectionNumber[SOUTH] = nextSouth;
        }
    }

    public void setNextWest(int nextWest) {
        if (nextWest == backNum) {
            drectionNumber[WEST] = -1;
        }else {
            drectionNumber[WEST] = nextWest;
        }
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
    public int getNextNorth() {
        return drectionNumber[NORTH];
    }

    public int getNextSouth() {
        return drectionNumber[SOUTH];
    }

    public int getNextWest() {
        return drectionNumber[WEST];
    }

    public int getNextEast() {
        return drectionNumber[EAST];
    }


    private int[] getSelectCourse(int backDrection){
        int[] courseSelect = new int[4];
        int courseSelectIdx = 0;
        int routeDrection = backDrection;

        /*routeDrectionが現在backDrection(後ろ)を示しているのに対して、
        進路の前を示すように変更する
        例：backが SOUTH == 2 の時は、前は NORTH == 0 */
        if ((routeDrection += 2 ) > 3 ){
            routeDrection -= 4;
        }


        setCourse: for(int selectCourse = 1 ; selectCourse <= 3 ; ){
            if(routeDrection == backDrection){
                continue;
            }
            if()
            drectionNumber[routeDrection]

            if((routeDrection + 1) <= 3){
                routeDrection += 1;
            }
        }
    }

    public int[] getCourse() {
        int backDrection = 0;

        for (; drectionNumber[backDrection] == backNum; backDrection++) {}

        return getSelectCourse(backDrection);
    }
}