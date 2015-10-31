package com.jacmobile.halloween.util;

import android.util.Log;

import com.jacmobile.halloween.BuildConfig;

public class Logger
{
    public static final String TAG = Logger.class.getSimpleName();

    public static void debugLog(String toLog)
    {
        if (BuildConfig.DEBUG) Log.wtf(TAG, toLog);
    }

    public static void exception(String toLog)
    {
        if (BuildConfig.DEBUG) Log.e(TAG, toLog);
    }
}
