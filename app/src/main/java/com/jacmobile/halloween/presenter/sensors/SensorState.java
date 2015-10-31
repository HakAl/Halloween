package com.jacmobile.halloween.presenter.sensors;

import android.hardware.SensorManager;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class SensorState
{
    public static final int UNRELIABLE = SensorManager.SENSOR_STATUS_UNRELIABLE;
    public static final int LOW_ACCURACY = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
    public static final int MEDIUM_ACCURACY = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
    public static final int HIGH_ACCURACY = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNRELIABLE, LOW_ACCURACY, MEDIUM_ACCURACY, HIGH_ACCURACY})
    public @interface SensorAccuracy {}

    public abstract @SensorState.SensorAccuracy int getSensorAccuracy();
    public abstract void setSensorAccuracy(@SensorState.SensorAccuracy int sensorAccuracy);
}
