package com.example.snakka.arayashikiuserapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.View;


import static com.example.snakka.arayashikiuserapp.HttpCommunication.setSensorList;
import static com.example.snakka.arayashikiuserapp.SensorNumber.END;
import static com.example.snakka.arayashikiuserapp.SensorNumber.FRONT;
import static com.example.snakka.arayashikiuserapp.SensorNumber.LEFT;
import static com.example.snakka.arayashikiuserapp.SensorNumber.RIGHT;
import static com.example.snakka.arayashikiuserapp.SensorNumber.drectionNumber;
import static com.example.snakka.arayashikiuserapp.VoiceGuideActivity.reVoiceButton;
import static com.example.snakka.arayashikiuserapp.VoiceGuideActivity.textView1;
import static com.example.snakka.arayashikiuserapp.VoiceGuideActivity.textView2;

public class VoiceRevival {
    //音声の属性を指定するクラス
    private AudioAttributes audioAttributes;
    //音声の読み込みや再生を行えるクラス
    public static SoundPool soundPool;
    //センサーの方向ナンバーを受け取れるクラス
    private SensorNumber senNum;
    //音声リソース配列
    private static final int voiceResources[] =
            {
                    R.raw.front, //前
                    R.raw.right, //右
                    R.raw.left,  //左
                    R.raw.start, //に進めます
                    R.raw.stop   //行き止まりです
            };
    //読み込みをした際に受け取る音声Id配列(2回目以降は固定化される)
    public static final int[] voiceIds = new int[6];


    // 初期化
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VoiceRevival(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            // 音声の属性を設定
            audioAttributes = new AudioAttributes.Builder()
                    // USAGE_MEDIA
                    // USAGE_GAME
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    // CONTENT_TYPE_MUSIC
                    // CONTENT_TYPE_SPEECH, etc.
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            // SoundPoolクラス作成
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    // ストリーム数に応じて
                    .setMaxStreams(1)
                    .build();
        }

        //音声をロード
        for (int i = 0; i < voiceResources.length; i++)
            voiceIds[i] = soundPool.load(context, voiceResources[i], 1);
    }

    //音声再生の全てを司るメソッド
    public void mainVoice() {
        //blockがfalseだった時
        //Log.d("VoiceRevival",drectionNumber[0]));
        // 方向ナンバーを受け取る
       // Log.e("VoiceRevival","ナンバーを受け取ります");
        //senNum = new SensorNumber();
        //senNum.getCourse();
        //Log.e("VoiceRevival","ナンバーを受け取りました");

        //ここでも配列の中身を拝見
        for(int i=0;i<drectionNumber.length;i++)
            Log.d("VoiceRevival", String.valueOf(drectionNumber[i]));
        //blockがtrueの時
       if(HttpCommunication.getBlock()) {

            Log.e("VoiceRevival","blockはtrue");
            Log.e("VoiceRevival","ナンバーを受け取ります");
            senNum = new SensorNumber();
            senNum.getCourse();
            Log.e("VoiceRevival","ナンバーを受け取りました");

           //配列の中身を拝見
            for(int i=0;i<4;i++)
                Log.d("VoiceRevival", String.valueOf(drectionNumber[i]));

           //とりあえずテスト用として全部の方向に進めるように設定
           for(int i = 0 ; i < drectionNumber.length ; i++)
               drectionNumber[i] = i+1;

           //最後に必ず0を入れる
           drectionNumber[3]=0;

            //音声再生
            startVoice();

            //ボタンをクリックすると音声再生
            reVoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVoice();
                }
            });
        }
        //blockがfalseの時
        else {
           Log.e("VoiceRevival", "blockはfalse");
           // 「行き止まりです」の音声を再生
           soundPool.play(voiceIds[4], 1.0f, 1.0f, 0, 0, 1);

           //ボタンをクリックすると音声再生
           reVoiceButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   soundPool.play(voiceIds[4], 1.0f, 1.0f, 0, 0, 1);
               }
           });
       }
    }

    //音声を再生するメソッド(非同期処理(Listenerクラス)はmainVoiceメソッド内で行われる)
    public void startVoice() {
        // 配列の中身を見てそれぞれに対応する音声を再生
        for (int i = 0; drectionNumber[i]!=END; i++) {
            switch(drectionNumber[i]) {
                //「前」に行ける場合
                case FRONT:
                    soundPool.play(voiceIds[FRONT - 1], 1.0f, 1.0f, 0, 0, 1);
                    break;
                //「右」に行ける場合
                case RIGHT:
                    soundPool.play(voiceIds[RIGHT - 1], 1.0f, 1.0f, 0, 0, 1);
                    break;
                //「左」に行ける場合
                case LEFT:
                    soundPool.play(voiceIds[LEFT - 1], 1.0f, 1.0f, 0, 0, 1);
                    break;
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
            }
        }
        //配列の中身が0なら
        if (drectionNumber[0]==END) {
            // 「行き止まりです」の音声を再生
            soundPool.play(voiceIds[4], 1.0f, 1.0f, 0, 0, 1);
        }
        else {
            // 「に進めます」の音声を再生
            soundPool.play(voiceIds[3], 1.0f, 1.0f, 0, 0, 1);
        }
    }


}