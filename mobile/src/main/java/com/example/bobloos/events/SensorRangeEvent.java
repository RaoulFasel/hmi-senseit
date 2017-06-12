package com.example.bobloos.events;

import com.example.bobloos.data.Sensor;

/**
 * Created by bob.loos on 14/05/16.
 */
public class SensorRangeEvent {
    private Sensor sensor;

    public SensorRangeEvent(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }
}

