package com.example.bobloos.coach;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.bobloos.shared.ClientPaths;
import com.example.bobloos.shared.DataMapKeys;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;

/**
 * Created by bob.loos on 14/05/16.
 */

public class MessageReceiverService extends WearableListenerService {
    private static final String TAG = "SensorDashboard/MessageReceiverService";

    private DeviceClient deviceClient;


    @Override
    public void onCreate() {
        super.onCreate();

        deviceClient = DeviceClient.getInstance(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/filter")) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    int filterById = dataMap.getInt(DataMapKeys.FILTER);
                    deviceClient.setSensorFilter(filterById);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Received message: " + messageEvent.getPath());

        if (messageEvent.getPath().equals(ClientPaths.START_MEASUREMENT)) {
            startService(new Intent(this, SensorService.class));
            broadcastIntent("com.example.MeasurementON");
        }

        if (messageEvent.getPath().equals(ClientPaths.STOP_MEASUREMENT)) {
            stopService(new Intent(this, SensorService.class));
            broadcastIntent("com.example.MeasurementOFF");
        }

        if (messageEvent.getPath().equals(ClientPaths.MOMENT_UPDATE)) {
            final String moment_message = new String(messageEvent.getData());
            Intent intent = new Intent();
            intent.setAction("com.example.momentUpdate");
            intent.putExtra("moment", moment_message);
            sendBroadcast(intent);
            Log.d("MOBILE", "sending momemnt update" + moment_message );

        }
    }

    public void broadcastIntent(String action){
        Log.d(TAG, "CALLED BROADCASTINTENT");
        Log.d(TAG, action);
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }
}

