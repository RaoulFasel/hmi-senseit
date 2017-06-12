package com.example.bobloos.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.TimeZone.*;

/**
 * Created by bob.loos on 13/05/16.
 */
public class PhysStateModel {
    private int id;
    private String stateTimeStamp;
    private String userId;
    private String level;
    private String contextDescription;

    public PhysStateModel() {
        this.contextDescription = null;
    }

    public PhysStateModel(int id, String stateTimeStamp, String userId, String level, String contextDescription) {
        this.id = id;
        this.stateTimeStamp = stateTimeStamp;
        this.userId = userId;
        this.level = level;
        this.contextDescription = contextDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getStateTimeStamp() {
        return stateTimeStamp;
    }

    public void setStateTimeStamp(String stateTimeStamp) {
        this.stateTimeStamp = stateTimeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getContextDescription() {
        return contextDescription;
    }

    public void setContextDescription(String contextDescription) {
        this.contextDescription = contextDescription;
    }

    public String getDate(){
        long timeStamp =  Long.parseLong(this.stateTimeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        formatter.setTimeZone(getTimeZone("Europe/Amsterdam"));

        String result = formatter.format(timeStamp);
        return result;
    }

}


