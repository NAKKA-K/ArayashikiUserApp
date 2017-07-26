package com.example.snakka.arayashikiuserapp;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Random;


public class VoiceRevivalActivity extends AppCompatActivity {

    private AudioAttributes audioAttributes;
    private SoundPool sp;
    private int directionVoice[] = {
            R.raw.front, //前
            R.raw.right, //右
            R.raw.left,  //左
            R.raw.start, //に進めます
            R.raw.stop   //行き止まりです
    };
    private String[] directionString = {
            "前",
            "右",
            "左"
    };
    private int [] directionNum = new int[4]; //directionNumberGetメソッドから方向ナンバーを受け取るためのフィールド
    private int [] soundVoice = new int[6];

    //主に初期化するメソッド
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initVoice() {
        //super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        sp = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(2)
                .build();

        //音声をload
        for(int i=0;i<directionVoice.length-1;i++) soundVoice[i] = sp.load(this,directionVoice[i],1);

        // load が終わったか確認する場合
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });

        voiceStart();
    }

    //ナンバーを受け取るメソッド(今はテスト用)
    public void directionNumberGet() {
        int i = 0;
        Random rand = new Random(); //とりあえず今はランダムに生成した数を方向ナンバーとする(本来はNumberOperationクラス内にあるメソッドから受け取る
        while (i < directionNum.length) {
            directionNum[i] = rand.nextInt(2);
            if (directionNum[i] == 0 || directionNum[i] == 1) i++; //0の場合はその方向(前右左のいずれか)に進めなくて1の場合はその方向に進める
        }
    }

    //音声を再生するメソッド
    private void voiceStart() {
        TextView textView1 = (TextView) findViewById(R.id.textView1); //TextViewオブジェクト生成
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        directionNumberGet(); //ナンバーを受け取る
        String viewString = "";

        for (int i = 0; i < directionNum.length-1; i++) {
            if (directionNum[i] == 1) {
                viewString += directionString[i] + " "; //前 右・・・といった感じでスペースを空けながら表示するため格納
                sp.play(soundVoice[i], 1.0f, 1.0f, 0, 0, 1); //ここで行ける方向の音声を再生
            }
        }

        if (directionNum[0] == 1 || directionNum[1] == 1 || directionNum[2] == 1) {
            sp.play(soundVoice[3], 1.0f, 1.0f, 0, 0, 1);
            textView1.setText(viewString); //全てのテキスト表示
            textView2.setText("に進めます");
        } else {
            sp.play(soundVoice[4], 1.0f, 1.0f, 0, 0, 1);
            textView2.setText("行き止まりです");
        }
    }


    //メモリを解放するメソッド
    @Override
    public void onDestroy() {
        super.onDestroy();
        //SoundPoolの解放
        sp.release();
    }
}