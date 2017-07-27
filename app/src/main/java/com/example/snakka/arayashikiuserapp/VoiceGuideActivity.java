package com.example.snakka.arayashikiuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class VoiceGuideActivity extends AppCompatActivity{
    private VoiceRevival voiceRev;
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
       //行ける方向の音声再生、文字表示、もう一度再生可能(全てを司る！！！)
        voiceRev.mainVoice();

    }

}
