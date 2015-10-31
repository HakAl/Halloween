package com.jacmobile.halloween.app;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class Lifecycle
{
    public static final int RESUMED = 0;
    public static final int STARTED = 1;
    public static final int PAUSED = 2;
    public static final int STOPPED = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESUMED, STARTED, PAUSED, STOPPED})
    public @interface ActivityState {}

    abstract @Lifecycle.ActivityState int getActivityState();
    abstract @Lifecycle.ActivityState void setActivityState(@Lifecycle.ActivityState int state);
}