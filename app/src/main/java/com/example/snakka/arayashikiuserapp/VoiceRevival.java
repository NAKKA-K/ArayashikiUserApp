package com.example.snakka.arayashikiuserapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import static com.example.snakka.arayashikiuserapp.SensorNumber.END;
import static com.example.snakka.arayashikiuserapp.SensorNumber.FRONT;
import static com.example.snakka.arayashikiuserapp.SensorNumber.LEFT;
import static com.example.snakka.arayashikiuserapp.SensorNumber.RIGHT;

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
    //方向を示す文字列を格納した配列
    private static final String[] directionTexts =
            {
                    "前",
                    "右",
                    "左"
            };

    //directionNumberGetメソッドから方向ナンバーを受け取るためのフィールド
    private int[] directionNums = new int[4];
    //読み込みをした際に受け取る音声Id配列
    public static final int[] voiceIds = new int[6];
    // TextViewに表示する文字列を格納変数(後に戻り値となる)
    public String viewString = "";

    // 初期化
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VoiceRevival(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        } else {
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

    //音声を再生するメソッド(非同期処理はVoiceGuideActivity内で行われる)
    public void startVoice() {
        // 配列の中身を見てそれぞれに対応する音声を再生
        // directionTextsが3つの配列なので条件文はlength-1にしておく
        for (int i = 0; directionNums[i]!=END; i++) {
            //「前」に行ける場合
            if (directionNums[i] == FRONT) {
                // ここで行ける方向の音声を再生
                soundPool.play(voiceIds[FRONT - 1], 1.0f, 1.0f, 0, 0, 1);
                //音声再生の待ち時間
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                }
            }
            //「右」に行ける場合
            else if (directionNums[i] == RIGHT) {
                soundPool.play(voiceIds[RIGHT - 1], 1.0f, 1.0f, 0, 0, 1);
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                }
            }
            //「左」に行ける場合
            else {
                soundPool.play(voiceIds[LEFT - 1], 1.0f, 1.0f, 0, 0, 1);
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                }
            }
        }
        //配列の中身が0なら
        if (directionNums[0]==END) {
            // 「行き止まりです」の音声を再生
            soundPool.play(voiceIds[4], 1.0f, 1.0f, 0, 0, 1);
        }
        else {
            // 「に進めます」の音声を再生
            soundPool.play(voiceIds[3], 1.0f, 1.0f, 0, 0, 1);
        }
    }

    //行ける方向の文字列を返すメソッド(同期処理)
    public String viewVoice()
    {
        // 方向ナンバーを受け取る
        senNum = new SensorNumber();
        directionNums = senNum.getCourse();

        //先に同期処理を終わらせるために文字列を返す
        for (int i = 0; directionNums[i]!=END; i++) {
            //「前」に行ける場合
            if (directionNums[i] == FRONT) {
                // 前 右・・・といった感じでスペースを空けながら表示するため空白を格納
                viewString += directionTexts[FRONT-1] + " ";
            }
            //「右」に行ける場合
            else if(directionNums[i] == RIGHT) {
                viewString += directionTexts[RIGHT-1] + " ";
            }
            //「左」に行ける場合
            else {
                viewString += directionTexts[LEFT-1] + " ";
            }
        }
        return viewString;
    }

}