package com.example.bobloos.coach;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.fragments.SelfReportFragment;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String ID = "a";
    public BroadcastReceiver mMessageReceiverInstance;

    public SharedPreferences sharedPrefs;
    public static SharedPreferences.Editor sharedPrefsEditor;

    Toolbar toolbar;
    UserModel user;
    PhysStateModel physState;
    DatabaseHandler db;
    private static RemoteSensorManager remoteSensorManager;
    SocketClient socket;
    long lastMeasurementTime = 0L;
    public Switch coachSwitch;
    TextView coachSwitchTextView;
    PendingIntent alarmIntent;
    Boolean processingAlarmIntent = false;
    FloatingActionButton fab;
    ViewPager viewPager;
    int MY_PERMISSION_REQUEST_BODY_SENSORS;
    public GoogleApiClient mApiClient;
    private URI ip = new URI("ws://130.89.136.203:8004");

    public MainActivity() throws URISyntaxException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        remoteSensorManager = RemoteSensorManager.getInstance(this);
        db = new DatabaseHandler(this);
        socket = new SocketClient(ip,this);
        try {
            socket.connectBlocking();
            } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            socket.send("HANDSHAKE,"+ID);
        }catch (WebsocketNotConnectedException e){
            Log.d("socket", "socket not connected");
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDrawer();
        getSupportActionBar().setTitle("Jouw Coach");

        setPrefHandler();
        setUser();


        if (user.getAvgHeartRate() == null || user.getStdfHeartRate() == null) {
            Intent intent = new Intent(MainActivity.this, BaseLineInitActivity.class);
            MainActivity.this.startActivity(intent);
        } else {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupActivity();
            Intent intent = getIntent();
            String pagerViewId = intent.getStringExtra("pageId");
            if (pagerViewId != null){
                viewPager.setCurrentItem(Integer.valueOf(pagerViewId));
            }
        }


        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
        checkAppPermission();
        try{
            socket.send("HANDSHAKE,"+ID);
        }catch (WebsocketNotConnectedException e){
            Log.d("socket", "socket not connected");
        }
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    socket.send("KEEP_ALIVE,"+ID);
                } catch (WebsocketNotConnectedException ex){
                    Log.d("socket","connect socket");
                    socket.close();
                    socket = new SocketClient(ip, MainActivity.this);
                    try {
                        socket.connectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.send("HANDSHAKE,"+ID);
                    }
                    catch (WebsocketNotConnectedException e) {
                        Log.d("socket", "socket not connected");
                    }
                }
            }
        }, 0, 300000);
        toggleCoach(true);
        toggleCoach(true);


    }

    private void checkAppPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BODY_SENSORS},
                    MY_PERMISSION_REQUEST_BODY_SENSORS);
        }
    }

    private void setupActivity(){
        Log.d("DEBUG", "VIEWPAGER NOW");
        Log.d("DEBUG: TRUE", String.valueOf(viewPager != null) );
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        // register receivers
        try{
            MainActivity.this.unregisterReceiver(mMessageReceiver);
            MainActivity.this.unregisterReceiver(measureMomentAlarmReceiver);
            Log.d("LOG", "UNREGISTRERD ON CREATE");

        } catch (Throwable e) {
        }

        try{
            MainActivity.this.unregisterReceiver(mMessageReceiver);
            MainActivity.this.unregisterReceiver(measureMomentAlarmReceiver);
            Log.d("LOG", "UNREGISTRERD ON CREATE");

        } catch (Throwable e) {
        }


        Log.d("LOG", "CALLING ON CREATE");
        MainActivity.this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast"));
        MainActivity.this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast2"));
        MainActivity.this.registerReceiver(measureMomentAlarmReceiver, new IntentFilter("com.example.measureMomentAlarm"));



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        fab = (FloatingActionButton) findViewById(R.id.add_story_button);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewSelfReport.class);
                startActivity(intent);
            }
        });

        coachSwitch = (Switch) findViewById(R.id.coachSwitch);
        boolean coachSwitchStateChecked = sharedPrefs.getBoolean("coachSwitchStateChecked", false);
        coachSwitch.setChecked(coachSwitchStateChecked);
        coachSwitch.setOnCheckedChangeListener(checkBtnChangeCoachMode);
        coachSwitchTextView = (TextView) findViewById(R.id.coach_switch_text_view);
        if (coachSwitchStateChecked==true){
            coachSwitchTextView.setText("Coaching is on");



        }

        // start new intent
        Intent intent = new Intent();
        intent.setAction("com.example.Broadcast");
        intent.putExtra("START_TIME", 0L); // clear millisec time
        MainActivity.this.sendBroadcast(intent);
    }



    CompoundButton.OnCheckedChangeListener checkBtnChangeCoachMode = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//            AlarmManager alarmManager=(AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
