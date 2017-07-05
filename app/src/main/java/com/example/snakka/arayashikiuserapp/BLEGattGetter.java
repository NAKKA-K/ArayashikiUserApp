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

public class BLEGattGetter {
    private BluetoothGattCallback bleGattCallback;
    private BluetoothGatt bleGatt = null;
    private ArrayList<BluetoothGattService> gattServiceList;

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

                if (newState != BluetoothProfile.STATE_CONNECTED){
                    return;
                }

                bleGatt = gatt;
                discoverService();
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
                if(status != BluetoothGatt.GATT_SUCCESS){
                    return;
                }

                gattServiceList = (ArrayList) gatt.getServices(); //TODO:ListとArrayListの互換性を確かめる必要あり
                for(BluetoothGattService service : gattServiceList){
                    //サービス一覧から既定のサービスを取得する必要がある
                    //キャラクタリスティクスを取得したりもできる
                }
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

                if(status != BluetoothGatt.GATT_SUCCESS){
                    return;
                }

            }
        };
    }

    public void connectGatt(Context context, BluetoothDevice device){
        device.connectGatt(context, false, bleGattCallback);

    }


    private void discoverService(){
        if(bleGatt == null) return;

        bleGatt.discoverServices();
    }


}
