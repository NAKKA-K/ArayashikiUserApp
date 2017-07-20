package com.example.snakka.arayashikiuserapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BLEScanner {
    private static BluetoothLeScanner bleLeScanner;
    private static ScanCallback scanCallback;
    private static BluetoothDevice sensorDevice;
    private boolean isScanning = false;

    private static boolean isEndScan = false;
    private static String SENSOR_UUID;

    public BLEScanner(BluetoothLeScanner bleLeScanner, String uuid){
        this.bleLeScanner = bleLeScanner;
        this.SENSOR_UUID = uuid;

        scanCallback = initScanCallback();
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

                //TODO:指定のデバイスが見つかったら代入
                BluetoothDevice device = result.getDevice();
                if(isSensorDevice(device) == false){
                    setSensorDevice(device);

                    //デバック用
                    Log.d("ScanDevice address:", device.getAddress());
                    Log.d("ScanDevice name:", device.getName());

                    String deviceName = result.getScanRecord().getDeviceName();
                    Log.i("ScanDevice", deviceName); //取得したデバイスの名前をlogに流す


                    BLEScanner.setTrueToIsEndScan(); //目的のデバイスのスキャンが終了したことを示すフラグをオンにする
                }

            }


            /* バッチ結果が配信されるときのコールバック。onBatchScanResults(List<ScanResult> results)
             * @param results 以前にスキャンされた結果のリスト。
             */
            /*スキャンを開始できなかったときのコール。onScanFailed(int errorCode)
             *@param errorCode スキャン失敗したときのエラーコード(SCAN_FAILED_* の中の1つ)
             */
        };
    }


    /** deviceListにデバイスをセットする */
    public static void setSensorDevice(BluetoothDevice device){
        sensorDevice = device;
    }

    /** 保存されているセンサーと検出したセンサーが同じならtrue */
    public boolean isEqualDevice(BluetoothDevice device){
        if (this.sensorDevice != null && this.sensorDevice == device){
            return true;
        }else{
            return false;
        }
    }

    /** deviceがセンサーであるか？ */
    public static boolean isSensorDevice(BluetoothDevice device){
        ParcelUuid[] uuids = device.getUuids();
        if (uuids == null) return false;

        //deviceのUUIDの中に、センサーと同じUUIDが存在すればセンサーと判定する
        for(ParcelUuid uuid : uuids){
            if(uuid.toString() == SENSOR_UUID){
                return true;
            }
        }

        return false;
    }



    /** BLEManagerで作られたインスタンスから呼び出される */
    public void startScanDevice(){
        if(isScanning == true) return;

        isEndScan = false;
        isScanning = true;
        bleLeScanner.startScan(scanCallback);
    }

    /** BLEManagerで作られたインスタンスから呼び出される */
    public void stopScanDevice() {
        if(isScanning == false) return;

        isScanning = false;
        bleLeScanner.stopScan(scanCallback);
    }


    public boolean isScanning(){
        return isScanning;
    }

    public static BluetoothDevice getSensorDevice() { return sensorDevice; }

    public static void setTrueToIsEndScan(){ isEndScan = true; }
    public boolean getIsEndScan(){ return isEndScan; }
}
