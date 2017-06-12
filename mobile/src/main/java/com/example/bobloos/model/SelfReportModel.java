package com.example.bobloos.model;

import java.text.SimpleDateFormat;
import java.util.Random;

import static java.util.TimeZone.getTimeZone;

/**
 * Created by bob.loos on 30/05/16.
 */
public class SelfReportModel {
    private int id;
    private String user_id;
    private String report_text;
    private String unique_user_id;
    private String timestamp;


    public SelfReportModel() {
    }

    public SelfReportModel(int id, String user_id, String report_text, String unique_user_id, String timestamp){
        this.id = id;
        this.user_id = user_id;
        this.report_text = report_text;
        this.unique_user_id = unique_user_id;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id){
        this.user_id = user_id;
    }

    public String getReportText() {
       return report_text;
    }

    public void setReportText(String report_text) {
        this.report_text = report_text;
    }

    public String getUniqueUserId() {
        return unique_user_id;
    }

    public void setUniqueUserId(String unique_user_id) {
        this.unique_user_id = unique_user_id;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setTimeStamp(String timestamp){
        this.timestamp = timestamp;
    }

    public String getDate(){
        long timeStamp =  Long.parseLong(this.timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        formatter.setTimeZone(getTimeZone("Europe/Amsterdam"));

        String result = formatter.format(timeStamp);
        return result;
    }

}
