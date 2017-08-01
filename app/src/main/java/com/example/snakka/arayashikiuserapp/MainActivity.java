package com.example.snakka.arayashikiuserapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(BLEManager.isBleSupport(this) == false){
            Toast.makeText(this, "Bluetoothに対応していません", Toast.LENGTH_LONG).show();
            //TODO:HACK:仮想マシンでは絶対に終了してしまうので、一時コメント化
            //this.finishAndRemoveTask();
        }

    }



    /*startButtonを押したときの動作
     *1度アカウント登録していた場合は音声案内画面に。
     *始めてアプリを起動したときはアカウント登録画面に飛ぶ。*/
    public void onStartButtonClick(View view){
        //アカウント登録済み
        if(AccountManager.loginedAccount(view.getContext())){
            if(checkAppPermission() == false){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) == false){
                    //ポップアップを2度と表示しない設定になっている
                    Toast.makeText(this, "位置情報の権限が許可されていません", Toast.LENGTH_LONG).show();
                }else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                return;
            }


            Intent intent = new Intent(MainActivity.this, VoiceGuideActivity.class);
            startActivity(intent);
            return;
        }

        //アプリにアカウントが登録されていない
        Intent intent = new Intent(MainActivity.this, AccountCreateActivity.class);
        startActivity(intent);
    }

    /** permissionが設定されていればtrue */
    private boolean checkAppPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}