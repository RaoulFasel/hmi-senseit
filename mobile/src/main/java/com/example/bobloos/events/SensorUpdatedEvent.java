package com.example.bobloos.events;

import com.example.bobloos.data.Sensor;
import com.example.bobloos.data.SensorDataPoint;

/**
 * Created by bob.loos on 14/05/16.
 */
public class SensorUpdatedEvent {
    private Sensor sensor;
    private SensorDataPoint sensorDataPoint;

    public SensorUpdatedEvent(Sensor sensor, SensorDataPoint sensorDataPoint) {
        this.sensor = sensor;
        this.sensorDataPoint = sensorDataPoint;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public SensorDataPoint getDataPoint() {
        return sensorDataPoint;
    }
}

