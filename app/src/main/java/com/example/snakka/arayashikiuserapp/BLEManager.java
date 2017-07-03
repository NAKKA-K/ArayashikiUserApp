package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BLEManager {
    private Context context;
    private static BluetoothAdapter bleAdapter = null;
    private final static int REQUEST_ENABLE_BT = 1;
    private static ScanCallback scanCallback;
    private static ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();


    public BLEManager(Context context){
        this.context = context;

        scanCallback = initScanCallback();
    }

    /** scanCallback変数を初期化するメソッド */
    private ScanCallback initScanCallback(){
        return new ScanCallback() {
            /**
             * BLE端末が見つかった場合のコールバック。
             * @param callbackType このコールバックがどのように発動されたのか決定する。どれか1つの可能性があります。
             *                     {link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
             *                     {link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
             *                     {link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
             * @param result       BLEで受信した結果オブジェクト。
             */
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
            }

            /**
             * バッチ結果が配信されるときのコールバック。
             * @param results 以前にスキャンされた結果のリスト。
             */
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            /**
             * スキャンを開始できなかったときのコール。
             * @param errorCode スキャン失敗したときのエラーコード(SCAN_FAILED_* の中の1つ)
             */
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
    }


    /** 端末がBluetoothに対応しているか判定。非対応ならメッセージを表示 */
    public boolean isBleSupport(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        //古い実装？:bleAdapter = BluetoothAdapter.getDefaultAdapter();
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

    /** ArrayListにスキャンしたデバイスが追加されているかどうか */
    public boolean isAdded(BluetoothDevice device){
        if(deviceList != null && deviceList.isEmpty() == false){
            return deviceList.contains(device);
        }
        return false;
    }
}
