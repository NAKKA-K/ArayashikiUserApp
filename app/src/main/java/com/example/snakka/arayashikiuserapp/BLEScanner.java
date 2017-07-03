package com.example.snakka.arayashikiuserapp;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BLEScanner {
    private static ScanCallback scanCallback;
    private static ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    public BLEScanner(){
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

                if(isAdded(result.getDevice()) == false){
                    addDevice(result.getDevice());
                }

                String deviceName = result.getScanRecord().getDeviceName();
                Log.i("ScanDevice", deviceName); //取得したデバイスの名前をlogに流す
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


    /** deviceListにデバイスをセットする */
    public void addDevice(BluetoothDevice device){
        /* //TODO:どちらの実装が良いか
        if(deviceList == null){
            deviceList = new ArrayList<BluetoothDevice>();
        }*/
        deviceList.add(device);
    }

    /** deviceListにスキャンしたデバイスが追加されているかどうか */
    public boolean isAdded(BluetoothDevice device){
        if(deviceList != null && deviceList.isEmpty() == false){
            return deviceList.contains(device);
        }
        return false;
    }

}
