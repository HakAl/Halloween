package com.jacmobile.halloween.presenter.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jacmobile.halloween.app.App;
import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.model.SensorDataFileWriter;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Updates will be published in a {@link SensorData event}
 */
public class MagnetometerService extends Service implements SensorDataListener
{
    @Inject Bus bus;
    @Inject Magnetometer magnetometer;
    @Inject SensorDataFileWriter fileWriter;

    public static void start(Context context)
    {
        context.startService(new Intent(context, MagnetometerService.class));
    }

    public static void stop(Context context)
    {
        context.stopService(new Intent(context, MagnetometerService.class));
    }

    @Override public void onCreate()
    {
        super.onCreate();
        ((App) getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        fileWriter.initialize(this);
        magnetometer.resume(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onDestroy()
    {
        magnetometer.pause();
        super.onDestroy();
    }

    @Override public void sensorUpdate(SensorData sensorData)
    {
        bus.post(sensorData);
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return null;
    }
}