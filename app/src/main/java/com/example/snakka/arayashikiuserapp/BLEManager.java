package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BLEManager {
    private Context context;
    private static BluetoothAdapter bleAdapter = null;
    private static BLEScanner bleScanner;

    public BLEManager(Context context){
        this.context = context;

        initBleAdapter();
        bleScanner = new BLEScanner(bleAdapter.getBluetoothLeScanner());
    }


    private void initBleAdapter(){
        if(bleAdapter == null) return; //Adapterは複数作成しないようにすべき

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();
    }

    /** 端末がBluetoothに対応しているか判定。非対応ならメッセージを表示 */
    public static boolean isBleSupport(Context context){

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


    public void sensorNumGetter(){
        bleScanner.startScanDevice();
    }

}
