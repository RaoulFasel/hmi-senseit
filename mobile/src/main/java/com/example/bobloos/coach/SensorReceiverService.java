package com.example.bobloos.coach;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.MonitorDataModel;
import com.example.bobloos.shared.DataMapKeys;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bob.loos on 15/05/16.
 */

public class SensorReceiverService extends WearableListenerService {
    private static final String TAG = "SensorDashboard/SensorReceiverService";

    private RemoteSensorManager sensorManager;
    long startTime;
    boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = RemoteSensorManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
        startTime = pref.getLong("START_TIME", 0L);
        registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast1"));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
    }
    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged()");
        for (DataEvent dataEvent : dataEvents) {
            DataItem dataItem = dataEvent.getDataItem();
            Uri uri = dataItem.getUri();
            String path = uri.getPath();
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                isRunning = true;
                if (path.startsWith("/sensors/")) {
                    unpackSensorData(
                            Integer.parseInt(uri.getLastPathSegment()),
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
                else if (path.startsWith("/task/")) {
                    unpackSensorData(
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            } else {
                isRunning = false;
            }
        }
    }

    private void unpackSensorData(DataMap dataMap) {
        String message = dataMap.getString(DataMapKeys.TASK_MESSAGE);
        Intent intent = new Intent();
        intent.setAction("com.example.Broadcast2");
        intent.putExtra("TASK_MESSAGE", message);
        sendBroadcast(intent);
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        isRunning = true;
        float accuracy = dataMap.getFloat(DataMapKeys.ACCURACY);
        long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
        float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);
        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values) + " timestamp:" + timestamp + " start time:" + startTime);
        // Save to database if Start time more than 0
        if (startTime > 0) {
            DatabaseHandler db = new DatabaseHandler(this);
            db.addMonitorData(new MonitorDataModel(0, String.valueOf(sensorType), "anonymous", Arrays.toString(values), String.valueOf(timestamp), String.valueOf(accuracy), String.valueOf(startTime)));
            List<MonitorDataModel> list = db.getAllUserMonitorData();
        } else {
            isRunning = false;
        }
        Log.d(TAG,"Broadcast Sensor Value.");
        Intent intent = new Intent();
        intent.setAction("com.example.Broadcast");
        intent.putExtra("HR", values);
        intent.putExtra("ACCR", accuracy);
        intent.putExtra("TIME", timestamp);
        intent.putExtra("SENSOR_TYPE", sensorType);
        intent.putExtra("IS_RUNNING", true);
        Bundle bundle = intent.getExtras();

        sendBroadcast(intent);

        sensorManager.addSensorData(sensorType, (int)accuracy, timestamp, values);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long message1 = intent.getLongExtra("START_TIME",0L);
            startTime = message1;// set start time
            SharedPreferences pref = getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
            startTime = pref.getLong("START_TIME", 0L);
            Log.d("Receiver", "Got START_TIMEB: " + String.valueOf(startTime));
        }
    };
}

