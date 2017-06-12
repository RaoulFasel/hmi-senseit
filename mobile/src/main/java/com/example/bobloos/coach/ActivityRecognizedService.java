package com.example.bobloos.coach;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {

    public String curActivity = "NONE";

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }
    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        for( DetectedActivity activity : probableActivities ) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Sets an ID for the notification, so it can be updated
            int notifyID = 1;
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Activity")
                    .setContentText("You've received new messages.")
                    .setSmallIcon(R.drawable.sense_it);
            int numMessages = 0;

            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "IN VEHICLE";
                        mNotifyBuilder.setContentText("In Vehicle " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "ON BICYCLE";

                        mNotifyBuilder.setContentText("On Bicycle " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "ON FOOT";
                        mNotifyBuilder.setContentText("On Foot " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "RUNNING";
                        mNotifyBuilder.setContentText("Running " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "STILL";
                        mNotifyBuilder.setContentText("Still " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "TILTING";
                        mNotifyBuilder.setContentText("Tilting " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "WALKING";
                        mNotifyBuilder.setContentText("Walking " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        curActivity = "UNKNOWN";
                        mNotifyBuilder.setContentText("Unknown " + activity.getConfidence())
                                .setNumber(++numMessages);
                        // Because the ID remains unchanged, the existing notification is
                        // updated.
                        mNotificationManager.notify(
                                notifyID,
                                mNotifyBuilder.build());
                    }
                    break;
                }
            }
        }
    }
}
