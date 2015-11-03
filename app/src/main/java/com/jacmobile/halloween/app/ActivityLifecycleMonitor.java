package com.jacmobile.halloween.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ActivityLifecycleMonitor implements Application.ActivityLifecycleCallbacks, Subject
{
    public static final String TAG = ActivityLifecycleMonitor.class.getSimpleName();

    private List<Observer> observers = new ArrayList<>();
    private boolean changed;
    private final Object mutex = new Object();

    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

////Static Utility Methods

    public static boolean isApplicationVisible()
    {
        return started > stopped;
    }

    public static boolean isApplicationInForeground()
    {
        return resumed > paused;
    }

////Register an Observer to get updates

    @Override public void register(@NonNull Observer obj)
    {
        synchronized (mutex) {
            if (!observers.contains(obj)) {
                obj.setSubject(this);
                observers.add(obj);
            }
        }
    }

    @Override public void unregister(@NonNull Observer obj)
    {
        synchronized (mutex) {
            observers.remove(obj);
        }
    }

    @Override public void notifyObservers()
    {
        List<Observer> observersLocal = null;
        //synchronization is used to make sure any observer
        //registered after message is received is not notified
        synchronized (mutex) {
            if (!changed)
                return;
            observersLocal = new ArrayList<>(this.observers);
            this.changed = false;
        }
        for (Observer obj : observersLocal) {
            obj.update();
        }
    }

    /**
     * @return isApplicationInForeground() ? resumed or paused
     */
    @Override public Object getUpdate(@NonNull Observer obj)
    {
        return isApplicationInForeground();
    }

    public void postUpdate()
    {
        this.changed = true;
        notifyObservers();
    }

    @Override public void onActivityResumed(Activity activity)
    {
        ++resumed;
        postUpdate();
    }

    @Override public void onActivityPaused(Activity activity)
    {
        ++paused;
        postUpdate();
    }

    @Override public void onActivityStarted(Activity activity)
    {
        ++started;
    }

    @Override public void onActivityStopped(Activity activity)
    {
        ++stopped;
    }

////unused

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState)
    {

    }

    @Override public void onActivityDestroyed(Activity activity)
    {
    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState)
    {
    }
}