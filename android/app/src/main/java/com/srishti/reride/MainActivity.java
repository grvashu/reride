/**
 * ReRide Main Program for Android Mobile API Level 23/24
 * Author: Gaurav Singh (github.com/grvashu)
 * For INTERACT 2017 demo
 * Version 2.0
 * KeyStore / Key Password: 12345678
 **/

package com.srishti.reride;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import processing.android.CompatUtils;
import processing.android.PFragment;

public class MainActivity extends AppCompatActivity {

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    RR sketch;
    int acc;
    int push;
    int fsr;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mGatt;       //Ble Nano 1  -- Accelerometer
    private BluetoothGatt mGatt2;      //Ble Nano 2  -- Push Button
    private BluetoothGatt mGatt3;      //Genuino 101 -- FSR
    private String bleNano1_address = "EB:4D:CE:4E:A8:13";
    private String bleNano2_address = "C2:31:28:7F:FF:A7";
    private String genuino_address = "98:4F:EE:10:B9:88";
    private BluetoothDevice bleNano1;
    private BluetoothDevice bleNano2;
    private BluetoothDevice genuino;
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            if (status == 0) {
                Log.i("STATUS", "CONNECTED");
                gatt.discoverServices();
            } else {
                Log.i("STATUS", "NOT CONNECTED");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            BluetoothGattCharacteristic characteristic = services.get(2).getCharacteristics().get(0);
            gatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
            gatt.readCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (gatt.getDevice() == bleNano1) {
                acc = updateCharacteristicValue(characteristic);
                Log.i("bleNano1", Integer.toString(acc));
                sketch.acc = acc;
            } else if (gatt.getDevice() == bleNano2) {
                push = updateCharacteristicValue(characteristic);
                Log.i("bleNano2", Integer.toString(push));
                sketch.push = push;
            }
            if (gatt.getDevice() == genuino) {
                fsr = updateCharacteristicValue(characteristic);
                Log.i("genuino", Integer.toString(fsr));
                sketch.fsr = fsr;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (gatt.getDevice() == bleNano1) {
                acc = updateCharacteristicValue(characteristic);
                Log.i("bleNano1", Integer.toString(acc));
                sketch.acc = acc;
            } else if (gatt.getDevice() == bleNano2) {
                push = updateCharacteristicValue(characteristic);
                Log.i("bleNano2", Integer.toString(push));
                sketch.push = push;
            }
            if (gatt.getDevice() == genuino) {
                fsr = updateCharacteristicValue(characteristic);
                Log.i("genuino", Integer.toString(fsr));
                sketch.fsr = fsr;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        bleNano1 = mBluetoothAdapter.getRemoteDevice(bleNano1_address);
        connectToDevice(bleNano1);

        bleNano2 = mBluetoothAdapter.getRemoteDevice(bleNano2_address);
        connectToDevice(bleNano2);

        genuino = mBluetoothAdapter.getRemoteDevice(genuino_address);
        connectToDevice(genuino);

        // Create Processing sketch from MainActivity
        sketch = new RR();

        // Start Processing sketch in MainActivity
        // Processing Sketch is saved in reride_new
        PFragment fragment = new PFragment(sketch);
        fragment.setView(frame, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if (sketch != null) {
            sketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (sketch != null) {
            sketch.onNewIntent(intent);
        }
    }

    public void connectToDevice(BluetoothDevice device) {
        if (device == bleNano1) {
            if (mGatt == null) {
                mGatt = device.connectGatt(this, true, gattCallback);
            }
        } else if (device == bleNano2) {
            if (mGatt2 == null) {
                mGatt2 = device.connectGatt(this, true, gattCallback);
            }
        } else if (device == genuino) {
            if (mGatt3 == null) {
                mGatt3 = device.connectGatt(this, true, gattCallback);
            }
        }
    }

    public int updateCharacteristicValue(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        final int value = characteristic.getIntValue(format, 1);
        //Log.d("Value Fetched", String.format("%d", value));
        return value;
    }
}