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
    private int[] sensorNums = new int[4];
    //音声リソース配列
    private static final int voiceResources[] =
            {
                    R.raw.front, //前
                    R.raw.right, //右
                    R.raw.left,  //左
                    R.raw.start, //に進めます
                    R.raw.stop   //行き止まりです
            };
    //方向を示す文字列を格納した配列
    private static final String[] directionTexts =
            {
                    "前",
                    "右",
                    "左"
            };
    //読み込みをした際に受け取る音声Id配列(2回目以降は固定化される)
    public static final int[] voiceIds = new int[6];
    //音声ファイル読み込みの可否を格納する配列
    private boolean loadSuccessd[] =
            {
                    false,
                    false,
                    false,
                    false,
                    false
            };
    // 読み込みが成功する度に1ずつ加算していく値
    // 読み込みが完了しているかどうかをチェックする関数内で
    // 読み込み成否を格納する配列の添え字に使用する
    private int loadSuccessIdx = 0;
    // TextViewに表示する文字列を格納変数(後に戻り値となる)
    public String viewString = "";
    private HttpCommunication httpCommunication;

    private static int logNum;
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
        if(!HttpCommunication.getBlock()) {
            Log.e("HttpCommunication","blockはfalse");
            // 方向ナンバーを受け取る
            Log.e("SensorNumber","ナンバーを受け取ります：" );
            senNum = new SensorNumber();
            sensorNums = senNum.getCourse();

            Log.e("SensorNumber","ナンバーを受け取りました："
                    + Integer.toString(sensorNums[0])
                    + Integer.toString(sensorNums[1])
                    + Integer.toString(sensorNums[2])
                    + Integer.toString(sensorNums[3]));

            //音声再生
            Log.d("sensorCurrent & Back",Integer.toString(senNum.getCurrentNum()) + Integer.toString(logNum));
            if(senNum.getCurrentNum() != logNum) {
                startVoice();
                logNum = senNum.getCurrentNum();
            }
            //ボタンをクリックすると音声再生
            reVoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVoice();
                }
            });
        }
    }

    //音声を再生するメソッド(非同期処理(Listenerクラス)はmainVoiceメソッド内で行われる)
    public void startVoice() {
        // 配列の中身を見てそれぞれに対応する音声を再生
        for (int i = 0; sensorNums[i]!=END; i++) {
            switch(sensorNums[i]) {
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
        if (sensorNums[0]==END) {
            // 「行き止まりです」の音声を再生
            soundPool.play(voiceIds[4], 1.0f, 1.0f, 0, 0, 1);
        }
        else {
            // 「に進めます」の音声を再生
            soundPool.play(voiceIds[3], 1.0f, 1.0f, 0, 0, 1);
        }
    }

}