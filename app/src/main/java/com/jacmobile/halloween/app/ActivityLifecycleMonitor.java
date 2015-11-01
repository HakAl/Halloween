package com.jacmobile.halloween.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jacmobile.halloween.BuildConfig;
import com.jacmobile.halloween.util.Logger;

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

    private CurrentState currentState;

    public ActivityLifecycleMonitor()
    {
        this.currentState = new CurrentState();
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

    @Override public void onActivityResumed(Activity activity)
    {
        ++resumed;
        Logger.debugLog(BuildConfig.APPLICATION_ID + " is visible: " + (started > stopped));
        this.currentState.setActivityState(Lifecycle.RESUMED);
        postUpdate();
    }

    @Override public void onActivityPaused(Activity activity)
    {
        ++paused;
        Logger.debugLog(BuildConfig.APPLICATION_ID + " is in foreground: " + (resumed > paused));
        this.currentState.setActivityState(Lifecycle.PAUSED);
        postUpdate();
    }

    @Override public void onActivityStarted(Activity activity)
    {
        ++started;
        Logger.debugLog(BuildConfig.APPLICATION_ID + " is in foreground: " + (resumed > paused));
        this.currentState.setActivityState(Lifecycle.STARTED);
    }

    @Override public void onActivityStopped(Activity activity)
    {
        ++stopped;
        Logger.debugLog(BuildConfig.APPLICATION_ID + " is visible: " + (started > stopped));
        this.currentState.setActivityState(Lifecycle.STOPPED);
    }

////Lifecycle state

    class CurrentState extends Lifecycle
    {
        private @Lifecycle.ActivityState int state;

        public CurrentState()
        {
            this.state = STOPPED;
        }

        @Override @Lifecycle.ActivityState int getActivityState()
        {
            return state;
        }

        @Override void setActivityState(@ActivityState int state)
        {
            this.state = state;
        }
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