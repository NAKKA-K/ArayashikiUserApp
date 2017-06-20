package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothManager {
    private static BluetoothAdapter bleAdapter = null;
    private final int REQUEST_ENABLE_BT = 1;

    public boolean isBleSupport(){
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bleAdapter == null) return false;

        return true;
    }


    public void onBluetooth(Activity activity){
        bleAdapter.enable(); //強制的にBluetoothを起動する
    }


}
