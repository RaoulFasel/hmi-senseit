package com.example.bobloos.coach;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.MonitorDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    UserModel user;
    DatabaseHandler db;
    Button baseLineButton;
    Button exportButton;
    Button saveSettingsButton;

    EditText patientAVGHR;
    EditText patientSTDF;
    TextView patientUniqueID;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;
    String sensitivity;

    DefaultApplication helper = DefaultApplication.getInstance();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = new DatabaseHandler(this);
        setContentView(R.layout.activity_settings);
        setUser();
        setPrefHandler();


        baseLineButton = (Button) findViewById(R.id.baseLineButton);
        setBaseLineButtonListener();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDrawer();
        getSupportActionBar().setTitle("Instellingen");

        exportButton = (Button) findViewById(R.id.exportButton);
//        exportButton.setVisibility(View.INVISIBLE);
        exportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exportDataToServer();

                Log.d("EXPORT", "called");
            }
        });

        saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Settings", "save");
                saveSettings();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setBaseLineButtonListener() {
        baseLineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, BaseLineInitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                SettingsActivity.this.finish();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonLight:
                if (checked)
                    sensitivity = "1";
                    break;
            case R.id.radioButtonNormal:
                if (checked)
                    // Ninjas rule
                    sensitivity = "2";
                    break;
            case R.id.radioButtonSensitive:
                if (checked)
                    sensitivity = "3";
                    break;
        }
    }

    private void saveSettings(){
        user.setAvgHeartRate(patientAVGHR.getText().toString());
        user.setStdfHeartRate(patientSTDF.getText().toString());
        user.setSensitivityPref(sensitivity);
        db.updateUser(user);

        Context context = getApplicationContext();
        CharSequence text = "Jouw instelingen zijn opgeslagen!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void setUser() {
        user = db.getUser(1);

        patientAVGHR = (EditText) findViewById(R.id.editTextHRResult);
        patientSTDF = (EditText) findViewById(R.id.editTextStdfResult);
        patientUniqueID = (TextView) findViewById(R.id.textViewUniqueUserId);

        patientAVGHR.setText(user.getAvgHeartRate());
        patientSTDF.setText(user.getStdfHeartRate());
        patientUniqueID.setText(user.getUniqueUserId());
        sensitivity = user.getSensitivityPref();
        Log.d("SETTINGS SENSITIVITY", sensitivity);

        switch (sensitivity){
            case "1":
                RadioButton b = (RadioButton) findViewById(R.id.radioButtonLight);
                b.setChecked(true);
                break;
            case "2":
                RadioButton b2 = (RadioButton) findViewById(R.id.radioButtonNormal);
                b2.setChecked(true);
                break;
            case "3":
                RadioButton b3 = (RadioButton) findViewById(R.id.radioButtonSensitive);
                b3.setChecked(true);
                break;
        }
    }

    private void setPrefHandler() {
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    private void setupDrawer() {
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
                                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    SettingsActivity.this.startActivity(intent);
                                }

                                if (itemName.toString() == "Jouw Coach") {
                                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    SettingsActivity.this.startActivity(intent);
                                }
                            }
                        }

                        return false;
                    }
                })
                .build();
    }

    private void exportDataToServer() {
        exportUsers();
        exportHeartRate();
        exportMonitorData();
        exportPhysStates();
        exportSelfReports();
    }

    private void exportUsers() {
        List<UserModel> users = db.getAllUsers();

        for (int i = 0; i < users.size(); i++) {
            final UserModel user = users.get(i);
            String url = "130.89.20.34:5000/api/user";//https://bobloos.fwd.wf/api/user";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: " + site + "\nNetwork: " + network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("external_id", String.valueOf(user.getId()));
                    params.put("unique_user_id", String.valueOf(user.getUniqueUserId()));
                    params.put("avg_heart_rate", String.valueOf(user.getAvgHeartRate()));
                    params.put("stdf_heart_rate", String.valueOf(user.getStdfHeartRate()));
                    return params;
                }
            };
            helper.add(postRequest);
        }

    }

    private void exportHeartRate() {
        List<HeartRateDataModel> heartrates = db.getAllHeartRate();

        for (int i = 0; i < heartrates.size(); i++) {
            final HeartRateDataModel heartrate = heartrates.get(i);
            String url = "130.89.20.34:5000/api/heart_rate_data";//https://bobloos.fwd.wf/api/heart_rate_data";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: " + site + "\nNetwork: " + network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("external_id", String.valueOf(heartrate.getId()));
                    params.put("user_id", String.valueOf(user.getId()));
                    params.put("heart_rate", String.valueOf(heartrate.getHeartRate()));
                    params.put("measurement_time", String.valueOf(heartrate.getMeasurementTime()));
                    params.put("accuracy", String.valueOf(heartrate.getAccuracy()));
                    params.put("unique_user_id", String.valueOf(user.getUniqueUserId()));
                    return params;
                }
            };
            helper.add(postRequest);
        }

    }

    private void exportMonitorData() {
        List<MonitorDataModel> monitorDatas = db.getAllUserMonitorData();

        for (int i = 0; i < monitorDatas.size(); i++) {
            final MonitorDataModel monitorData = monitorDatas.get(i);
            String url = "130.89.20.34:5000/api/monitor_data";//https://bobloos.fwd.wf/api/monitor_data";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: " + site + "\nNetwork: " + network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("external_id", String.valueOf(monitorData.getId()));
                    params.put("sensor_update_time", String.valueOf(monitorData.getSensorUpdateTime()));
                    params.put("sensor_id", String.valueOf(monitorData.getSensorId()));
                    params.put("unique_user_id", String.valueOf(user.getUniqueUserId()));
                    params.put("accuracy", String.valueOf(monitorData.getAccuracy()));
                    params.put("measurement_time", String.valueOf(monitorData.getMeasurementTime()));
                    params.put("sensor_val", String.valueOf(monitorData.getSensorVal()));
                    return params;
                }
            };
            helper.add(postRequest);
        }

    }

    private void exportPhysStates() {
        List<PhysStateModel> physStates = db.getAllPhysStates();

        for (int i = 0; i < physStates.size(); i++) {
            final PhysStateModel physState = physStates.get(i);
            String url = "130.89.20.34:5000/api/phys_state";//https://bobloos.fwd.wf/api/phys_state";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: " + site + "\nNetwork: " + network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("external_id", String.valueOf(physState.getId()));
                    params.put("state_time_stamp", String.valueOf(physState.getStateTimeStamp()));
                    params.put("unique_user_id", String.valueOf(user.getUniqueUserId()));
                    params.put("level", String.valueOf(physState.getLevel()));
                    String contextDescription = physState.getContextDescription();
                    if (contextDescription != null) {
                        try {
                            String encrypted_context_description = AESCrypt.encrypt("cAaWKwi6QCMyxcAu3BgX", contextDescription);
                            params.put("context_description", encrypted_context_description);

                        } catch (GeneralSecurityException e) {
                            //handle error
                        }
                    }

                    return params;
                }
            };
            helper.add(postRequest);
        }

    }

    private void exportSelfReports() {
        List<SelfReportModel> selfReports = db.getAllSelfReports();

        for (int i = 0; i < selfReports.size(); i++) {
            final SelfReportModel selfReport = selfReports.get(i);
            String url = "130.89.20.34:5000/api/self_report";//https://bobloos.fwd.wf/api/self_report";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                                String site = jsonResponse.getString("site"),
                                        network = jsonResponse.getString("network");
                                System.out.println("Site: " + site + "\nNetwork: " + network);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:


                    params.put("external_id", String.valueOf(selfReport.getId()));
                    params.put("time_stamp", String.valueOf(selfReport.getTimestamp()));
                    params.put("unique_user_id", String.valueOf(user.getUniqueUserId()));
                    String reportText = selfReport.getReportText();
                    if (reportText != null){
                        try {
                            String encrypted_report_text = AESCrypt.encrypt("cAaWKwi6QCMyxcAu3BgX", reportText);
                            params.put("report_text", encrypted_report_text);
                            Log.d("ENCRYPTED", encrypted_report_text);
                        }catch (GeneralSecurityException e){
                            //handle error
                        }
                    }

                    Log.d("LOGGING DATA!", "self report");
                    return params;
                }
            };
            helper.add(postRequest);
        }

    }
}
