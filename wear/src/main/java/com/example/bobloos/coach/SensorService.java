package com.example.bobloos.coach;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by bob.loos on 14/05/16.
 */
public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;

    SensorManager mSensorManager;

    private Sensor mHeartrateSensor;
    ScheduledExecutorService hrScheduler;
    private DeviceClient client;
    Notification.Builder builder;
    private int currentValue=0;
    private long lastTimeSentUnix=0L;
    private float latestAccel = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        client = DeviceClient.getInstance(this);

        builder = new Notification.Builder(this);
        builder.setContentTitle("Halo");
        builder.setContentText("Monitoring...");

        startForeground(1, builder.build());

        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        mHeartrateSensor = mSensorManager.getDefaultSensor(SENS_HEARTRATE);

        // register various listeners
        if (mSensorManager != null) {


            Log.w(TAG, "YEAHHH WE SHOULD NOW START MEASURING");
            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            Log.d(TAG, sensors.toString());


            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, 1000000,1000);
            } else {
                Log.w(TAG, "No Accelerometer found");
            }
            if (mHeartrateSensor != null) {
                Log.d(TAG, "register Heartrate Sensor");
                mSensorManager.registerListener(SensorService.this, mHeartrateSensor, 5000000,1000);
            } else {
                Log.d(TAG, "No Heartrate Sensor found");
            }
//            if (mHeartrateSensor != null) {
//                final int measurementDuration   = 30;   // Seconds
//                final int measurementBreak      = 15;    // Seconds
//
//                mScheduler = Executors.newScheduledThreadPool(1);
//                mScheduler.scheduleAtFixedRate(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d(TAG, "register Heartrate Sensor");
//                                mSensorManager.registerListener(SensorService.this, mHeartrateSensor, SensorManager.SENSOR_DELAY_FASTEST,1000);
//
//                                try {
//                                    Thread.sleep(measurementDuration * 1000);
//                                } catch (InterruptedException e) {
//                                    Log.e(TAG, "Interrupted while waitting to unregister Heartrate Sensor");
//                                }
//
//                                Log.d(TAG, "unregister Heartrate Sensor");
//                                mSensorManager.unregisterListener(SensorService.this, mHeartrateSensor);
//                            }
//                        }, 3, measurementDuration + measurementBreak, TimeUnit.SECONDS);
//
//            } else {
//                Log.d(TAG, "No Heartrate Sensor found");
//            }
        }
    }

    private void stopMeasurement() {
        // unregister sensor manager
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
        // shut down heartrate scheduler
        if (hrScheduler != null)
            hrScheduler.shutdown();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // log incoming sensor data
        if (event.sensor.getType() == SENS_ACCELEROMETER && event.values.length > 0) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            latestAccel = (float)Math.sqrt(Math.pow(x,2.)+Math.pow(y,2)+Math.pow(z,2));
            //Log.d("pow", String.valueOf(latestAccel));

        }
        // send heartrate data and create new intent
        if (event.sensor.getType() == SENS_HEARTRATE && event.values.length > 0 && event.accuracy >0) {

            Log.d("Sensor", event.sensor.getType() + "," + event.accuracy + "," + event.timestamp + "," + Arrays.toString(event.values));

            int newValue = Math.round(event.values[0]);
            long currentTimeUnix = System.currentTimeMillis() / 1000L;
            long nSeconds = 30L;
            
            if(newValue!=0 && lastTimeSentUnix < (currentTimeUnix-nSeconds)) {
                lastTimeSentUnix = System.currentTimeMillis() / 1000L;
                currentValue = newValue;
                Log.d(TAG, "Broadcast HR.");
                Intent intent = new Intent();
                intent.setAction("com.example.Broadcast");
                intent.putExtra("HR", event.values);
                intent.putExtra("ACCR", latestAccel);
                intent.putExtra("TIME", event.timestamp);
                intent.putExtra("ACCEL", latestAccel);


                Log.d("change", "send intent");

                sendBroadcast(intent);

                client.sendSensorData(event.sensor.getType(), latestAccel, event.timestamp, event.values);
            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

