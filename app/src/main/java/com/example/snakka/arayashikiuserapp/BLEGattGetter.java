package com.example.snakka.arayashikiuserapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BLEGattGetter {
    private static BluetoothGattCallback bleGattCallback;
    private BluetoothGatt bleGatt;
    private boolean isGattGot = false;
    private byte[] sensorNum;


    public BLEGattGetter(){
        bleGattCallback = initGattCallback();
    }

    private BluetoothGattCallback initGattCallback(){
        return new BluetoothGattCallback() {
            /** GATTクライアントがリモートGATTサーバに接続、切断されたことを示すコールバック。 */
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_DISCONNECTED){
                    Log.d("discoverServices()", "GATTをclose()します");
                    gatt.close();
                    return;
                }

                Log.e("discoverServices()", "GATTに接続");
                bleGatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                if(status != BluetoothGatt.GATT_SUCCESS) return;

                //デバイスのサービスからabcdのUUIDを持つ物を探し、内部のキャラクタリスティックを取得
                for(BluetoothGattService service: bleGatt.getServices()){
                    if(isArayashikiUUID(service.getUuid().toString())){
                        bleGatt.readCharacteristic(service.getCharacteristics().get(0)); //読み込めると、同じクラス内のonCharacteristicReadが呼ばれる。(非同期)
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                if(status != BluetoothGatt.GATT_SUCCESS) return;

                sensorNum = characteristic.getValue();
                Log.d("onCharacteristicRead()", "sensorNum = " + sensorNum);

                closeGatt();
            }
        };
    }

    /** arayashiki内のabcdを持つUUID文字列を選択する */
    public boolean isArayashikiUUID(String uuid){
        Pattern pattern = Pattern.compile("^0000abcd"); //これ以外の文字が含まれると弾く
        Matcher matcher = pattern.matcher(uuid);

        if(matcher.find()){
            return true;
        }
        return false;
    }



    /** deviceに接続すると同時に、データの取得フラグをoffにする */
    public void connectGatt(Context context, BluetoothDevice device){
        if(device == null) return;

        setIsGattGot(false);
        bleGatt = device.connectGatt(context, false, bleGattCallback);
        bleGatt.connect();
    }

    public void closeGatt(){
        if(bleGatt == null) return;

        setIsGattGot(true);
        bleGatt.disconnect();
    }


    /** Activityがcancelしたときに呼ぶべき処理 */
    public void cancelGattGetter(){
        closeGatt();
    }


    //getter,setter
    public boolean isGattGot(){
        return isGattGot;
    }
    public void setIsGattGot(boolean gattGot) { isGattGot = gattGot; }

    public byte[] getSensorNum(){ return sensorNum; }

}
