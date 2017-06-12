package com.example.bobloos.model;

import com.example.bobloos.coach.MainActivity;
import com.example.bobloos.database.DatabaseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bob.loos on 18/05/16.
 */
public class HeartRateDataModel {
    private int id;
    private String userId;
    private String uniqueUserId;
    private String heartRate;
    private String accuracy;
    private Long measurementTime;

    public HeartRateDataModel () {

    }

    public  HeartRateDataModel (int id, String userId, String uniqueUserId, String heartRate, String accuracy, Long measurementTime) {
        this.id = id;
        this.userId = userId;
        this.uniqueUserId = uniqueUserId;
        this.heartRate = heartRate;
        this.accuracy = accuracy;
        this.measurementTime = measurementTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUniqueUserId() {
        return userId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;

    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public Long getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(Long measurementTime) {
        this.measurementTime = measurementTime;
    }

    private String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "00:00";
        }
    }
}
