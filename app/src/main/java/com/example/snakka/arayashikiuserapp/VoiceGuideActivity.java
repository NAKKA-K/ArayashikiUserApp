package com.example.snakka.arayashikiuserapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;


public class VoiceGuideActivity extends AppCompatActivity{
    public static VoiceRevival voiceRev;
    private HttpCommunication httpCommunication;
    public static TextView textView1,textView2;
    public static Button reVoiceButton;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_guide);
        // Buttonインスタンス作成
        reVoiceButton = (Button)findViewById(R.id.reVoiceButton);
        //音声再生機構の初期化
        voiceRev = new VoiceRevival(getApplicationContext());
        //HTTP通信のインスタンス生成
        httpCommunication = new HttpCommunication();

    }


    @Override
    protected void onStart(){
        super.onStart();
        //HTTPのGETアンドPOSTを1秒毎に交互にします
        httpCommunication.asyncTaskToGet();

        initBLE();
    }

    private void initBLE(){
        BLEManager.setContext(this);
        serviceIntent = new Intent(this, BLEManager.class);
        if(serviceIntent == null) Log.e("initBLE()", "serviceIntentがnullだーーーー！！");
        if(serviceIntent != null){
            this.startService(serviceIntent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(serviceIntent == null) return;
        this.stopService(serviceIntent);
        serviceIntent = null;
    }
}
