package com.jacmobile.halloween.presenter.sensors;

import com.jacmobile.halloween.model.SensorData;

public interface SensorDataListener
{
    void sensorUpdate(SensorData sensorData);
}
