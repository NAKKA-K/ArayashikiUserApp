package com.example.snakka.arayashikiuserapp;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLEGattGetter {
    private BluetoothGattCallback bleGattCallback;
    private BluetoothGatt bleGatt = null;

    private byte[] sensorNum;

    public BLEGattGetter(){
        bleGattCallback = initGattCallback();
    }

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

                //bleGatt = gatt;
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
                        = bleGatt.getService( UUID.fromString("ABCD") ).getCharacteristic( UUID.fromString("ABCD") );
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

    public void connectGatt(Context context, BluetoothDevice device){
        bleGatt = device.connectGatt(context, false, bleGattCallback);
        bleGatt.connect();
    }

    public void closeGatt(){
        if(bleGatt == null) return;

        bleGatt.close();
    }

}
