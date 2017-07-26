package com.example.snakka.arayashikiuserapp;

        import android.media.AudioAttributes;
        import android.media.AudioManager;
        import android.media.SoundPool;
        import android.os.Build;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.TextView;
        import java.util.Random;

public class VoiceRevivalActivity extends AppCompatActivity {

    SoundPool sp=null;
    int directionVoice[]={
            R.raw.front, //前に進むよう指示する音声
            R.raw.right, //右に曲がるよう　〃
            R.raw.left,  //左　　　　〃
    };
    int directionNum; //directionNumberGetメソッドから方向ナンバーを受け取るためのフィールド

    private static BLEManager bleMgr;


    @SuppressWarnings("deprecation") //soundpoolの警告回避
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_revival); //仮の画面を表示

        //互換性を保つためにインスタンスを2つ用意(初期化)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {  //バージョンによってインスタンス生成の仕方を変える
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0); //ver21以下
        } else {  //ver21以上
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            sp = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(1)
                    .build();
        }

        //音声読み込み
        for (int i = 0; i < directionVoice.length; i++)
        {
            directionVoice[i] = sp.load(this, directionVoice[i], 1);
        }



        //BLE通信
        initBLE();
    }

    //ナンバーを受け取るメソッド
    public void directionNumberGet()
    {
        Random rand = new Random(); //とりあえず今はランダムに生成した数を方向ナンバーとする(本来はNumberOperationクラス内にあるメソッドから受け取る)
        do{
            directionNum = rand.nextInt(4);
        }while(directionNum==0); //0の時ループを続ける
    }

    //音声を再生
    @Override
    protected void onResume()
    {
        super.onResume();
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                TextView textView = (TextView) findViewById(R.id.textView); //TextViewオブジェクト生成
                directionNumberGet(); //方向ナンバーを受け取る
                if (directionNum==1) { //受け取ったセンサのナンバーが1の場合は前へ進むよう指示する音声を流す
                    textView.setText(R.string.front);
                    sp.play(directionVoice[0], 1, 1, 0, 0, 1);
                }
                else if(directionNum==2){ //2なら右に曲がるよう指示
                    textView.setText(R.string.right);
                    sp.play(directionVoice[1], 1, 1, 0, 0, 1);
                }
                else{ //3なら左に曲がるよう指示
                    textView.setText((R.string.left));
                    sp.play(directionVoice[2], 1, 1, 0, 0, 1);
                }

            }
        });
    }

    //メモリの解放
    @Override
    protected void onPause(){
        super.onPause();
        sp.release();
    }


    /** BLE通信をするために必要な前準備を実装したメソッド */
    private void initBLE(){
        bleMgr = new BLEManager(this);

        bleMgr.onBluetooth(this); //Bluetoothを起動
    }

}
