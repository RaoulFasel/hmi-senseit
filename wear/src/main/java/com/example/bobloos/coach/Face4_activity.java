package com.example.bobloos.coach;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.params.Face;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

public class Face4_activity extends WearableActivity {

    public TextView waitMessage;
    public TextClock clock;
    private BroadcastReceiver UIReceiver = null;
    private Boolean touchEventBusy = false;
    public ImageView background;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_face4_activity);
        touchEventBusy = false;
        UIReceiver = this.UIChangeMessageReceiver;
        this.registerReceiver(UIReceiver, new IntentFilter("com.example.momentUpdate"));
        setAmbientEnabled();
        waitMessage = (TextView) findViewById(R.id.waitMessage4);
        background = (ImageView) findViewById(R.id.backgroundMode4);
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

    private BroadcastReceiver UIChangeMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == "com.example.momentUpdate") {
                Log.d("RECEIVED", "ACT 4");

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] vibrationPattern = {0, 500, 50, 300};
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                waitMessage.setText("");
                String moment = intent.getStringExtra("moment");
                Log.d("MOMENT ACT 4", moment);
                switch (moment){
                    case "-5":
                        Log.d("Should", "SET IMGAE -5");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level5_t, getApplicationContext().getTheme()));
                        break;
                    case "-4":
                        Log.d("Should", "SET IMGAE -4");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level4_t, getApplicationContext().getTheme()));
                        break;
                    case "-3":
                        Log.d("Should", "SET IMGAE -3");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level3_t, getApplicationContext().getTheme()));
                        break;
                    case "-2":
                        Log.d("Should", "SET IMGAE -2");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level2_t, getApplicationContext().getTheme()));
                        break;
                    case "-1":
                        Log.d("Should", "SET IMGAE -1");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level1_t, getApplicationContext().getTheme()));
                        break;
                    case "1":
                        Log.d("Should", "SET IMGAE 1");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level1, getApplicationContext().getTheme()));
                        break;
                    case "2":
                        Log.d("Should", "SET IMGAE 2");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level2, getApplicationContext().getTheme()));
                        break;
                    case "3":
                        Log.d("Should", "SET IMGAE 3");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level3, getApplicationContext().getTheme()));
                        break;
                    case "4":
                        Log.d("Should", "SET IMGAE 4");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level4, getApplicationContext().getTheme()));
                        break;
                    case "5":
                        Log.d("Should", "SET IMGAE 5");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level5, getApplicationContext().getTheme()));
                        break;
                    default:
                        Log.d("Should", "DEFAULT");
                        background.setImageDrawable(getResources().getDrawable(R.mipmap.level5, getApplicationContext().getTheme()));
                        break;
                }
                waitMessage.setText("");
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
        if (touchEventBusy == false) {
            touchEventBusy = true;
            Log.d("TOUCHED", "AAII< GETTING TOUCHED ACT 4");

            if (UIReceiver != null) {
                this.unregisterReceiver(UIChangeMessageReceiver);
                UIReceiver = null;
            }
            Intent intent = new Intent(Face4_activity.this, Face1_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}

