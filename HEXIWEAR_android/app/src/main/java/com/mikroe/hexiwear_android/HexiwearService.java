package com.mikroe.hexiwear_android;

/**
 * Created by djolic on 2015-11-24.
 */
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.MessageQueue;
import android.util.Log;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class HexiwearService {
    private static final String TAG = "KWARP_Service";

    public static final String HEXIWEAR_ADDRESS = "00:04:9F:00:00:01";

    public static final String UUID_CHAR_AMBIENT_LIGHT = "00002011-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_TEMPERATURE   = "00002012-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_HUMIDITY      = "00002013-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_PRESSURE      = "00002014-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_HEARTRATE     = "00002021-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_BATTERY       = "00002a19-0000-1000-8000-00805f9b34fb";

    public static final String UUID_CHAR_ACCEL  = "00002001-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_GYRO   = "00002002-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHAR_MAGNET = "00002003-0000-1000-8000-00805f9b34fb";

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<ArrayList<BluetoothGattCharacteristic>>> mGattsCharacteristics = new ArrayList<>();

    private final ArrayList<ArrayList<BluetoothGattCharacteristic>> charas = new ArrayList<>();

    private int charCnt = 0;
    private Timer myTimer;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public HexiwearService(ArrayList<String> uuidArray) {
        String uuidGat = null;
        mGattsCharacteristics = DeviceScanActivity.getGattsCharacteristics();
        mBluetoothLeService  = DeviceScanActivity.getBluetoothLeService();
        for(ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristics : mGattsCharacteristics) {
            ArrayList<BluetoothGattCharacteristic> tempCharas = new ArrayList<>();
            for (int cnt = 0; cnt < gattCharacteristics.size(); cnt++) {
                for (int cnt1 = 0; cnt1 < gattCharacteristics.get(cnt).size(); cnt1++) {
                    BluetoothGattCharacteristic characteristic = gattCharacteristics.get(cnt).get(cnt1);
                    uuidGat = characteristic.getUuid().toString();

                    for (String uuid : uuidArray) {
                        if (uuid.equals(uuidGat)) {
                            tempCharas.add(characteristic);
                            break;
                        }
                    }
                }
            }
            charas.add(tempCharas);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class ReadCharTask extends TimerTask {
        public void run() {
            for(ArrayList<BluetoothGattCharacteristic> chars : charas) {
                readCharacteristic(chars.get(charCnt++));
                if (charCnt == chars.size()) {
                    charCnt = 0;
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();

            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {

                while(mBluetoothLeService.readCharacteristic(characteristic) == false) {
                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException");
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void readCharStart(long interval) {
        myTimer = new Timer();
        ReadCharTask readCharTask = new ReadCharTask();
        myTimer.schedule(readCharTask, 200, interval);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void readCharStop() {
        myTimer.cancel();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
