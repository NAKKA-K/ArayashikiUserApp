package com.example.snakka.arayashikiuserapp;

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
        bleMgr.execute();
    }


    @Override
    protected void onStop(){
        bleMgr.cancel(true);
    }


    /** BLE通信をするために必要な前準備を実装したメソッド */
    private void initBLE(){
        bleMgr = new BLEManager();

        bleMgr.onBluetooth(this); //Bluetoothを起動
    }

}
