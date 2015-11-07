package com.jacmobile.halloween.app.lifecyclemonitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jacmobile.halloween.app.lifecyclemonitor.observers.Observer;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnPauseCallback;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnResumeCallback;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnStartCallback;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnStopCallback;
import com.jacmobile.halloween.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class ActivityLifecycleMonitor implements Application.ActivityLifecycleCallbacks, LifecycleRegistry
{
    private static final int STOP = 0;
    private static final int START = 1;
    private static final int RESUME = 2;
    private static final int PAUSE = 3;
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    private boolean changed;
    private final Object mutex = new Object();
    private List<OnStopCallback> stopObservers = new ArrayList<>();
    private List<OnPauseCallback> pauseObservers = new ArrayList<>();
    private List<OnResumeCallback> resumeObservers = new ArrayList<>();
    private List<OnStartCallback> startObservers = new ArrayList<>();

    @Override public void onActivityResumed(Activity activity)
    {
        ++resumed;
        postUpdate();
        Logger.debugLog(activity.getClass().getSimpleName() + " onResume() called.");
    }

    @Override public void onActivityPaused(Activity activity)
    {
        ++paused;
        postUpdate();
        Logger.debugLog(activity.getClass().getSimpleName() + " onPause() called.");
    }

    @Override public void onActivityStarted(Activity activity)
    {
        ++started;
        postUpdate();
        Logger.debugLog(activity.getClass().getSimpleName() + " onStart() called.");
    }

    @Override public void onActivityStopped(Activity activity)
    {
        ++stopped;
        postUpdate();
        Logger.debugLog(activity.getClass().getSimpleName() + " onStop() called.");
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

////Register an lifecycle callback to get updates

    @Override public void register(@NonNull OnStartCallback observer)
    {
        synchronized (mutex) {
            if (!startObservers.contains(observer)) {
                observer.setSubject(ActivityLifecycleMonitor.this);
                startObservers.add(observer);
            }
        }
    }

    @Override public void register(@NonNull OnPauseCallback observer)
    {
        synchronized (mutex) {
            if (!pauseObservers.contains(observer)) {
                observer.setSubject(ActivityLifecycleMonitor.this);
                pauseObservers.add(observer);
            }
        }
    }

    @Override public void register(@NonNull OnStopCallback observer)
    {
        synchronized (mutex) {
            if (!stopObservers.contains(observer)) {
                observer.setSubject(this);
                stopObservers.add(observer);
            }
        }
    }

    @Override public void register(@NonNull OnResumeCallback observer)
    {
        synchronized (mutex) {
            if (!resumeObservers.contains(observer)) {
                observer.setSubject(this);
                resumeObservers.add(observer);
            }
        }
    }

    @Override public void unregister(@NonNull Observer obj)
    {
        synchronized (mutex) {
            if (obj instanceof OnPauseCallback) pauseObservers.remove(obj);
            if (obj instanceof OnStartCallback) startObservers.remove(obj);
            if (obj instanceof OnResumeCallback) resumeObservers.remove(obj);
            if (obj instanceof OnStopCallback) stopObservers.remove(obj);
        }
    }

    /**
     * Synchronization is used to make sure any observer
     * registered after message is received is not notified
     */
    @Override public void notifyObservers()
    {
        if (stopObservers.size() > 0) notify(STOP);
        if (startObservers.size() > 0)  notify(START);
        if (pauseObservers.size() > 0)  notify(PAUSE);
        if (resumeObservers.size() > 0)  notify(RESUME);
    }

    void notify(int which)
    {
        List<OnStopCallback> stopObserversLocal = null;
        List<OnStartCallback> startObserversLocal = null;
        List<OnResumeCallback> resumeObserversLocal = null;
        List<OnPauseCallback> pauseObserversLocal = null;
        synchronized (mutex) {
            if (!changed) return;

            if (which == STOP) {
                stopObserversLocal = new ArrayList<>(this.stopObservers);
            }
            if (which == START) {
                startObserversLocal = new ArrayList<>(this.startObservers);
            }
            if (which == RESUME) {
                resumeObserversLocal = new ArrayList<>(this.resumeObservers);
            }
            if (which == PAUSE) {
                pauseObserversLocal = new ArrayList<>(this.pauseObservers);
            }

            if (which == STOP) {
                for (Observer obj : stopObserversLocal) {
                    obj.update();
                }
            }
            if (which == START) {
                for (Observer obj : startObserversLocal) {
                    obj.update();
                }
            }
            if (which == RESUME) {
                for (Observer obj : resumeObserversLocal) {
                    obj.update();
                }
            }
            if (which == PAUSE) {
                for (Observer obj : pauseObserversLocal) {
                    obj.update();
                }
            }

            this.changed = false;
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
        if (pauseObservers.size() > 0) notifyObservers();
        if (resumeObservers.size() > 0) notifyObservers();
        if (startObservers.size() > 0) notifyObservers();
    }

    //don't seem to be called
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
}