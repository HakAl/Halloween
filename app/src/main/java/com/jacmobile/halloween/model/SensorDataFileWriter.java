package com.jacmobile.halloween.model;

import android.content.Context;

import com.jacmobile.halloween.app.lifecyclemonitor.ActivityLifecycleMonitor;
import com.jacmobile.halloween.app.lifecyclemonitor.Subject;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnStopCallback;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SensorDataFileWriter implements OnStopCallback
{
    @Inject Bus bus;
    @Inject ActivityLifecycleMonitor lifecycleMonitor;

    Context context;
    ArrayList<SensorData> datas;

    @Inject SensorDataFileWriter() {}

    public void initialize(Context context)
    {
        this.context = context.getApplicationContext();
        datas = new ArrayList<>();
        bus.register(this);
        lifecycleMonitor.register(this);
    }

    @Subscribe public void event(SensorData data)
    {
        datas.add(data);
    }

    /**
     * Indicates onStop() has been called in the Activity this was initialized in.
     */
    @Override public void update()
    {
        StringBuilder sb = new StringBuilder("");
        for (SensorData data :
                datas) {
            sb.append(data.toString());
            sb.append(",");
        }
        FileStoreService.start(context, sb.toString());
        lifecycleMonitor.unregister(this);
        bus.unregister(this);
    }

    @Override public void setSubject(Subject sub)
    {
    }
}
