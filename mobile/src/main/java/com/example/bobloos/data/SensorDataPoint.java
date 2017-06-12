package com.example.bobloos.data;

/**
 * Created by bob.loos on 14/05/16.
 */
public class SensorDataPoint {
    private long timestamp;
    private float[] values;
    private float accuracy;

    public SensorDataPoint(long timestamp, float accuracy, float[] values) {
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.values = values;
    }

    public float[] getValues() {
        return values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }
}
