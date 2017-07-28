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
    public static Button reVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_guide);
        // TextViewオブジェクト生成
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        // Buttonオブジェクト作成
        reVoice = (Button)findViewById(R.id.button);
        //再生機構の初期化
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
        Intent intent = new Intent(this, BLEManager.class);
        this.startService(intent);
    }
}
