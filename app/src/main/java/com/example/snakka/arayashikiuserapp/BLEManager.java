package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BLEManager {
    private Context context;
    private static BluetoothAdapter bleAdapter = null;
    private final int REQUEST_ENABLE_BT = 1;

    public BLEManager(Context context){
        this.context = context;
    }


    public boolean isBleSupport(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        //古い実装？:bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bleAdapter == null){
            Toast.makeText(context, "Bluetoothに対応していません", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void onBluetooth(Activity activity){
        if(bleAdapter.isEnabled() == false){
            bleAdapter.enable(); //強制的にBluetoothを起動する
        }
    }


}
