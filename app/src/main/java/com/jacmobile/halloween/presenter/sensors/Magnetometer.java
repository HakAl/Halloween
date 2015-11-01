package com.jacmobile.halloween.presenter.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.util.Logger;

public class Magnetometer implements SensorEventListener
{
    private static final String TAG = Magnetometer.class.getSimpleName();

    /** Maximum magnetic field on Earth's surface */
    public static final float MAGNETIC_FIELD_EARTH_MAX = 60.0f;
    /** Minimum magnetic field on Earth's surface */
    public static final float MAGNETIC_FIELD_EARTH_MIN = 30.0f;
    private static final float ALPHA = .9f;

    private float currentX;
    private float currentY;
    private float currentZ;
    private float average;
    private Sensor magnetometer;
    private SensorManager sensorManager;
    private SensorData sensorData;
    private SensorDataListener sensorDataListener;

    public @SensorState.SensorAccuracy int accuracy;

    /** The time in nanosecond at which the event happened */
    public long timestamp;

    public Magnetometer(SensorManager sensorManager)
    {
        Logger.debugLog("Magnetometer initialized");
        this.sensorManager = sensorManager;
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void resume(SensorDataListener sensorDataListener)
    {
        Logger.debugLog("Magnetometer resumed");
        this.sensorData = new SensorData();
        this.sensorDataListener = sensorDataListener;
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pause()
    {
        Logger.debugLog("Magnetometer paused");
        sensorManager.unregisterListener(this);
        this.sensorDataListener = null;
        this.sensorData = null;
    }

    /** Called when sensor values have changed. */
    @Override public void onSensorChanged(SensorEvent event)
    {
        if (event.values.length == 3) {
            currentX = Math.abs(ALPHA * currentX + (1 - ALPHA) * event.values[0]);
            currentY = Math.abs(ALPHA * currentX + (1 - ALPHA) * event.values[1]);
            currentZ = Math.abs(ALPHA * currentX + (1 - ALPHA) * event.values[2]);
            average = (float) Math.floor((currentX * currentY * currentZ) / 3);
            timestamp = event.timestamp;
            sensorData.setAverage(average);
            sensorData.setX(currentX);
            sensorData.setY(currentY);
            sensorData.setZ(currentZ);
            sensorData.setTimeStamp(timestamp);

            sensorDataListener.sensorUpdate(sensorData);
        }
    }

    /** Called when the accuracy of the registered sensor has changed. */
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Logger.debugLog(TAG+" onAccuracyChanged() Accuracy: "+ accuracy);
        this.accuracy = accuracy;
        sensorData.setAccuracy(accuracy);
    }
}
