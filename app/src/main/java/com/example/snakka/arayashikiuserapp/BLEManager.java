package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.widget.Toast;


public class BLEManager{
    private Context context;
    private static BluetoothAdapter bleAdapter = null;
    private static BLEScanner bleScanner;
    private static BLEGattGetter bleGattGetter;




    public BLEManager(Context context){
        this.context = context;


        initBleAdapter();

        bleScanner = new BLEScanner(bleAdapter.getBluetoothLeScanner());
        bleGattGetter = new BLEGattGetter();
    }


    private void initBleAdapter(){
        if(bleAdapter != null) return; //Adapterは複数作成しないようにすべき

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();
    }

    /** 端末がBluetoothに対応しているか判定。非対応ならメッセージを表示 */
    public boolean isBleSupport(){
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


    //TODO:非常にひどい一時的な実装
    public void sensorNumGetter(){
        bleScanner.startScanDevice();

        //スキャン中は続ける
        while(bleScanner.isScanning()){
            bleScanner.stopScanDevice();
        }

        //TODO:正当な端末を自動で探す処理が必要

        bleGattGetter.connectGatt(context, bleScanner.getDevice()); //切断は自動でしてくれる

        //センサ番号の取得待ち
        while(bleGattGetter.isGattGot()){}
    }

}
