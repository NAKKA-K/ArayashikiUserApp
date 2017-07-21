package com.example.snakka.arayashikiuserapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BLEの実装がむちゃくちゃなため、テストとリファクタリングのために一度合体させてみる
 *
 */

public class TestBLEClass {
    private Context context;
    private static BluetoothAdapter bleAdapter;

    private static BluetoothDevice sensorDevice;
    private boolean isScanning = false;
    private static BluetoothLeScanner bleLeScanner;
    private static ScanCallback scanCallback;
    private static final String SENSOR_UUID = "ABCD";

    private static BluetoothGattCallback bleGattCallback;
    private BluetoothGatt bleGatt;
    private boolean isGattGot = false;
    private byte[] sensorNum;



    public TestBLEClass(Context context){
        this.context = context;

        initBleAdapter();

        this.bleLeScanner = bleAdapter.getBluetoothLeScanner();

        scanCallback = initScanCallback();
        bleGattCallback = initGattCallback();
    }


    /** Adapterの取得 */
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
        startScanDevice();//HACK:このままでは永遠にスキャンし続ける

        //TODO:スキャンが終わるまで待つ
        while(isScanning()){
            stopScanDevice();
        }

        connectGatt(context, getSensorDevice()); //切断は自動でしてくれる

        //センサ番号の取得待ち
        while(isGattGot()){}
    }



    //-------------------------------------------------------------------------------------------

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
        if (this.sensorDevice != null || this.sensorDevice == device){
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



    //------------------------------------------------------------------------------------------




    private BluetoothGattCallback initGattCallback(){
        return new BluetoothGattCallback() {
            /**
             * GATTクライアントがリモートGATTサーバに接続、切断されたことを示すコールバック。
             *
             * @param gatt     GATTクライアント
             * @param status   接続、切断操作のステータス
             *                 {BluetoothGatt#GATT_SUCCESS}成功すると返されます
             * @param newState 新しい接続状態を返します。以下のどちらかになります。
             *                 {BluetoothProfile#STATE_DISCONNECTED}
             *                 {BluetoothProfile#STATE_CONNECTED}
             */
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState != BluetoothProfile.STATE_CONNECTED) return;

                //bleGatt = gatt; //こういう記述があったが必要なのか？
                bleGatt.discoverServices();
            }

            /**
             * Callback invoked when the list of remote services, characteristics and descriptors
             * for the remote device have been updated, ie new services have been discovered.
             *
             * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
             * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
             */
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                if(status != BluetoothGatt.GATT_SUCCESS) return;

                //サービスを取得->キャクタリスティクスを取得 ==> 読み込み開始(非同期実行)
                BluetoothGattCharacteristic characteristic
                        = bleGatt.getService( UUID.fromString(SENSOR_UUID) ).getCharacteristic( UUID.fromString(SENSOR_UUID) );

                bleGatt.readCharacteristic(characteristic); //読み込めると、同じクラス内のonCharacteristicReadが呼ばれる。(非同期)
            }


            /**
             * Callback reporting the result of a characteristic read operation.
             *
             * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
             * @param characteristic Characteristic that was read from the associated
             *                       remote device.
             * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation
             */
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                if(status != BluetoothGatt.GATT_SUCCESS) return;


                //TODO:戻り値をint型に変換してやる必要あり
                sensorNum = characteristic.getValue();

                closeGatt();
            }
        };
    }


    /** deviceに接続すると同時に、データの取得フラグをoffにする */
    public void connectGatt(Context context, BluetoothDevice device){
        isGattGot = false;
        bleGatt = device.connectGatt(context, false, bleGattCallback);
        bleGatt.connect();
    }

    public void closeGatt(){
        bleGatt.close();
        isGattGot = true;
    }

    public boolean isGattGot(){
        return isGattGot;
    }

}
