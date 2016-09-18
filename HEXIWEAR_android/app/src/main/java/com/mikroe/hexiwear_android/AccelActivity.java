package com.mikroe.hexiwear_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;



/*TODO:

rep counter,
just display pitch

 */
public class AccelActivity extends Activity {

    int i = 0;
    int j = 0;
    int k = 0;
    int repNumber = 7;
    int repCounter = 0;
    double maxXValue = 0.3;
    double minXValue = -0.3;


    //10 0 45 degress
    double pitchDiff = 0.0;
    double rollDiff = 0.0;

    double pitchOne= 0.0;
    double rollOne = 0.0;

    double pitchTwo = 0.0;
    double rollTwo = 0.0;

    //private CustomProgress_Vertical progressBarX;
    //private CustomProgress_Vertical progressBarX2;
   // private CustomProgress_Vertical progressBarY;
   // private CustomProgress_Vertical progressBarZ;

    private TextView xValueOneHexOne;
    private TextView xValueOneHexTwo;
    private TextView xValueTwoHexOne;
    private TextView xValueTwoHexTwo;

    private TextView diffPitch;
    private TextView diffRoll;


    private HexiwearService hexiwearService;

    private final ArrayList<String> uuidArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_accel_screen);

        uuidArray.add(HexiwearService.UUID_CHAR_ACCEL);


        xValueOneHexOne = (TextView) findViewById(R.id.xValueOneHexOne);
        xValueTwoHexOne = (TextView) findViewById(R.id.xValueTwoHexOne);
        xValueOneHexTwo = (TextView) findViewById(R.id.xValueOneHexTwo);
        xValueTwoHexTwo = (TextView) findViewById(R.id.xValueTwoHexTwo);

        diffPitch = (TextView) findViewById(R.id.diffPitch);
        diffRoll = (TextView) findViewById(R.id.diffRoll);

        /* TODO: REMOVE
        progressBarX = (CustomProgress_Vertical) findViewById(R.id.accelProgressX);
        progressBarX2 = (CustomProgress_Vertical) findViewById(R.id.accelProgressX2);
        */
       // progressBarY = (CustomProgress_Vertical) findViewById(R.id.accelProgressY);
       //progressBarZ = (CustomProgress_Vertical) findViewById(R.id.accelProgressZ);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onResume() {
        super.onResume();
        hexiwearService = new HexiwearService(uuidArray);
        //TODO changed this from 10 to 100
        hexiwearService.readCharStart(100);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onPause() {
        super.onPause();
        hexiwearService.readCharStop();
        unregisterReceiver(mGattUpdateReceiver);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////1//////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override

    protected void onDestroy() {

        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayCharData(String uuid, byte[] data, String address) {
        double tmpLongX;
        double tmpLongY;
        double tmpLongZ;
        double roll;
        double pitch;
        float tmpFloatX;
        float tmpFloatY;
        float tmpFloatZ;

        //set up time zone to append to data string
        //String[] ids = TimeZone.getAvailableIDs(-5 * 60 * 60 * 1000);
        // SimpleTimeZone est = new SimpleTimeZone(-5 * 60 * 60 * 1000, ids[0]);


        if (uuid.equals(HexiwearService.UUID_CHAR_ACCEL)) {
            tmpLongX = (((int) data[1]) << 8) | (data[0] & 0xff);
            tmpLongY = (((int) data[3]) << 8) | (data[2] & 0xff);
            tmpLongZ = (((int) data[5]) << 8) | (data[4] & 0xff);
            //tmpFloatX = (float) tmpLongX / 100;

            roll = Math.round(Math.atan2(-tmpLongY, tmpLongZ)*180.0/3.14159265359);
            pitch = Math.round(Math.atan2(tmpLongX, Math.sqrt(tmpLongY*tmpLongY + tmpLongZ*tmpLongZ))*180.0/3.14159265359);


            if(address.equals(DeviceScanActivity.KWARP_ADDRESS)) {
                xValueOneHexOne.setText("Roll = " + roll);
                xValueOneHexOne.postInvalidate();
                xValueTwoHexOne.setText("Pitch = " + pitch);
                xValueTwoHexOne.postInvalidate();
                pitchOne = pitch;
                rollOne = roll;
            }
            else if(address.equals(DeviceScanActivity.KWARP_ADDRESS_TWO)){
                xValueOneHexTwo.setText("Roll = " + roll);
                xValueOneHexTwo.postInvalidate();
                xValueTwoHexTwo.setText("Pitch = " + pitch);
                xValueTwoHexTwo.postInvalidate();
                pitchTwo = pitch;
                rollTwo = roll;
            }

            pitchDiff = pitchOne - pitchTwo;
            rollDiff = rollOne - rollTwo;

            diffPitch.setText("diffPitch = "+pitchDiff);
            diffPitch.postInvalidate();
            diffRoll.setText("diffRoll = "+rollDiff);
            diffRoll.postInvalidate();

            /*
            if(address.equals(DeviceScanActivity.KWARP_ADDRESS_TWO)) {
                progressBarX2.setProgressTitle(String.valueOf(tmpFloatX) + "g");
                tmpLong += (progressBarX2.getProgressMax() >> 1);
                if (tmpLong > progressBarX2.getProgressMax()) {
                    tmpLong = progressBarX2.getProgressMax();
                }

                progressBarX2.setProgressValue(tmpLong);
            }
        */
            // NEED TO COUNT REPS AND TURN SCREEN GREEN AFTER X REPS. FIGURE OUT HOW TO SAMPLE SLOWER, LET THE COUNTER CHANGE TO A DIFFERENT VALUE FIRST, OR TAKE BREAK BETWEEN LOOPS
           // for (repCounter = 0; tmpLong < -0.4; repCounter++){

           // }
           /* if (tmpFloatX > maxXValue && i!=5) {
                i=5; //flag if it's the first time tmplongx is smaller larger than minXvalue in this rep
               // Toast.makeText(getApplicationContext(), "floatX > maxX", Toast.LENGTH_SHORT).show();
            }

            if (tmpFloatX < minXValue && j!=6){
                j=6; //flag if it's the first time tmplongx is smaller than minXvalue in this rep
               // Toast.makeText(getApplicationContext(), "floatX < minX", Toast.LENGTH_SHORT).show();
            }

            if (tmpFloatX > minXValue && tmpFloatX < maxXValue && i==5 && j==6 && k!=7){
                k = 7; //flag if tmpfloatx is in between the maxXValue and minXValue
                //Toast.makeText(getApplicationContext(), "minX < floatX < maxX", Toast.LENGTH_SHORT).show();
            }


            if (tmpFloatX > maxXValue && i==5 && k==7 && j==6){
                i=0;
                j=0;
                k=0;
                repCounter++;
                //Toast.makeText(getApplicationContext(), "Rep", Toast.LENGTH_SHORT).show();
            }


            if (repCounter == repNumber){  //0 to 9 will give us 10 reps
                //exercise complete message
                //set the text or screen green here
                //maybe a delay needs to be here to in order to keep the screen green for
                //a certain amount of time...or have it so the user has to press a button
                //in order for the rep count to reset to 0 and change the color of the screen back
                //and set i=0, j=0 and k=0 to initiate a new exercise;
                TextView rl = (TextView)findViewById(R.id.textView);

                //Changes the background color of textView in accel_screen
                rl.setBackgroundColor(Color.GREEN);

                //creating a timestamp
                Calendar now = Calendar.getInstance();
                final int hour = now.get(Calendar.HOUR_OF_DAY);
                final int minute = now.get(Calendar.MINUTE);

                //displaying message upon exercise completion
                Toast.makeText(getApplicationContext(), "Exercise Complete at "+String.valueOf(hour )+":"
                        +String.valueOf(minute ), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Committing data to cloud...", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Data sent.", Toast.LENGTH_SHORT).show();
                repCounter = 0;

            }

           // for (int i = 0; i < repCounter; i++) {
            //    Toast.makeText(getApplicationContext(), aryAccel[i], Toast.LENGTH_SHORT).show();
            //}
            //Changes the background color of textView in accel_screen

           /*

            tmpLong = (((int) data[3]) << 8) | (data[2] & 0xff);
            tmpFloatY = (float) tmpLong / 100;
            progressBarY.setProgressTitle(String.valueOf(tmpFloatY) + "g");

            tmpLong += (progressBarY.getProgressMax() >> 1);
            if (tmpLong > progressBarY.getProgressMax()) {
                tmpLong = progressBarY.getProgressMax();
            }
            progressBarY.setProgressValue(tmpLong);

            tmpLong = (((int) data[5]) << 8) | (data[4] & 0xff);
            tmpFloatZ = (float) tmpLong / 100;
            progressBarZ.setProgressTitle(String.valueOf(tmpFloatZ) + "g");
            tmpLong += (progressBarZ.getProgressMax() >> 1);
            if (tmpLong > progressBarZ.getProgressMax()) {
                tmpLong = progressBarZ.getProgressMax();
            }
            progressBarZ.setProgressValue(tmpLong);*/

           //Long tsLong = System.currentTimeMillis()/1000;
            //String ts = tsLong.toString();

            //////////tmpFloatXYZ to string////////////////
           // StringBuilder xStringBuilder = new StringBuilder();

           // xStringBuilder.append(tmpFloatX+"g").append(ts);
           //xStringBuilder.append(tmpFloatX+"g").append(tmpFloatY+"g")
            //        .append(tmpFloatZ+"g").append(ts);

            /*String accelData = xStringBuilder.toString();
            String[] aryAccel = accelData.split(",");
            int arraySize = aryAccel.length;
            for (int i = 0; i < arraySize; i++) {
                Toast.makeText(getApplicationContext(), aryAccel[i], Toast.LENGTH_SHORT).show();
            }*/
            //Toast.makeText(getApplicationContext(), aryX[0], Toast.LENGTH_SHORT).show();
            //////////tmpFloatXYZ to string////////////////

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Handles various events fired by the Service.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                invalidateOptionsMenu();
                Intent intentAct = new Intent(AccelActivity.this, DeviceScanActivity.class);
                startActivity(intentAct);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_CHAR);
                //get BLE device that is sending the information
                String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);

                displayCharData(uuid, data, address);
            }
        }
    };
}
