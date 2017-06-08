package com.example.snakka.arayashikiuserapp;

/**
 * Created by morikei on 2017/05/24.
 * ユーザー側のナンバー操作クラス
 */

public class Number {

    private int currentNum; //現在地のNumber
    private int nextNorth, nextSouth,  //進路方向にある次のNumber北南西東
            nextWest, nextEast;
    private int backNum;    //一つ前に通り過ぎたNumber

    //コンストラクターの宣言（初期化
    public Number (){
        currentNum = 0;
        nextNorth = 0;// N北
        nextSouth = 0;// S南
        nextWest = 0;// W西
        nextEast = 0;// E東
        backNum = 0;
    }


    //setNum:サーバーから送られる情報をセットする
    /*引数
    num ラズパイから取得したNumber*/
    public void SetNum(int num){
        backNum = currentNum;
        currentNum = num;

    }

    public void setNextNorth(int nextNorth) {
        if (nextNorth == backNum) {
            this.nextNorth = -1;
        }else{
        this.nextNorth = nextNorth;
        }
    }

    public void setNextSouth(int nextSouth) {
        if (nextSouth == backNum) {
            this.nextSouth = -1;
        }else {
            this.nextSouth = nextSouth;
        }
    }

    public void setNextWest(int nextWest) {
        if (nextWest == backNum) {
            this.nextWest = -1;
        }else {
            this.nextWest = nextWest;
        }
    }

    public void setNextEast(int nextEast) {
        if (nextEast == backNum) {
            this.nextEast = -1;
        }else {
            this.nextEast = nextEast;
        }
    }



    public int getCurrentNum() {
        return currentNum;
    }

    public int getBackNumet(){
        return backNum;
    }


    //GetNext:進路の先にあるNumberを取得する
    public int getNextNorth() {
        return nextNorth;
    }

    public int getNextSouth() {
        return nextSouth;
    }

    public int getNextWest() {
        return nextWest;
    }

    public int getNextEast() {
        return nextEast;
    }

    public String cours() {

    }
}