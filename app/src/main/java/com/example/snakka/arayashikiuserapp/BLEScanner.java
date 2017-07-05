package com.example.snakka.arayashikiuserapp;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BLEScanner {
    private static final ScanCallback scanCallback = initScanCallback();
    private static ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private boolean isScanning = false;
    private BluetoothLeScanner bleLeScanner;

    public BLEScanner(BluetoothLeScanner bleLeScanner){
        this.bleLeScanner = bleLeScanner;
    }


    /** scanCallback変数を初期化するメソッド */
    private static ScanCallback initScanCallback(){
        return new ScanCallback() {
            /**
             * BLE端末が見つかった場合のコールバック。
             * @param callbackType このコールバックがどのように発動されたのか決定する。どれか1つの可能性があります。
             *                     {ScanSettings#CALLBACK_TYPE_ALL_MATCHES}
             *                     {ScanSettings#CALLBACK_TYPE_FIRST_MATCH}
             *                     {ScanSettings#CALLBACK_TYPE_MATCH_LOST}
             * @param result       BLEで受信した結果オブジェクト。
             */
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                BluetoothDevice device = result.getDevice();
                if(isAdded(device) == false){
                    addDevice(device);

                    Log.d("ScanDevice address:", device.getAddress());
                    Log.d("ScanDevice name:", device.getName());

                    String deviceName = result.getScanRecord().getDeviceName();
                    Log.i("ScanDevice", deviceName); //取得したデバイスの名前をlogに流す
                }

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
    public static void addDevice(BluetoothDevice device){
        /* //TODO:どちらの実装が良いか
        if(deviceList == null){
            deviceList = new ArrayList<BluetoothDevice>();
        }*/
        deviceList.add(device);
    }

    /** deviceListにスキャンしたデバイスが追加されているかどうか */
    public static boolean isAdded(BluetoothDevice device){
        if(deviceList != null && deviceList.isEmpty() == false){
            return deviceList.contains(device);
        }
        return false;
    }


    /** BLEManagerで作られたインスタンスから呼び出される */
    public void startScanDevice(){
        if(isScanning == true) return;

        isScanning = true;
        bleLeScanner.startScan(scanCallback);
    }

    /** BLEManagerで作られたインスタンスから呼び出される */
    public void stopScanDevice() {
        if(isScanning == false) return;

        isScanning = false;
        bleLeScanner.stopScan(scanCallback);
    }


    public static ArrayList getDeviceList(){
        return deviceList;
    }

}
