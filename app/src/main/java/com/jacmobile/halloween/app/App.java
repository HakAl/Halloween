package com.jacmobile.halloween.app;

import android.app.Application;

import com.jacmobile.halloween.app.lifecyclemonitor.ActivityLifecycleMonitor;
import com.jacmobile.halloween.di.components.AppComponent;
import com.jacmobile.halloween.di.components.DaggerAppComponent;
import com.jacmobile.halloween.di.modules.AppModule;

public class App extends Application
{
    private AppComponent appComponent;

    @Override public void onCreate()
    {
        super.onCreate();
//        LeakCanary.install(this);
        this.appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent()
    {
        return appComponent;
    }
}
