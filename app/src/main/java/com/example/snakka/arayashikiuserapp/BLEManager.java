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


public class BLEManager extends AsyncTask<Void, Void, Void> {
    private static Context context;
    private static BluetoothAdapter bleAdapter;
    private String sensorNumStr;

    //別クラスを内部に保存する
    private static BLEScanner bleScanner = null;
    private static BLEGattGetter bleGattGetter = null;


    public BLEManager(){
        if(bleScanner != null) bleScanner = new BLEScanner(bleAdapter.getBluetoothLeScanner());
        if(bleGattGetter != null) bleGattGetter = new BLEGattGetter();
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
    public void onBluetooth(Activity activity){
        if(bleAdapter.isEnabled() == false){
            bleAdapter.enable(); //強制的にBluetoothを起動する
        }
        this.context = activity;
    }




    @Override
    protected Void doInBackground(Void... params) {
        //TODO:HACK:AsyncTaskで長時間のループは避けるべきとのこと
        while(isCancelled() == false){
            sensorNumGetter();

            setSensorPost();
        }

        return null;
    }

    /** センサをスキャン、データの取得をしてそれぞれのクラスの内部に保存しておく */
    public void sensorNumGetter(){
        //TODO:HACK:仕様として同期処理があればそちらに変更する

        bleScanner.startScanDevice();

        //センサーがスキャンできればスキャンが停止して、isScanningがfalseに代わる
        while(bleScanner.getIsScanning()){
            if(isCancelled()) return;
        }


        bleGattGetter.connectGatt(context, bleScanner.getSensorDevice()); //切断は自動でしてくれる

        //センサ番号の取得待ち
        while(bleGattGetter.isGattGot() == false){
            if(isCancelled()) return;
        }
    }


    public void setSensorPost(){
        try {
            sensorNumStr = new String(bleGattGetter.getSensorNum(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("onPostExecute()", "センサー番号 = " + sensorNumStr);
        //Toast.makeText(context, "センサー番号 = " + sensorNumStr, Toast.LENGTH_LONG).show();

        HttpCommunication.setSensorList(sensorNumStr);
    }


    @Override
    protected void onCancelled(){
        bleScanner.cancelScanner();
        bleGattGetter.cancelGattGetter();
    }


    public String getSensorNumStr(){ return sensorNumStr; }
    public void setSensorNumStr(String numStr){ sensorNumStr = numStr; }

    public static Context getGuideContext(){ return context; }
    public static void setContext(Context context){ BLEManager.context = context; }
}

