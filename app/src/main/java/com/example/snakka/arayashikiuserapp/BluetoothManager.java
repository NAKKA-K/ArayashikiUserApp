package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothManager {
    private static BluetoothAdapter bleAdapter = null;
    private final int REQUEST_ENABLE_BT = 1;

    public boolean isBleSupport(Context context){
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bleAdapter == null){
            Toast.makeText(context, "Bluetoothに対応していません", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void onBluetooth(Activity activity){
        bleAdapter.enable(); //強制的にBluetoothを起動する
    }


}
