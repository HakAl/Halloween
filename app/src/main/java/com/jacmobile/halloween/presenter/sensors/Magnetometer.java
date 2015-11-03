package com.jacmobile.halloween.presenter.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Magnetometer implements SensorEventListener
{
    /** Maximum magnetic field on Earth's surface */
    public static final float MAGNETIC_FIELD_EARTH_MAX = 60.0f;
    /** Minimum magnetic field on Earth's surface */
    public static final float MAGNETIC_FIELD_EARTH_MIN = 30.0f;
    /** Constant used to smooth data */
    private static final float ALPHA = .9f;

    private int counter = 0;
    private List<float[]> eventValues;
    private static final int SAMPLE_SIZE = 10;

    private Sensor magnetometer;
    private SensorManager sensorManager;
    private SensorData sensorData;
    private SensorDataListener sensorDataListener;

    public Magnetometer(SensorManager sensorManager)
    {
        this.sensorManager = sensorManager;
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.eventValues = new ArrayList<>(SAMPLE_SIZE);
    }

    public void resume(SensorDataListener sensorDataListener)
    {
        this.sensorData = new SensorData();
        this.sensorDataListener = sensorDataListener;
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void pause()
    {
        sensorManager.unregisterListener(this);
        sensorDataListener = null;
        sensorData = null;
        magnetometer = null;
        eventValues.clear();
        eventValues = null;
    }

    @Override public void onSensorChanged(SensorEvent event)
    {
        if (event.values.length == 3) {
            counter++;
            eventValues.add(new float[] {
                    Math.abs(ALPHA * event.values[0] + (1 - ALPHA) * event.values[0]),
                    Math.abs(ALPHA * event.values[1] + (1 - ALPHA) * event.values[1]),
                    Math.abs(ALPHA * event.values[2] + (1 - ALPHA) * event.values[2])});

            if (counter == SAMPLE_SIZE) {
                counter = 0;
                float x = 0, y = 0, z = 0;
                for (float[] array : eventValues) {
                    x += array[0];
                    y += array[1];
                    z += array[2];
                }
                x = x / SAMPLE_SIZE;
                y = y / SAMPLE_SIZE;
                z = z / SAMPLE_SIZE;
                eventValues.clear();
                sensorData.clear();
                sensorData.setX(x);
                sensorData.setY(y);
                sensorData.setZ(z);
                sensorData.setAverage((float) Math.ceil((x + y + z) / 3));
                sensorData.setTimeStamp(event.timestamp);
                sensorDataListener.sensorUpdate(sensorData);
            }

        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        sensorData.setAccuracy(accuracy);
    }
}