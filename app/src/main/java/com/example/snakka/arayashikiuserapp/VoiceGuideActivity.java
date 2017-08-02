package com.example.snakka.arayashikiuserapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;

public class VoiceGuideActivity extends AppCompatActivity{
    private VoiceRevival voiceRev;
    private HttpCommunication httpCommunication;
    public static TextView textView1,textView2;
    public static Button reVoiceButton;
    private static BLEManager bleMgr;
    public static Button reVoice;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_guide);
        // TextViewオブジェクト生成
        // textView1には「前」、「右」、「左」のいずれかが入る
        textView1 = (TextView) findViewById(R.id.textView1);
        // textView2には「に進めます」、「行き止まりです」のどちらかが入る
        textView2 = (TextView) findViewById(R.id.textView2);
        // Buttonオブジェクト作成
        reVoiceButton = (Button)findViewById(R.id.reVoiceButton);
        //音声再生機構の初期化
        voiceRev = new VoiceRevival(getApplicationContext());
        //HTTP通信のインスタンス生成
        httpCommunication = new HttpCommunication();

    }


    @Override
    protected void onStart(){
        super.onStart();
        //行ける方向の音声再生、文字表示、もう一度再生可能(全てを司る！！！)
        voiceRev.mainVoice();

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
        if(serviceIntent == null) return;
        this.stopService(serviceIntent);
        serviceIntent = null;
    }
}
