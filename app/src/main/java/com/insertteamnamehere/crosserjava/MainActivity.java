package com.insertteamnamehere.crosserjava;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CrosserLog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch switchButton = (Switch) findViewById(R.id.bluetoothSwitch);
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(getBaseContext(), "Bluetooth isn't supported", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bluetooth is not supported on this device");
                    MainActivity.this.finish();
                    System.exit(0);
                }
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                    while (!bluetoothAdapter.isEnabled()) {
                        continue;
                    }
                    Toast.makeText(getBaseContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bluetooth enabled");
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress();
                            Log.d(TAG, String.format("Found paired device named: %s with the mac address: %s", deviceName, deviceHardwareAddress));
                            Log.d(TAG,"Searching for unpaired devices");
                            if (bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            bluetoothAdapter.startDiscovery();
                            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                            registerReceiver(receiver, discoverDevicesIntent);
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bluetooth is already enabled");
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress();
                            Log.d(TAG, String.format("Found paired device named: %s with the mac address: %s", deviceName, deviceHardwareAddress));
                            Log.d(TAG,"Searching for unpaired devices");
                            if (bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            bluetoothAdapter.startDiscovery();
                            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                            registerReceiver(receiver, discoverDevicesIntent);
                        }
                    }
                }
            }
            private BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if(action.equals(BluetoothDevice.ACTION_FOUND)){
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Log.d(TAG, String.format("unpaired device found named: %s with the mac address %s", device.getName(), device.getAddress()));
                    }
                }
            };
        });
    }
}