package com.example.snakka.arayashikiuserapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    /**startButtonを押したときの動作
     * 1度アカウント登録していた場合は音声案内画面に。
     * 始めてアプリを起動したときはアカウント登録画面に飛ぶ。*/
    public void onStartButtonClick(View view){
        //TODO:テスト実装
        Intent intent = new Intent(MainActivity.this, AccountCreateActivity.class);
        startActivity(intent);
    }
}