//            Intent intent2 = new Intent();
//            intent2.setAction("com.example.measureMomentAlarm");
//            alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent2, 0);
//
//            if (isChecked) {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000,
//                        alarmIntent);
//                Intent intent3 = new Intent();
//                intent3.setAction("com.example.sendMessageAlarm");
//                intent3.putExtra("moment", String.valueOf(1));
//                sharedPrefsEditor.putInt("momentState", 1);
//                MainActivity.this.sendBroadcast(intent3);
//
//                sharedPrefsEditor.putBoolean("coachSwitchStateChecked", true);
//                long timeInMs = System.currentTimeMillis();
//                coachSwitchTextView.setText("Coaching is on");
//
//                lastMeasurementTime = System.currentTimeMillis();
//                remoteSensorManager.startMeasurement();
//                Intent intent = new Intent();
//                intent.setAction("com.example.Broadcast1");
//                intent.putExtra("START_TIME", lastMeasurementTime); // get current millisec time
//                MainActivity.this.sendBroadcast(intent);
//                SharedPreferences pref = MainActivity.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putLong("START_TIME", lastMeasurementTime);
//                editor.apply();
//            } else {
//                alarmIntent.cancel();
//                // stop measurement of baseline
//                sharedPrefsEditor.putBoolean("coachSwitchStateChecked", false);
//                coachSwitchTextView.setText("Coaching is off");
//                Intent intent = new Intent();
//                intent.setAction("com.example.Broadcast1");
//                intent.putExtra("START_TIME", 0L); // clear millisec time
//                MainActivity.this.sendBroadcast(intent);
//                remoteSensorManager.stopMeasurement();
//                SharedPreferences pref = MainActivity.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putLong("START_TIME", 0L);
//                editor.apply();
//            }
//            sharedPrefsEditor.commit();
        }
    };

    private void setPrefHandler(){
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
        }
    }
    void toggleCoach(boolean bool){
        AlarmManager alarmManager=(AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        Intent intent2 = new Intent();
        intent2.setAction("com.example.measureMomentAlarm");
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent2, 0);

        if (bool) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000,
                    alarmIntent);
            Intent intent3 = new Intent();
            intent3.setAction("com.example.sendMessageAlarm");
            intent3.putExtra("moment", String.valueOf(1));
            sharedPrefsEditor.putInt("momentState", 1);
            MainActivity.this.sendBroadcast(intent3);

            sharedPrefsEditor.putBoolean("coachSwitchStateChecked", true);
            long timeInMs = System.currentTimeMillis();
            coachSwitchTextView.setText("Coaching is on");

            lastMeasurementTime = System.currentTimeMillis();
            remoteSensorManager.startMeasurement();
            Intent intent = new Intent();
            intent.setAction("com.example.Broadcast1");
            intent.putExtra("START_TIME", lastMeasurementTime); // get current millisec time
            MainActivity.this.sendBroadcast(intent);
            SharedPreferences pref = MainActivity.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putLong("START_TIME", lastMeasurementTime);
            editor.apply();
        } else {
            alarmIntent.cancel();
            // stop measurement of baseline
            sharedPrefsEditor.putBoolean("coachSwitchStateChecked", false);
            coachSwitchTextView.setText("Coaching is off");
            Intent intent = new Intent();
            intent.setAction("com.example.Broadcast1");
            intent.putExtra("START_TIME", 0L); // clear millisec time
            MainActivity.this.sendBroadcast(intent);
            //remoteSensorManager.stopMeasurement();
            SharedPreferences pref = MainActivity.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putLong("START_TIME", 0L);
            editor.apply();
        }
        sharedPrefsEditor.commit();
    }

    
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new com.example.bobloos.coach.CoachListFragment(), "HISTORY");
        adapter.addFragment(new SelfReportFragment(), "ZELFRAPPORTAGE");

