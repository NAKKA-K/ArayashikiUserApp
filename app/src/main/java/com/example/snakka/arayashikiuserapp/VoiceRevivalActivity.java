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
    int id_SOUND[]={
            R.raw.front, //前に進むよう指示する音声
            R.raw.right, //右に曲がるよう　〃
            R.raw.left,  //左　　　　〃
    };
    int id_SENSOR_NUM,i=0; //センサのナンバーを受け取るためのフィールド

    @SuppressWarnings("deprecation") //soundpoolの警告回避
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_revival);


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
        for (i = 0; i < id_SOUND.length; i++)
        {
            id_SOUND[i] = sp.load(this, id_SOUND[i], 1);
        }
    }

    //センサのナンバーを受け取るメソッド
    public void testNumberGet()
    {
        Random rand = new Random(); //とりあえず今はランダムに生成した数をセンサのナンバーとする
        do{
            id_SENSOR_NUM = rand.nextInt(3);
        }while(id_SENSOR_NUM==0); //0の時ループを続ける

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
                testNumberGet(); //センサのナンバーを受け取る
                if (id_SENSOR_NUM==1) { //受け取ったセンサのナンバーが1の場合は前へ進むよう指示する音声を流す
                    textView.setText(R.string.front);
                    sp.play(id_SOUND[0], 1, 1, 0, 0, 1);
                }
                else if(id_SENSOR_NUM==2){ //2なら右に曲がるよう指示
                    textView.setText(R.string.right);
                    sp.play(id_SOUND[1], 1, 1, 0, 0, 1);
                }
                else{ //3なら左に曲がるよう指示
                    textView.setText((R.string.left));
                    sp.play(id_SOUND[2], 1, 1, 0, 0, 1);
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


}
