package com.jacmobile.halloween.di.modules;


import android.content.Context;
import android.hardware.SensorManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import com.jacmobile.halloween.app.lifecyclemonitor.ActivityLifecycleMonitor;
import com.jacmobile.halloween.app.App;
import com.jacmobile.halloween.presenter.sensors.Magnetometer;
import com.jacmobile.halloween.util.ForApplication;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

@Module public class AppModule
{
    private final App app;

    public AppModule(App app)
    {
        this.app = app;
    }

    @Provides @Singleton @ForApplication Context provideApplication()
    {
        return this.app;
    }

    @Provides @Singleton ActivityLifecycleMonitor provideActivityLifecycleMonitor()
    {
        ActivityLifecycleMonitor lifecycleMonitor = new ActivityLifecycleMonitor();
        app.registerActivityLifecycleCallbacks(lifecycleMonitor);
        return lifecycleMonitor;
    }

    @Provides @Singleton SensorManager provideSensorManager()
    {
        return (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides @Singleton Magnetometer provideMagnetometer(SensorManager sensorManager)
    {
        return new Magnetometer(sensorManager);
    }

    @Provides @Singleton Bus provideBus()
    {
        return new Bus(ThreadEnforcer.ANY);
    }
}
