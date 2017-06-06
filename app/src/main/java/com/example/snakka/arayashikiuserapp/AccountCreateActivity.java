package com.example.snakka.arayashikiuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AccountCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);


    }


    /**createButtonを押された時の動作
     * 入力されたデータを解析して問題が無ければ登録後、スタート画面に戻る*/
    public void onCreateButtonClick(View view){
        //TODO:テスト実装
        finish();
    }
}
