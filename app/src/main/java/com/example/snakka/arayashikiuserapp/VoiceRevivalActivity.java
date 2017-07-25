package com.example.snakka.arayashikiuserapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import java.util.Random;
//import static com.example.snakka.arayashikiuserapp.SensorNumber.END;

public class VoiceRevivalActivity {
    //音声の属性を指定するクラス
    private AudioAttributes audioAttributes;
    //音声の読み込みや再生を行えるクラス
    private SoundPool soundPool;
    //センサーの方向ナンバーを受け取れるクラス
    private SensorNumber senNum;

    //音声リソース配列
    private int voiceResources[] =
            {
                    R.raw.front, //前
                    R.raw.right, //右
                    R.raw.left,  //左
                    R.raw.start, //に進めます
                    R.raw.stop   //行き止まりです
            };
    //方向を示す文字列を格納した配列
    private String[] directionTexts =
            {
                    "前",
                    "左",
                    "右"
            };

    //directionNumberGetメソッドから方向ナンバーを受け取るためのフィールド
    private int[] directionNums = new int[4];
    //読み込みをした際に受け取る音声Id配列
    private int[] voiceIds = new int[6];
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
    // 上記の読み込み成否を格納する配列の添え字に使用する
    private int loadSuccessIdx = 0;
    // TextViewに表示する文字列を格納変数(後に戻り値となる)
    private String viewString = "";

    // 初期化
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VoiceRevivalActivity(Context context){

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
                .setMaxStreams(4)
                .build();

        //音声をロード
        for (int i = 0; i < voiceResources.length; i++)
            voiceIds[i] = soundPool.load(context,voiceResources[i], 1);
    }

    //音声を再生するメソッド
    public String startVoice() {
        // 読み込みが終わったかどうか確認ができるクラスをインスタンス化
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            // 読み込みが終わったか確認する関数
            // 読み込みが非同期で行われているためか、
            // この関数も非同期で行われるっぽい
            // 1読み込みにつき1回この関数が呼ばれる
            // そのため同じ音声に関しては1度読み込まれれば解放→再度読み込みとしない限り呼ばれることはない(byよーすけさん)
            @Override
            public void onLoadComplete(SoundPool _SoundPool, int _SampleId, int _Status) {
                // 読み込みが成功している(_Statusが0)なら
                if (_Status == 0)
                {
                    // 読み込み成否配列にtrueを入れて添え字をずらす
                    loadSuccessd[loadSuccessIdx++] = true;
                    // デバッグ用としてとりあえず鳴らす(現段階でここはちゃんと鳴るようになった！)
                    soundPool.play(_SampleId, 1.0f, 1.0f, 0, 0, 1.0f);
                }
            }
        });

        // 全部の音声の読み込みが成功しているか確認するローカル変数
        boolean loadEnd = false;
        // 全部の音声が読み込み成功しているかを判定
        if (loadSuccessd[0] && loadSuccessd[1] && loadSuccessd[2] && loadSuccessd[3] && loadSuccessd[4])
        {
            // 成功しているならtrueを格納
            loadEnd = true;
        }

        // 無事全部読み込めてたら(なぜか読み込めているはずなのにここのifを通ってくれない・・・。)
        if (loadEnd)
        {
            // ランダムでナンバーを受け取る(テスト用)
            directionNumberGet();

            // 方向ナンバーを受け取る(本番用)
            // senNum = new SensorNumber();
            //directionNums = senNum.getCourse();

            /*//テスト用として全ての方向に行けるとし、音声を鳴らしてみる
            for(int i=0;i<directionNums.length-1;i++)
            {
                directionNums[i]=1;
            }*/

            // 配列の中身を見てそれぞれに対応する音声を再生
            // directionTextsが3つの配列なので条件文はlength-1にしておく
            for (int i = 0;i<directionNums.length-1; i++)
            {
                if (directionNums[i] == 1)
                {
                    // 前 右・・・といった感じでスペースを空けながら表示するため空白を格納
                    viewString += directionTexts[i] + " ";
                    // ここで行ける方向の音声を再生
                    soundPool.play(voiceIds[i], 1.0f, 1.0f, 0, 0, 1);
                }
            }

            // 前、右、左なら
            if (directionNums[0] == 1 || directionNums[1] == 1 || directionNums[2] == 1)
            {
                // 「に進めます」の音声を再生
                soundPool.play(voiceIds[3], 1.0f, 1.0f, 0, 0, 1);
            }
            else if(directionNums[0] == 0 && directionNums[1] == 0 && directionNums[2] == 0)
            {
                // 「行き止まりです」の音声を再生
                soundPool.play(voiceIds[4], 1.0f, 1.0f, 0, 0, 1);
            }
        }
        //文字列を返す
        return viewString;
    }

    // メモリを解放するメソッド
    public void releaseVoice() {
        //SoundPoolの解放
        soundPool.release();
    }

    // ナンバーを受け取るメソッド(テスト用)
    public void directionNumberGet() {
        int i = 0;
        // とりあえず今はランダムに生成した数を方向ナンバーとする(本来はNumberOperationクラス内にあるメソッドから受け取る
        Random rand = new Random();
        while (i < directionNums.length-1)
        {
            directionNums[i] = rand.nextInt(2);
            if (directionNums[i] == 0 || directionNums[i] == 1)
                i++; //0の場合はその方向(前右左のいずれか)に進めなくて1の場合はその方向に進める
        }
    }
}