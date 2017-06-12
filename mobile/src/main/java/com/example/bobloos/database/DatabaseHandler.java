package com.example.bobloos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.MonitorDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;

import java.util.LinkedList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "coachDB";

    // Table Names
    private static final String TABLE_MONITOR_DATA = "monitorData";
    private static final String TABLE_PHYS_STATE = "physStates";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_HEART_RATE_DATA = "heartRateData";
    private static final String TABLE_SELF_REPORTS = "selfReports";

    // COMMON column names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_UNIQUE_USER_ID = "uniqueUserId";
    private static final String KEY_MEASUREMENT_TIME = "measurementTime";


    // MONITOR DATA column names
    private static final String KEY_SENSOR_UPDATE_TIME = "sensorUpdateTime";
    private static final String KEY_SENSOR_ID = "sensorId";
    private static final String KEY_SENSOR_VAL = "sensorVal";

    // PHYS STATE column names
    private static final String KEY_STATE_TIME_STAMP = "stateTimeStamp";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_CONTEXT_DESCRIPTION = "contextDescription";

    // USERS column names
    private static final String KEY_AVG_HEART_RATE = "avgHeartRate";
    private static final String KEY_STDF_HEART_RATE = "stdfHeartRate";
    private static final String KEY_SENSITIVY_PREF = "sensitivityPref";

    // HEART RATE DATA column names
    private static final String KEY_HEART_RATE = "heartRate";
    private static final String KEY_ACCURACY = "accuracy";

    // HEART RATE DATA column names
    private static final String KEY_REPORT_TEXT = "reportText";
    private static final String KEY_TIMESTAMP = "timestamp";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MONITOR_DATA_TABLE = "CREATE TABLE "+TABLE_MONITOR_DATA+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SENSOR_UPDATE_TIME + " TEXT, " +
                KEY_SENSOR_ID + " TEXT, " +
                KEY_USER_ID + " TEXT, " +
                KEY_ACCURACY + " TEXT, " +
                KEY_MEASUREMENT_TIME + " TEXT, " +
                KEY_SENSOR_VAL + " TEXT )";

        String CREATE_PHYS_STATE_TABLE = "CREATE TABLE "+TABLE_PHYS_STATE+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_STATE_TIME_STAMP + " TEXT, "+
                KEY_USER_ID + " TEXT, "+
                KEY_LEVEL + " TEXT, "+
                KEY_CONTEXT_DESCRIPTION + " TEXT)";

        String CREATE_USERS_TABLE = "CREATE TABLE "+TABLE_USERS+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_AVG_HEART_RATE + " TEXT, "+
                KEY_SENSITIVY_PREF + " TEXT, "+
                KEY_STDF_HEART_RATE + " TEXT)";

        String CREATE_HEART_RATE_DATA_TABLE = "CREATE TABLE "+TABLE_HEART_RATE_DATA+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_USER_ID + " TEXT, "+
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_HEART_RATE + " TEXT, "+
                KEY_ACCURACY + " TEXT, "+
                KEY_MEASUREMENT_TIME + " INTEGER)";

        String CREATE_SELF_REPORT_TABLE = "CREATE TABLE "+TABLE_SELF_REPORTS+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_USER_ID + " TEXT, "+
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_REPORT_TEXT + " TEXT, "+
                KEY_TIMESTAMP + " TEXT)";

        db.execSQL(CREATE_MONITOR_DATA_TABLE);
        db.execSQL(CREATE_PHYS_STATE_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_HEART_RATE_DATA_TABLE);
        db.execSQL(CREATE_SELF_REPORT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONITOR_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYS_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEART_RATE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELF_REPORTS);
        this.onCreate(db);
    }


    // ALL CALLS RELATED TO ADDING MONITOR DATA

    public void addMonitorData(MonitorDataModel monitorData) {
        Log.d("addMonitorData", monitorData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_UPDATE_TIME, monitorData.getSensorUpdateTime());
        values.put(KEY_SENSOR_ID, monitorData.getSensorId());
        values.put(KEY_USER_ID, monitorData.getUserId());
        values.put(KEY_SENSOR_VAL, monitorData.getSensorVal());
        values.put(KEY_ACCURACY, monitorData.getAccuracy());
        values.put(KEY_MEASUREMENT_TIME, monitorData.getMeasurementTime());
        db.insert(TABLE_MONITOR_DATA, null, values);
        db.close();
    }

    public List<MonitorDataModel> getAllUserMonitorData() {
        List<MonitorDataModel> monitorDatas = new LinkedList<MonitorDataModel>();
        String query = "SELECT  * FROM " + TABLE_MONITOR_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MonitorDataModel monitorData = null;
        if (cursor.moveToFirst()) {
            do {
                monitorData = new MonitorDataModel();
                monitorData.setId(Integer.parseInt(cursor.getString(0)));
                monitorData.setSensorUpdateTime(cursor.getString(1));
                monitorData.setSensorId(cursor.getString(2));
                monitorData.setUserId(cursor.getString(3));
                monitorData.setAccuracy(cursor.getString(4));
                monitorData.setMeasurementTime(cursor.getString(5));
                monitorData.setSensorVal(cursor.getString(6));
                monitorDatas.add(monitorData);
            } while (cursor.moveToNext());
        }
        return monitorDatas;
    }

    public List<MonitorDataModel> getAllUserMonitorDataByLastMeasurementTime(long measurementTime) {
        List<MonitorDataModel> monitorDatas = new LinkedList<MonitorDataModel>();
        String query = "SELECT  * FROM " + TABLE_MONITOR_DATA + " where measurementTime='" + String.valueOf(measurementTime) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MonitorDataModel monitorData = null;
        if (cursor.moveToFirst()) {
            do {
                monitorData = new MonitorDataModel();
                monitorData.setId(Integer.parseInt(cursor.getString(0)));
                monitorData.setSensorUpdateTime(cursor.getString(1));
                monitorData.setSensorId(cursor.getString(2));
                monitorData.setUserId(cursor.getString(3));
                monitorData.setAccuracy(cursor.getString(4));
                monitorData.setMeasurementTime(cursor.getString(5));
                monitorData.setSensorVal(cursor.getString(6));
                monitorDatas.add(monitorData);
            } while (cursor.moveToNext());
        }
        return monitorDatas;
    }

    public void deleteMeasurementDataByMeasurementTime(long measurementTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MONITOR_DATA, //table name
                "measurementTime" + " = ?",  // selections
                new String[]{String.valueOf(measurementTime)}); //selections args
        db.close();
        Log.d("db", "deleted");
    }


    // ALL CALLS RELATED PHYSSTATES

    // ADD PHYSSTATE
    public void addPhysState(PhysStateModel physState) {
        Log.d("addPhysState", physState.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATE_TIME_STAMP, physState.getStateTimeStamp());
        values.put(KEY_USER_ID, physState.getUserId());
        values.put(KEY_LEVEL, physState.getLevel());
        values.put(KEY_CONTEXT_DESCRIPTION, physState.getContextDescription());
        db.insert(TABLE_PHYS_STATE, null, values);
        db.close();
    }

    public PhysStateModel getPhysState(long state_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PHYS_STATE + " WHERE " + KEY_ID + " = " + state_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("DATABASE", "GETTING PhysSate ");
        if(c !=null) {
            Log.d("DATABASE", "USER NOT NILL");
            if (c.moveToFirst()) {
                PhysStateModel physState = new PhysStateModel();
                physState.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                physState.setUserId(c.getString(c.getColumnIndex(KEY_USER_ID)));
                physState.setLevel(c.getString(c.getColumnIndex(KEY_LEVEL)));
                physState.setStateTimeStamp(c.getString(c.getColumnIndex(KEY_STATE_TIME_STAMP)));
                physState.setContextDescription(c.getString(c.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
                return physState;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int updatePhysState(PhysStateModel physState){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CONTEXT_DESCRIPTION, physState.getContextDescription());

        Log.d("UPDATING PhysState", String.valueOf(physState.getId()));
        // updating row
        return db.update(TABLE_PHYS_STATE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(physState.getId()) });
    }


    // GET LIST OF ALL PHYSSTATES
    public List<PhysStateModel> getAllPhysStates() {
        List<PhysStateModel> physStates = new LinkedList<PhysStateModel>();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE + " ORDER BY stateTimeStamp DESC limit 500";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PhysStateModel physState = null;
        if (cursor.moveToFirst()) {
            do {
                physState = new PhysStateModel();
                physState.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                physState.setStateTimeStamp(cursor.getString(cursor.getColumnIndex(KEY_STATE_TIME_STAMP)));
                physState.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                physState.setLevel(cursor.getString(cursor.getColumnIndex(KEY_LEVEL)));
                physState.setContextDescription(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
                physStates.add(physState);
            } while (cursor.moveToNext());
        }
        return physStates;
    }

    //GET LAST PHYSSTATE
    public PhysStateModel getLastPhysState() {
        List<PhysStateModel> physStates = new LinkedList<PhysStateModel>();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE + " ORDER BY stateTimeStamp DESC limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PhysStateModel physState = null;
        if (cursor.moveToFirst()) {
            physState = new PhysStateModel();
            physState.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            physState.setStateTimeStamp(cursor.getString(cursor.getColumnIndex(KEY_STATE_TIME_STAMP)));
            physState.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
            physState.setLevel(cursor.getString(cursor.getColumnIndex(KEY_LEVEL)));
            physState.setContextDescription(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
        }
        Log.d("gettAllPhysStates()", physStates.toString());
        return physState;
    }

    //GET LAST PHYSSTATE
    public List<PhysStateModel> getLastFivePhysStates() {
        List<PhysStateModel> physStates = new LinkedList<PhysStateModel>();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE + " ORDER BY stateTimeStamp DESC limit 5";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PhysStateModel physState = null;
        if (cursor.moveToFirst()) {
            do {
                physState = new PhysStateModel();
                physState.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                physState.setStateTimeStamp(cursor.getString(cursor.getColumnIndex(KEY_STATE_TIME_STAMP)));
                physState.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                physState.setLevel(cursor.getString(cursor.getColumnIndex(KEY_LEVEL)));
                physState.setContextDescription(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
                physStates.add(physState);
            } while (cursor.moveToNext());
        }
        return physStates;
    }


    // ALL CALLS RELATED TO USERS
    public void addUser(UserModel userData) {
        Log.d("addUserData", userData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UNIQUE_USER_ID, userData.getUniqueUserId());
        values.put(KEY_AVG_HEART_RATE, userData.getAvgHeartRate());
        values.put(KEY_STDF_HEART_RATE, userData.getStdfHeartRate());
        values.put(KEY_SENSITIVY_PREF, userData.getSensitivityPref());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public UserModel getUser(long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_ID + " = " + user_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("DATABASE", "GETTING USER");
        if(c !=null) {
            Log.d("DATABASE", "USER NOT NILL");
            if (c.moveToFirst()) {
                UserModel user = new UserModel();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                user.setAvgHeartRate(c.getString(c.getColumnIndex(KEY_AVG_HEART_RATE)));
                user.setStdfHeartRate(c.getString(c.getColumnIndex(KEY_STDF_HEART_RATE)));
                user.setSensitivityPref(c.getString(c.getColumnIndex(KEY_SENSITIVY_PREF)));
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int updateUser(UserModel user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STDF_HEART_RATE, user.getStdfHeartRate());
        values.put(KEY_AVG_HEART_RATE, user.getAvgHeartRate());
        values.put(KEY_SENSITIVY_PREF, user.getSensitivityPref());
        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new LinkedList<UserModel>();
        String query = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        UserModel user = null;
        if (c.moveToFirst()) {
            do {
                user = new UserModel();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                user.setAvgHeartRate(c.getString(c.getColumnIndex(KEY_AVG_HEART_RATE)));
                user.setStdfHeartRate(c.getString(c.getColumnIndex(KEY_STDF_HEART_RATE)));
                user.setSensitivityPref(c.getString(c.getColumnIndex(KEY_SENSITIVY_PREF)));
                users.add(user);
            } while (c.moveToNext());
        }
        return users;
    }

    public void addHeartRateData(HeartRateDataModel heartRateData) {
        Log.d("addHRData", heartRateData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, heartRateData.getUserId());
        values.put(KEY_UNIQUE_USER_ID, heartRateData.getUniqueUserId());
        values.put(KEY_HEART_RATE, heartRateData.getHeartRate());
        values.put(KEY_ACCURACY, heartRateData.getAccuracy());


        values.put(KEY_MEASUREMENT_TIME, heartRateData.getMeasurementTime());

        Log.d("addHRData - Values", values.toString());
        db.insert(TABLE_HEART_RATE_DATA, null, values);
        db.close();
    }

    public List<HeartRateDataModel> getAllHeartRate() {
        List<HeartRateDataModel> heartRates = new LinkedList<HeartRateDataModel>();
        String query = "SELECT  * FROM " + TABLE_HEART_RATE_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HeartRateDataModel heartRate = null;
        if (cursor.moveToFirst()) {
            do {
                heartRate = new HeartRateDataModel();
                heartRate.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                heartRate.setUniqueUserId(cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_USER_ID)));
                heartRate.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                heartRate.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                heartRate.setAccuracy(cursor.getString(cursor.getColumnIndex(KEY_ACCURACY)));
                heartRate.setMeasurementTime(cursor.getLong(cursor.getColumnIndex(KEY_MEASUREMENT_TIME)));
                heartRates.add(heartRate);
            } while (cursor.moveToNext());
        }
        return heartRates;
    }


    public List<HeartRateDataModel> getAllHeartRateAfterTimeStamp(Long measurementTime) {
        List<HeartRateDataModel> heartRates = new LinkedList<HeartRateDataModel>();
        String query = "SELECT  * FROM " + TABLE_HEART_RATE_DATA + " WHERE measurementTime > '" + measurementTime + "'";;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HeartRateDataModel heartRate = null;
        if (cursor.moveToFirst()) {
            do {
                heartRate = new HeartRateDataModel();
                heartRate.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                heartRate.setUniqueUserId(cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_USER_ID)));
                heartRate.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                heartRate.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                heartRate.setAccuracy(cursor.getString(cursor.getColumnIndex(KEY_ACCURACY)));
                heartRate.setMeasurementTime(cursor.getLong(cursor.getColumnIndex(KEY_MEASUREMENT_TIME)));
                heartRates.add(heartRate);
            } while (cursor.moveToNext());
        }
        return heartRates;
    }

    public List<HeartRateDataModel> getLatestMeasures() {
        List<HeartRateDataModel> heartRates = new LinkedList<HeartRateDataModel>();
        String query = "SELECT  * FROM (SELECT * FROM " + TABLE_HEART_RATE_DATA + " ORDER BY measurementTime DESC limit 10) ORDER BY measurementTime DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HeartRateDataModel heartRate = null;
        if (cursor.moveToFirst()) {
            do {
                heartRate = new HeartRateDataModel();
                heartRate.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                heartRate.setUniqueUserId(cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_USER_ID)));
                heartRate.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                heartRate.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                heartRate.setAccuracy(cursor.getString(cursor.getColumnIndex(KEY_ACCURACY)));
                heartRate.setMeasurementTime(cursor.getLong(cursor.getColumnIndex(KEY_MEASUREMENT_TIME)));
                heartRates.add(heartRate);
            } while (cursor.moveToNext());
        }
        Log.d("getlatest10heartrates()", heartRates.toString());
        return heartRates;
    }

    // ALL CALLS RELATED TO SELF REPORTS
    public void addSelfReport(SelfReportModel selfReportData) {
        Log.d("addSelfReport", selfReportData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, selfReportData.getUserId());
        values.put(KEY_UNIQUE_USER_ID, selfReportData.getUniqueUserId());
        values.put(KEY_REPORT_TEXT, selfReportData.getReportText());
        values.put(KEY_TIMESTAMP, selfReportData.getTimestamp());
        db.insert(TABLE_SELF_REPORTS, null, values);
        db.close();
    }

    public SelfReportModel getSelfReport(long self_report_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_SELF_REPORTS + " WHERE " + KEY_ID + " = " + self_report_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("DATABASE", "GETTING SELF REPORT FOR USER");
        if(c !=null) {
            Log.d("DATABASE", "USER NOT NILL");
            if (c.moveToFirst()) {
                SelfReportModel selfReport = new SelfReportModel();
                selfReport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                selfReport.setUserId(c.getString(c.getColumnIndex(KEY_USER_ID)));
                selfReport.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                selfReport.setReportText(c.getString(c.getColumnIndex(KEY_REPORT_TEXT)));
                selfReport.setTimeStamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
                return selfReport;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int updateSelfReport(SelfReportModel selfReport){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REPORT_TEXT, selfReport.getReportText());
        values.put(KEY_TIMESTAMP, selfReport.getTimestamp());

        Log.d("UPDATING SELF REPORT", String.valueOf(selfReport.getId()));

        Log.d("UPDATING SELF REPORT", selfReport.getReportText());
        // updating row
        return db.update(TABLE_SELF_REPORTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(selfReport.getId()) });
    }

    public int deleteSelfReport(SelfReportModel selfReport){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Delete Self report", String.valueOf(selfReport.getId()));
        return db.delete(TABLE_SELF_REPORTS, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(selfReport.getId())}); //selections args
    }

    public List<SelfReportModel> getAllSelfReports() {
        List<SelfReportModel> selfReports = new LinkedList<SelfReportModel>();
        String query = "SELECT  * FROM " + TABLE_SELF_REPORTS + " ORDER BY timestamp DESC limit 500";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        SelfReportModel selfReport = null;

        if (c.moveToFirst()) {
            do {
                selfReport = new SelfReportModel();
                selfReport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                selfReport.setUserId(c.getString(c.getColumnIndex(KEY_USER_ID)));
                selfReport.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                selfReport.setReportText(c.getString(c.getColumnIndex(KEY_REPORT_TEXT)));
                selfReport.setTimeStamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
                selfReports.add(selfReport);
            } while (c.moveToNext());
        }
        return selfReports;
    }
}
