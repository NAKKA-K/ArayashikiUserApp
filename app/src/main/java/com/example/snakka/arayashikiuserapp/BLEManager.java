package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class BLEManager extends AsyncTask<Void, Void, Void> implements OnCancelListener {
    private static Context context;
    private static BluetoothAdapter bleAdapter;
    private static String sensorNumStr;
    private static ProgressDialog proDialog;

    private static final String SENSOR_UUID = "ABCD";


    //別クラスを内部に保存する
    private static BLEScanner bleScanner;
    private static BLEGattGetter bleGattGetter;


    public BLEManager(Context context){
        this.context = context;

        bleScanner = new BLEScanner(bleAdapter.getBluetoothLeScanner(), SENSOR_UUID);
        bleGattGetter = new BLEGattGetter(SENSOR_UUID);
    }


    /** Adapterの取得 */
    private static void initBleAdapter(){
        if(bleAdapter != null) return; //Adapterは複数作成しないようにすべき

        bleAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    /** 端末がBluetoothに対応しているか判定。非対応ならメッセージを表示 */
    public static boolean isBleSupport(){
        initBleAdapter();

        if(bleAdapter == null){
            Toast.makeText(context, "Bluetoothに対応していません", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /** Bluetoothを強制的にONにする */
    public void onBluetooth(Activity activity){
        if(bleAdapter.isEnabled() == false){
            bleAdapter.enable(); //強制的にBluetoothを起動する
        }
    }


    @Override
    protected void onPreExecute(){
        //Progress Dialog
        proDialog = new ProgressDialog(context);
        proDialog.setCancelable(true);
        proDialog.setOnCancelListener(this);
    }

    @Override
    protected Void doInBackground(Void... params) {
        sensorNumGetter();

        return null;
    }

    /** センサをスキャン、データの取得をしてそれぞれのクラスの内部に保存しておく */
    public void sensorNumGetter(){
        //HACK:非常にひどい一時的な実装
        bleScanner.startScanDevice(); //HACK:このままでは永遠にスキャンし続ける

        //TODO:HACK:仕様として同期処理があればそちらに変更する
        while(bleScanner.getIsScanning()){} //センサーがスキャンできればスキャンが停止して、isScanningがfalseに代わる


        bleGattGetter.connectGatt(context, bleScanner.getSensorDevice()); //切断は自動でしてくれる

        //センサ番号の取得待ち
        while(bleGattGetter.isGattGot() == false){} //未取得の場合
    }


    @Override
    protected void onPostExecute(Void params){
        //TODO:センサ番号が取得できたので、Byte[]をStringに変換してしかるべき場所にsetする
        try {
            sensorNumStr = new String(bleGattGetter.getSensorNum(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("sensorNumStr:", sensorNumStr);


        //setSensorList(sensorNumStr);


        //TODO:HACK:場合によっては、センサーのスキャン終了時に呼び出す方が良い場合もある
        this.execute(); //スレッドが終了する直前に、次のスレッドを開始してBLE通信を続ける
    }



    //ProgressDialogでキャンセルが入力された時に呼ばれる
    @Override
    public void onCancel(DialogInterface dialog){
        bleScanner.pauseScanner();
        bleGattGetter.pauseGattGetter();
    }



    /* //こんな感じの奴作ってくれれば、こちらからsetします
    static ArrayList<String> sensorList = new ArrayList<String>();
    public static void setSensorList(String sensorStr){
        if(sensorList.get(sensorList.size() - 1) == sensorStr) return; //連続して同じセンサーは受け付けない

        sensorList.add(sensorStr); //センサーを順次追加
    }

    public static String getSensorList(){
        if(sensorList == null) return null; //Listがnullでないか

        //先頭から取得し、取得したら破棄
        String str = sensorList.get(0);
        sensorList.remove(0);

        return str;
    }
    */

    public String getSensorNumStr(){ return sensorNumStr; }
    public void setSensorNumStr(String numStr){ sensorNumStr = numStr; }

    public ProgressDialog getProDialog(){ return proDialog; }
}

/*
    private static BLEManager bleMgr;

onCreate
        //BLE通信
        initBLE();
onResume
        //BLE
        Log.d("onResume", "scan開始だってばよ");
        bleMgr.execute(); //BLEスキャン開始
onPause
        //BLE
        bleMgr.getProDialog().cancel();
        Log.d("onPause", "cancelだってばよ");

BLE通信をするために必要な前準備を実装したメソッド
private void initBLE(){
    bleMgr = new BLEManager(this);

    bleMgr.onBluetooth(this); //Bluetoothを起動
}

*/