//        adapter.addFragment(new HelpFragment(), "TECHNISCHE HULP");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    fab.setVisibility(View.INVISIBLE);
                }

                if (position == 1){
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //socket.send("SDFSF");
            }
        });
    }



    private void setupDrawer(){
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Jouw Coach");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName("Instellingen");
        Drawer result = new DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withActionBarDrawerToggle(true)
            .addDrawerItems(
                    item1,
                    new DividerDrawerItem(),
                    item2)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                    if (drawerItem != null) {
                        if (drawerItem instanceof Nameable) {
                            StringHolder itemName = ((Nameable) drawerItem).getName();
                            if (itemName.toString() == "Instellingen") {
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                MainActivity.this.startActivity(intent);
                            }

                            if (itemName.toString() == "Jouw Coach") {
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                    }

                    return false;
                }
            })
            .build();
    }

    // handler for received intents
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DATA FROM SENSOR", String.valueOf(intent.hasExtra("ACCEL")));
            if(intent.getAction().equals("com.example.Broadcast2")) {
                MainActivity.this.getFragmentManager();
            }
            else {
                Log.d("intent", String.valueOf(intent.getExtras()));

                // extract data included in the intent
                try {
                    // get extras
                    Log.d("intent", String.valueOf(intent.getFloatArrayExtra("HR").getClass().getName()));
                    Bundle bundle = intent.getExtras();
                    float accel = bundle.getFloat("ACCR");

                    float[] heartRate = intent.getFloatArrayExtra("HR");
                    //float accel = intent.getFloatExtra("ACCEL",0);
                    Log.d("DATA FROM SENSOR", String.valueOf(accel));
                    int sensorType = intent.getIntExtra("SENSOR_TYPE", 0);
                    int userId = user.getId();
                    long timeInMs = System.currentTimeMillis();
                    Log.d("DATA FROM SENSOR", String.valueOf(heartRate[0]));
                    try {
                        Log.d("socket", String.valueOf(socket.isOpen()));
                        socket.send("DATA," + ID + "," + heartRate[0] + "," + accel);
                    } catch (WebsocketNotConnectedException ex){
                        Log.d("socket","connect socket");
                        socket.close();
                        socket = new SocketClient(ip, MainActivity.this);
                        socket.connectBlocking();
                        socket.send("HANDSHAKE,"+ID);
                    }
                    HeartRateDataModel hrModel = new HeartRateDataModel( );
                    hrModel.setUserId(String.valueOf(userId));
                    //hrModel.setAccuracy(String.valueOf(accuracy));
                    hrModel.setUniqueUserId(user.getUniqueUserId());
                    hrModel.setHeartRate(String.valueOf(heartRate[0]));
                    hrModel.setMeasurementTime(timeInMs);
                    Log.d("ReceiverMain", "Got HR: " + heartRate[0]);
                    db.addHeartRateData(hrModel);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }
            }
        }
    };

    // handler for received intents
    private BroadcastReceiver measureMomentAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (processingAlarmIntent==false){
//                processingAlarmIntent=true;
//                defineMoment();
//                            Log.d("measureMmoentALARM", "GOT Single MESSAGE");
//
//            }
        }
    };
    public void defineMoment(int state){
        int moment_mode = state;
        Integer previousMomentState = sharedPrefs.getInt("momentState", state);
        sharedPrefsEditor.putInt("momentState", -5);
        sharedPrefsEditor.commit();

        if (previousMomentState == moment_mode) {
            Log.d("MEASUREER", "NO NEW STATE");
            Log.d("MEASUREER", previousMomentState.toString());
            Log.d("MEASUREER", String.valueOf(moment_mode));
        }else{
            Log.d("MEASUREER", "NEW STATE!!!!");
            Log.d("MEASUREER", previousMomentState.toString());
            Log.d("MEASUREER", String.valueOf(moment_mode));
            long timeInMs = System.currentTimeMillis();
            physState = new PhysStateModel();
            physState.setLevel(String.valueOf(moment_mode));
            physState.setUserId(String.valueOf(user.getId()));
            physState.setStateTimeStamp(String.valueOf(timeInMs));

            db.addPhysState(physState);

            Intent intent = new Intent();
            intent.setAction("com.example.sendMessageAlarm");
            intent.putExtra("moment", String.valueOf(moment_mode));

            MainActivity.this.sendBroadcast(intent);
        }
        processingAlarmIntent = false;
    }
