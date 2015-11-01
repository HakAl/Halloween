package com.jacmobile.halloween.presenter.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jacmobile.halloween.app.ActivityLifecycleMonitor;
import com.jacmobile.halloween.app.App;
import com.jacmobile.halloween.model.SensorData;
import com.jacmobile.halloween.util.Logger;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Updates will be published in a {@link SensorData event}
 */
public class MagnetometerService extends Service implements SensorDataListener
{
    @Inject Bus bus;
    @Inject Magnetometer magnetometer;
    @Inject ActivityLifecycleMonitor activityLifecycleMonitor;

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override public void onCreate()
    {
        super.onCreate();
        ((App) getApplicationContext()).getAppComponent().inject(this);
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        magnetometer.resume(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        magnetometer.pause();
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
