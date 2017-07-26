package com.example.snakka.arayashikiuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AccountCreateActivity extends AppCompatActivity {
    EditText nameEdit;
    EditText mailEdit;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);

        nameEdit = (EditText) findViewById(R.id.userNameEdit);
        mailEdit = (EditText) findViewById(R.id.guardianMailEdit);
    }


    /*createButtonを押された時の動作
     *入力されたデータを解析して問題が無ければ登録後、スタート画面に戻る*/
    public void onCreateButtonClick(View view){
        String userName = nameEdit.getText().toString();
        String guardianMail = mailEdit.getText().toString();

        AccountManager accountMgr = new AccountManager(this, userName, guardianMail);


        //受け付けない形式の文字列の場合、警告だけ表示して画面は変わらない
        if(accountMgr.isLogicalCheckName(userName) == false){
            Toast.makeText(this, "使用できない文字が使われています", Toast.LENGTH_SHORT).show();
            return;
        }

        accountMgr.postAccountToServer(userName, guardianMail);
    }

}
