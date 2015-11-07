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
    private List<OnStopCallback> stopObservers = new ArrayList<>();
    private List<OnPauseCallback> pauseObservers = new ArrayList<>();
    private List<OnResumeCallback> resumeObservers = new ArrayList<>();
    private List<OnStartCallback> startObservers = new ArrayList<>();

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
        postUpdate();
    }

    @Override public void onActivityStopped(Activity activity)
    {
        ++stopped;
        postUpdate();
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

    @Override public void register(@NonNull Observer obj)
    {
        synchronized (mutex) {
            if (obj instanceof OnPauseCallback) registerPause((OnPauseCallback) obj);
            if (obj instanceof OnStartCallback) registerStart((OnStartCallback) obj);
            if (obj instanceof OnResumeCallback) registerResume((OnResumeCallback) obj);
            if (obj instanceof OnStopCallback) registerStop((OnStopCallback) obj);
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
     * Synchronization is used to make sure any observer registered after message is received is not
     * notified
     */
    @Override public void notifyObservers()
    {
        if (stopObservers.size() > 0) notify(Lifecycle.STOPPED);
        if (startObservers.size() > 0) notify(Lifecycle.STARTED);
        if (pauseObservers.size() > 0) notify(Lifecycle.PAUSED);
        if (resumeObservers.size() > 0) notify(Lifecycle.RESUMED);
    }

    private void registerStart(@NonNull OnStartCallback observer)
    {
        synchronized (mutex) {
            if (!startObservers.contains(observer)) {
                observer.setSubject(ActivityLifecycleMonitor.this);
                startObservers.add(observer);
            }
        }
    }

    private void registerPause(@NonNull OnPauseCallback observer)
    {
        synchronized (mutex) {
            if (!pauseObservers.contains(observer)) {
                observer.setSubject(ActivityLifecycleMonitor.this);
                pauseObservers.add(observer);
            }
        }
    }

    private void registerStop(@NonNull OnStopCallback observer)
    {
        synchronized (mutex) {
            if (!stopObservers.contains(observer)) {
                observer.setSubject(this);
                stopObservers.add(observer);
            }
        }
    }

    private void registerResume(@NonNull OnResumeCallback observer)
    {
        synchronized (mutex) {
            if (!resumeObservers.contains(observer)) {
                observer.setSubject(this);
                resumeObservers.add(observer);
            }
        }
    }

    private void notify(int which)
    {
        List<OnStopCallback> stopObserversLocal = null;
        List<OnStartCallback> startObserversLocal = null;
        List<OnResumeCallback> resumeObserversLocal = null;
        List<OnPauseCallback> pauseObserversLocal = null;
        synchronized (mutex) {
            if (!changed) return;

            if (which == Lifecycle.STOPPED) {
                stopObserversLocal = new ArrayList<>(this.stopObservers);
            }
            if (which == Lifecycle.STARTED) {
                startObserversLocal = new ArrayList<>(this.startObservers);
            }
            if (which == Lifecycle.RESUMED) {
                resumeObserversLocal = new ArrayList<>(this.resumeObservers);
            }
            if (which == Lifecycle.PAUSED) {
                pauseObserversLocal = new ArrayList<>(this.pauseObservers);
            }

            if (which == Lifecycle.STOPPED) {
                for (Observer obj : stopObserversLocal) {
                    obj.update();
                }
            }
            if (which == Lifecycle.STARTED) {
                for (Observer obj : startObserversLocal) {
                    obj.update();
                }
            }
            if (which == Lifecycle.RESUMED) {
                for (Observer obj : resumeObserversLocal) {
                    obj.update();
                }
            }
            if (which == Lifecycle.PAUSED) {
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
    }

////Don't seem to be called
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override public void onActivityDestroyed(Activity activity) {}

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
}