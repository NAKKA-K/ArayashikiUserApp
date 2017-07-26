package com.example.snakka.arayashikiuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class VoiceGuideActivity extends AppCompatActivity{
    private VoiceRevival voiceRev;
    private TextView textView1,textView2;
    private static BLEManager bleMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_guide);
        // TextViewオブジェクト生成
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);

        // VoiceRevivalActivityクラスをインスタンス化
        // 再生機構の初期化
        voiceRev = new VoiceRevival(getApplicationContext());
        // 音声再生あんど格納された文字列を返す
        String viewString = voiceRev.startVoice();

        // 返ってきたviewStringの中身が空のままだったら行き止まりと判定
        if(viewString.isEmpty())
        {
            textView2.setText("行き止まりです");
        }
        else
        {
            textView1.setText(viewString);
            textView2.setText("に進めます");
        }


        //BEL
        //initBLE();
    }

    /** BLE通信をするために必要な前準備を実装したメソッド */
    private void initBLE(){
        bleMgr = new BLEManager(this);

        bleMgr.onBluetooth(this); //Bluetoothを起動
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("onResume", "scan開始だってばよ");
        //bleMgr.execute(); //BLEスキャン開始
    }


    @Override
    protected void onPause(){
        super.onPause();
        //bleMgr.getProDialog().cancel();
        Log.d("onPause", "cancelだってばよ");
    }

}


