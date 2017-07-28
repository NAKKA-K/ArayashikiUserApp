package com.example.snakka.arayashikiuserapp;


import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;


public class BLEManager extends IntentService {
    private static Context guideContext;
    private static BluetoothAdapter bleAdapter;
    private String sensorNumStr;

    //別クラスを内部に保存する
    private static BLEScanner bleScanner = null;
    private static BLEGattGetter bleGattGetter = null;

    public BLEManager(String name){
        super(name);
        Log.e("ManagerStringじゃあ", "イケてる");
    }

    public BLEManager(){
        super("BLEManager");
        Log.e("Managerじゃあ", "イケてる");
    }

    @Override
    public void onCreate(){
        if(bleScanner == null){
            bleScanner = new BLEScanner(bleAdapter.getBluetoothLeScanner());
            Log.e("onCreateじゃあ", "BLEScannerが生成された");
        }
        if(bleGattGetter == null){
            bleGattGetter = new BLEGattGetter();
            Log.e("onCreateじゃあ", "BLEGattが生成された");
        }

        this.onBluetooth(); //Bluetoothを起動
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.e("onHandleIntentじゃあ", "イケてる");

        while(true){
            sensorNumGetter();

            setSensorPost();
        }
    }


    /** Adapterの取得 */
    private static void initBleAdapter(Context context){
        if(bleAdapter != null) return; //Adapterは複数作成しないようにすべき

        bleAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    /** 端末がBluetoothに対応しているか判定。非対応ならメッセージを表示 */
    public static boolean isBleSupport(Context context){
        initBleAdapter(context);

        if(bleAdapter == null){
            return false;
        }

        return true;
    }

    /** Bluetoothを強制的にONにする */
    public void onBluetooth(){
        if(bleAdapter.isEnabled() == false){
            bleAdapter.enable(); //強制的にBluetoothを起動する
        }
    }




    /** センサをスキャン、データの取得をしてそれぞれのクラスの内部に保存しておく */
    public void sensorNumGetter(){
        //TODO:HACK:仕様として同期処理があればそちらに変更する

        bleScanner.startScanDevice();

        //センサーがスキャンできればスキャンが停止して、isScanningがfalseに代わる
        while(bleScanner.getIsScanning()){}


        bleGattGetter.connectGatt(getContext(), bleScanner.getSensorDevice()); //切断は自動でしてくれる

        //センサ番号の取得待ち
        while(bleGattGetter.isGattGot() == false){}
    }


    public void setSensorPost(){
        try {
            sensorNumStr = new String(bleGattGetter.getSensorNum(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("onPostExecute()", "センサー番号 = " + sensorNumStr);
        Toast.makeText(getContext(), "センサー番号 = " + sensorNumStr, Toast.LENGTH_LONG).show();

        HttpCommunication.setSensorList(sensorNumStr);
    }


    @Override
    public void onDestroy(){
        bleScanner.cancelScanner();
        bleGattGetter.cancelGattGetter();
    }


    public String getSensorNumStr(){ return sensorNumStr; }
    public void setSensorNumStr(String numStr){ sensorNumStr = numStr; }

    public static void setContext(Context context){ guideContext = context; }
    private Context getContext(){ return guideContext; }
}

