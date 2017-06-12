package com.example.bobloos.coach;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.bobloos.shared.Constants;

import org.w3c.dom.Text;

/**
 * Created by bob.loos on 19/05/16.
 */
public class Face1_Activity extends WearableActivity{
    public TextView leveltextView;
    public TextClock clock;
    private BroadcastReceiver UIReceiver = null;
    private Boolean touchEventBusy = false;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    int MY_PERMISSION_REQUEST_BODY_SENSORS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_face1_activity);
        touchEventBusy = false;
        UIReceiver = this.UIChangeMessageReceiver;
        this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast"));
        this.registerReceiver(UIReceiver, new IntentFilter("com.example.momentUpdate"));
        setAmbientEnabled();
        leveltextView = (TextView) findViewById(R.id.levelText);
        leveltextView.setText("Wacht op meting van mobiel");
        checkAppPermission();
    }

    private void checkAppPermission(){
        if (ContextCompat.checkSelfPermission(Face1_Activity.this,
                Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Face1_Activity.this,
                    new String[]{Manifest.permission.BODY_SENSORS},
                    MY_PERMISSION_REQUEST_BODY_SENSORS);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        clock = (TextClock) findViewById(R.id.clock);

        clock.getPaint().setAntiAlias(false);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        clock = (TextClock) findViewById(R.id.clock);
        clock.getPaint().setAntiAlias(true);
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "RECEIVED CHANGE FOR my-vent");
        }
    };

    private BroadcastReceiver UIChangeMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == "com.example.momentUpdate") {
                Log.d("RECEIVED", "ACT 1");

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] vibrationPattern = {0, 500, 50, 300};
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                String moment = intent.getStringExtra("moment");

                if (Integer.valueOf(moment) > 0) {
                    leveltextView.setText("Aantal bolletjes: " + intent.getStringExtra("moment"));

                } else {
                    String moment_sliced = moment.replace("-", "");
                    leveltextView.setText("Aantal transparante bolletjes: " + moment_sliced);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(UIChangeMessageReceiver, new IntentFilter("com.example.momentUpdate"));

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = ev.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
//                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                    }
                    // Right to left swipe action
                    else
                    {
                        switchActivity();
                    }

                } else {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return  super.dispatchTouchEvent(ev);
    }

    private void switchActivity(){
        if (touchEventBusy  == false) {
            touchEventBusy = true;
            Log.d("TOUCHED", "AAII< GETTING TOUCHED ACT 1");
            if (UIReceiver != null) {
                this.unregisterReceiver(UIChangeMessageReceiver);
                UIReceiver = null;
            }
            Intent intent = new Intent(Face1_Activity.this, Face2_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
