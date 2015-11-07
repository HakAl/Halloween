package com.jacmobile.halloween.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jacmobile.halloween.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class ActivityLifecycleMonitor implements Application.ActivityLifecycleCallbacks, Subject
{
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    private boolean changed;
    private final Object mutex = new Object();
    private List<Observer> observers = new ArrayList<>();

    @Override public void onActivityResumed(Activity activity)
    {
        ++resumed;
        Logger.debugLog(activity.getClass().getSimpleName() + " onResume() called.");
    }

    @Override public void onActivityPaused(Activity activity)
    {
        ++paused;
        Logger.debugLog(activity.getClass().getSimpleName() + " onPause() called.");
    }

    @Override public void onActivityStarted(Activity activity)
    {
        ++started;
        Logger.debugLog(activity.getClass().getSimpleName() + " onStart() called.");
    }

    @Override public void onActivityStopped(Activity activity)
    {
        ++stopped;
        Logger.debugLog(activity.getClass().getSimpleName() + " onStop() called.");
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState)
    {
        Logger.debugLog(activity.getClass().getSimpleName() + " onCreate() called.");
    }

    @Override public void onActivityDestroyed(Activity activity)
    {
        Logger.debugLog(activity.getClass().getSimpleName() + " onDestroy() called.");
    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState)
    {
    }

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
}