package com.example.bobloos.model;

import java.util.Random;

/**
 * Created by bob.loos on 14/05/16.
 */
public class UserModel {
    private int id;
    private String unique_user_id;
    private String avg_heart_rate;
    private String stdf_heart_rate;
    private String sensitivity_pref;


    public UserModel() {
        this.unique_user_id = getUniqueUserId();
        this.avg_heart_rate = "75";
        this.stdf_heart_rate = "5";
        this.sensitivity_pref = "2";
    }

    public UserModel(int id, String unique_user_id, String avg_heart_rate, String stdf_heart_rate, String sensitivity_pref ) {
        this.id = id;
        this.unique_user_id = unique_user_id;
        this.avg_heart_rate = avg_heart_rate;
        this.stdf_heart_rate = stdf_heart_rate;
        this.sensitivity_pref = sensitivity_pref;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUniqueUserId() {
        if (unique_user_id != null) {
            return unique_user_id;
        } else {
            Random rnd = new Random();
            int n = (100000 + rnd.nextInt(900000));
            setUniqueUserId(String.valueOf(n));
            return unique_user_id;
        }

    }

    public void setUniqueUserId(String unique_user_id) {
        this.unique_user_id = unique_user_id;
    }

    public String getAvgHeartRate() {
        return avg_heart_rate;
    }

    public void setAvgHeartRate(String avg_heart_rate) {
        this.avg_heart_rate = avg_heart_rate;
    }

    public String getStdfHeartRate() {
        return stdf_heart_rate;
    }

    public void setStdfHeartRate(String stdf_heart_rate) {
        this.stdf_heart_rate = stdf_heart_rate;
    }

    public String getSensitivityPref() {
        return sensitivity_pref;
    }

    public void setSensitivityPref(String sensitivity_pref) {
        this.sensitivity_pref = sensitivity_pref;
    }

}




