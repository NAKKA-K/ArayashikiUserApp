package com.example.snakka.arayashikiuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AccountCreateActivity extends AppCompatActivity {
    EditText nameEdit;
    EditText mailEdit;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);

        nameEdit = (EditText) findViewById(R.id.userNameEdit);
        mailEdit = (EditText) findViewById(R.id.guardianMailEdit);
    }


    //TODO:テスト実装
    /*createButtonを押された時の動作
     *入力されたデータを解析して問題が無ければ登録後、スタート画面に戻る*/
    public void onCreateButtonClick(View view){
        AccountManager accountMgr = new AccountManager();

        String userName = nameEdit.getText().toString();
        String guardianMail = mailEdit.getText().toString();

        //TODO:受け付けない形式の文字列の場合、警告だけ表示して画面は変わらない
        if(accountMgr.isLogicalCheckName(userName) == false){
            return;
        }


        //TODO:サーバに問い合わせて、ユーザー情報の確認とログイン。
        if(accountMgr.postAccountToServer(userName, guardianMail)){
            accountMgr.loginAccount(view.getContext(), userName, guardianMail);
        }

        finish();
    }
}
