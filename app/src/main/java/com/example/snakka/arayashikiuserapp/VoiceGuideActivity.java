package com.example.snakka.arayashikiuserapp;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import static com.example.snakka.arayashikiuserapp.VoiceRevival.soundPool;


public class VoiceGuideActivity extends AppCompatActivity{
    private VoiceRevival voiceRev;
    private TextView textView1,textView2;
    private Button reVoice;
    private String viewString;
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


        //行ける方向の文字列を返す(先に同期処理を終わらせてしまう)
        viewString=voiceRev.viewVoice();
        // 返ってきたviewStringの中身が空のままだったら行き止まりと判定
        if(viewString.isEmpty()) {
            textView2.setText("行き止まりです");
        }
        else {
            textView1.setText(viewString);
            textView2.setText("に進めます");
        }

        // 読み込みが終わったか確認する関数
        // 読み込みが非同期で行われているためか、
        // この関数も非同期で行われるっぽい
        // 1読み込みにつき1回この関数が呼ばれる
        // そのため同じ音声に関しては1度読み込まれれば解放→再度読み込みとしない限り呼ばれることはない(byよーすけさん)
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                // 読み込みが成功している(statusが0)なら
                //startVoiceメソッド内に同期処理を記述
                if(status==0) {
                    loadSuccessd[loadSuccessIdx++] = true;
                     // 無事全部読み込めてたら
                    if (loadSuccessd[0] && loadSuccessd[1] && loadSuccessd[2] && loadSuccessd[3] && loadSuccessd[4]) {
                       //音声再生
                        voiceRev.startVoice();
                    }
                }
            }
        });

        //ボタンをクリックすると音声再生
        reVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 voiceRev.startVoice();
            }
        });
    }

}