//    private void defineMoment(){
//        List<HeartRateDataModel> latest_ten_measures = db.getLatestMeasures();
//        int count = latest_ten_measures.size();
//        float totalHR = 0;
//        for (int i = 0; i < count; i++) {
//            float hr = Float.valueOf(latest_ten_measures.get(i).getHeartRate());
//            Log.d("DEFINE MOMENT, HR TIME", latest_ten_measures.get(i).getMeasurementTime().toString());
//            totalHR += hr;
//        }
//
//        float avgHr = totalHR/count;
//        float userAverageHr = Float.valueOf(user.getAvgHeartRate());
//        float userStdfHr = Float.valueOf(user.getStdfHeartRate());
//        float hrDiff = avgHr - userAverageHr;
//
//        Log.d("AVGHR", String.valueOf(avgHr));
//        Log.d("useraveragdeHR", String.valueOf(userAverageHr));
//        Log.d("userSTDFhr", String.valueOf(userStdfHr));
//        Log.d("hrDiff", String.valueOf(hrDiff));
//
//        int moment_mode = 999;
//        Log.d("Use Sensitivity", getSensitivity().toString());
//
//        double interval = (userStdfHr * getSensitivity());
//        Log.d("INTERVAL", String.valueOf(interval));
//
//        Integer previousMomentState = sharedPrefs.getInt("momentState", 1);
//
//        if(hrDiff <= interval*-5){
//            sharedPrefsEditor.putInt("momentState", -5);
//            Log.d("Moment", "in state -5");
//            moment_mode = -5;
//        } else if (hrDiff > interval*-5 && hrDiff <= (interval*-4) ){
//            sharedPrefsEditor.putInt("momentState", -4);
//            moment_mode = -4;
//            Log.d("Moment", "in state -4");
//        } else if (hrDiff > interval*-4 && hrDiff <= (interval*-3) ){
//            sharedPrefsEditor.putInt("momentState", -3);
//            moment_mode = -3;
//            Log.d("Moment", "in state -3");
//        } else if (hrDiff > interval*-3 && hrDiff <= (interval*-2) ){
//            sharedPrefsEditor.putInt("momentState", -2);
//            moment_mode = -2;
//            Log.d("Moment", "in state -2");
//        } else if (hrDiff > interval*-2 && hrDiff <= (interval*-1) ){
//            sharedPrefsEditor.putInt("momentState", -1);
//            moment_mode = -1;
//            Log.d("Moment", "in state -1");
//        } else if (hrDiff > interval*-1 && hrDiff <= (interval) ){
//            sharedPrefsEditor.putInt("momentState", 1);
//            moment_mode = 1;
//            Log.d("Moment", "in state 1");
//        } else if (hrDiff > interval && hrDiff <= (interval*2) ){
//            sharedPrefsEditor.putInt("momentState", 2);
//            moment_mode = 2;
//            Log.d("Moment", "in state 2");
//        } else if (hrDiff >  (interval*2) && hrDiff <= (interval*3) ){
//            sharedPrefsEditor.putInt("momentState", 3);
//            moment_mode = 3;
//            Log.d("Moment", "in state 3");
//        } else if (hrDiff > (interval*3) && hrDiff <= (interval*4 )) {
//            sharedPrefsEditor.putInt("momentState", 4);
//            moment_mode = 4;
//            Log.d("Moment", "in state 4");
//        } else if (hrDiff > (interval*4) ) {
//            sharedPrefsEditor.putInt("momentState", 5);
//            moment_mode = 5 ;
//            Log.d("Moment", "in state 5");
//        }
//
//        sharedPrefsEditor.commit();
//
//        if (previousMomentState == moment_mode) {
//            Log.d("MEASUREER", "NO NEW STATE");
//            Log.d("MEASUREER", previousMomentState.toString());
//            Log.d("MEASUREER", String.valueOf(moment_mode));
//        }else{
//            Log.d("MEASUREER", "NEW STATE!!!!");
//            Log.d("MEASUREER", previousMomentState.toString());
//            Log.d("MEASUREER", String.valueOf(moment_mode));
//            long timeInMs = System.currentTimeMillis();
//            physState = new PhysStateModel();
//            physState.setLevel(String.valueOf(moment_mode));
//            physState.setUserId(String.valueOf(user.getId()));
//            physState.setStateTimeStamp(String.valueOf(timeInMs));
//
//            db.addPhysState(physState);
//
//            Intent intent = new Intent();
//            intent.setAction("com.example.sendMessageAlarm");
//            intent.putExtra("moment", String.valueOf(moment_mode));
//
//            MainActivity.this.sendBroadcast(intent);
//        }
//        processingAlarmIntent = false;
//    }

    public Double getSensitivity(){
        String sensitivity = user.getSensitivityPref();
        switch (sensitivity){
            case "1":
                return 1.5;
            case "2":
                return 1.0;
            case "3":
                return 0.5;
        }
        return null;
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("RANDY", "ActivityRecognitionApi started");
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

