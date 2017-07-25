package com.example.snakka.arayashikiuserapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    BLEManager bleMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bleMgr = new BLEManager(this);
        if(bleMgr.isBleSupport() == false){
            this.moveTaskToBack(true);
        }

    }



    //TODO:テスト実装
    /*startButtonを押したときの動作
     *1度アカウント登録していた場合は音声案内画面に。
     *始めてアプリを起動したときはアカウント登録画面に飛ぶ。*/
    public void onStartButtonClick(View view){
        //TODO:テスト実装。現在の作業はすべて音声案内画面で行われるはずなため、常にtrueになるように設定
        if(true || AccountManager.loginedAccount(view.getContext()) == true){
            Intent intent = new Intent(MainActivity.this, VoiceRevivalActivity.class);
            startActivity(intent);


            bleMgr.onBluetooth(this); //Bluetoothを起動
            return;
        }

        //アプリにアカウントが登録されていない
        Intent intent = new Intent(MainActivity.this, AccountCreateActivity.class);
        startActivity(intent);
    }



}
