package com.example.bobloos.coach;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bobloos.shared.Constants;

/**
 * Created by bob.loos on 18/05/16.
 */
public class DefaultApplication extends Application {
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;
    private RequestQueue mRequestQueue;
    private static DefaultApplication mInstance;
    public static final String TAG = DefaultApplication.class.getName();



    @Override
    public void onCreate() {
        super.onCreate();
        setPrefHandler();
        sharedPrefsEditor.putBoolean("coachSwitchStateChecked", false);
        sharedPrefsEditor.putLong("baseLineTimeStamp", 0);
        sharedPrefsEditor.putBoolean("backtoMain", false);
        sharedPrefsEditor.putBoolean("baseLinePerforming", false);
        sharedPrefsEditor.commit();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private void setPrefHandler(){
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    public static synchronized DefaultApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {
        mRequestQueue.cancelAll(TAG);
    }

}
