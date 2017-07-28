package com.example.snakka.arayashikiuserapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;


public class BLEScanner {
    private static BluetoothLeScanner bleLeScanner;
    private static ScanCallback scanCallback;
    private static BluetoothDevice sensorDevice;
    private boolean isScanning = false;


    public BLEScanner(BluetoothLeScanner bleLeScanner){
        this.bleLeScanner = bleLeScanner;

        scanCallback = initScanCallback();
    }


    /** scanCallback変数を初期化するメソッド */
    private ScanCallback initScanCallback(){
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                BluetoothDevice device = result.getDevice();

                //センサーで無いか、同じセンサであれば用無し
                if(isSensorDevice(device.getName()) == false || isEqualDevice(device)) return;

                setSensorDevice(device);
                Log.e("ScanCallback()", "取得したデバイス名 = " + device.getName());

                stopScanDevice();
            }
        };
    }



    /** 保存されているセンサーと検出したセンサーが同じならtrue */
    public boolean isEqualDevice(BluetoothDevice device){
        if (this.sensorDevice != null && this.sensorDevice == device){
            return true;
        }
        return false;
    }


    /** deviceがセンサーであるか？ */
    public static boolean isSensorDevice(String deviceName){
        if ("arayashiki".equals(deviceName)) return true;

        return false;
    }



    /** BLEManagerで作られたインスタンスから呼び出される */
    public void startScanDevice(){
        if(isScanning == true) return;

        Log.d("startScanDevice()", "デバイススキャン開始");
        setIsScanning(true);
        bleLeScanner.startScan(scanCallback);
    }

    /** BLE通信を停止して、スキャン中のフラグを解除する */
    public void stopScanDevice() {
        if(isScanning == false) return;

        bleLeScanner.stopScan(scanCallback);
        setIsScanning(false);
    }



    /** Activityがcencelしたときに呼ぶべき処理 */
    public void cancelScanner(){
        stopScanDevice();
        setSensorDevice(null);
    }


    //getter,setter
    public boolean getIsScanning(){ return isScanning; }
    public void setIsScanning(boolean scanning) { isScanning = scanning; }

    public static BluetoothDevice getSensorDevice() { return sensorDevice; }
    public static void setSensorDevice(BluetoothDevice device){ sensorDevice = device; }

}
