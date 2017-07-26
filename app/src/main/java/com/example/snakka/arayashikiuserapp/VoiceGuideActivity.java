package com.example.snakka.arayashikiuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class VoiceGuideActivity extends AppCompatActivity{
    private VoiceRevival voiceRev;
    private TextView textView1,textView2;
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
        // 音声再生
        voiceRev.startVoice();
        // 文字列を返す
        String viewString = voiceRev.viewVoice();

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
    }

}


