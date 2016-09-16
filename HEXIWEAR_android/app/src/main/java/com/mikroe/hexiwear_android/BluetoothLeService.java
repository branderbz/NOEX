/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mikroe.hexiwear_android;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    //TODO: associative array of both bluetooth and gatt
    //multiple addresses
    private ArrayList<String> mBluetoothDeviceAddresses = new ArrayList<>();
    //multiple devices
    private ArrayList<BluetoothGatt> mBluetoothGatts = new ArrayList<>();

    private int changeDevice = 0;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_WRITE_RESPONSE_OK =
            "com.example.bluetooth.le.ACTION_WRITE_RESPONSE_OK";
    public final static String ACTION_WRITE_RESPONSE_ERROR =
            "com.example.bluetooth.le.ACTION_WRITE_RESPONSE_ERROR";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_CHAR =
            "com.example.bluetooth.le.EXTRA_CHAR";
    public final static String EXTRA_ADDRESS =
            "com.example.bluetooth.le.EXTRA_ADDRESS";


    //see how many devices are connected
    public int getDevicesConnected(){
        return mBluetoothGatts.size();
    }


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallbackTwo = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Hex 2Connected to GATT server.");
                // Attempts to discover services after successful connection for each device
                for (BluetoothGatt bluetoothGatt : mBluetoothGatts)
                    Log.i(TAG, "Hex 2 Attempting to start service discovery:" + bluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Hex 2 Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "Hex 2 onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_WRITE_RESPONSE_OK, characteristic,gatt.getDevice().getAddress());
            }
            else {
                broadcastUpdate(ACTION_WRITE_RESPONSE_ERROR, characteristic,gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt.getDevice().getAddress());
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Hex 1 Connected to GATT server.");
                // Attempts to discover services after successful connection for each device
                for (BluetoothGatt bluetoothGatt : mBluetoothGatts)
                    Log.i(TAG, "Hex 1 Attempting to start service discovery:" + bluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Hex 1 Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "Hex 1 onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_WRITE_RESPONSE_OK, characteristic, gatt.getDevice().getAddress());
            }
            else {
                broadcastUpdate(ACTION_WRITE_RESPONSE_ERROR, characteristic,gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt.getDevice().getAddress());
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic,String address) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        //setCharacteristicNotification(characteristic,true);
        String uuid = null;
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            intent.putExtra(EXTRA_DATA, data);
            uuid = characteristic.getUuid().toString();
            intent.putExtra(EXTRA_CHAR, new String(uuid));
            intent.putExtra(EXTRA_ADDRESS, address);

        }

        sendBroadcast(intent);
    }


    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param addresses The devices address of the destination devices.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final ArrayList<String> addresses) {
        for( int i = 0; i<addresses.size(); i++) {
            String address = addresses.get(i);
            if (mBluetoothAdapter == null || addresses == null) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }

            //TODO: Fix this
            // Previously connected device.  Try to reconnect to each device
            //for each bleutooth gatt and address,
            if (mBluetoothDeviceAddresses.contains(address)) {
                Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection at index: " + i);
                if (mBluetoothGatts.get(i).connect()) {
                    mConnectionState = STATE_CONNECTING;
                } else {
                    return false;
                }
            }
            else {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                if (device == null) {
                    Log.w(TAG, "Device not found.  Unable to connect.");
                    return false;
                }
                // We want to directly connect to the device, so we are setting the autoConnect
                // parameter to false.
                //TODO: make this neater
                BluetoothGatt gatt = null;
                if(i==0) {
                    gatt = device.connectGatt(this, false, mGattCallback);
                }
                else {
                    gatt = device.connectGatt(this, false, mGattCallbackTwo);
                }
                mBluetoothGatts.add(gatt);
                Log.d(TAG, "Trying to create a new connection.");
                mBluetoothDeviceAddresses.add(address);
                mConnectionState = STATE_CONNECTING;
            }
        }
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        //for each bluetooth gatts
        for(BluetoothGatt bluetoothGatt: mBluetoothGatts) {
            if (mBluetoothAdapter == null || bluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            bluetoothGatt.disconnect();
        }
    }

    /**
     *
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        mBluetoothGatts.removeAll(mBluetoothGatts);
        int size = mBluetoothGatts.size();
        for ( int i = 0 ; i < size ; i++) {
            if (mBluetoothGatts == null) {
                return;
            }
            //mBlue
        }
    }


    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {

            if (mBluetoothAdapter == null || mBluetoothGatts == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return true;
            }
            //todo: generalize this
            if(changeDevice==0) {
                mBluetoothGatts.get(0).readCharacteristic(characteristic);
                changeDevice= 1;
            }else{
                mBluetoothGatts.get(1).readCharacteristic(characteristic);
                changeDevice = 0;
            }
        //TODO: return idk
        return true;
    }

    public boolean writeNoResponseCharacteristic(BluetoothGattCharacteristic characteristic) {
        for(BluetoothGatt bluetoothGatt : mBluetoothGatts) {
            if (mBluetoothAdapter == null || mBluetoothGatts == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return true;
            }
            bluetoothGatt.writeCharacteristic(characteristic);
        }
        //TODO idk
        return true;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                               boolean enabled) {
        for (BluetoothGatt bluetoothGatt : mBluetoothGatts) {
            if (mBluetoothAdapter == null || bluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return;
            }
            bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    //TODO: see if this function works
    public List<List<BluetoothGattService>> getSupportedGattServices() {
        List<List<BluetoothGattService>> supportedGattServices = new ArrayList<>();
        if (mBluetoothGatts == null) return null;
        else{
            for(BluetoothGatt bluetoothGatt : mBluetoothGatts ){
                supportedGattServices.add(bluetoothGatt.getServices());
            }
        }
        return supportedGattServices;
    }
}
