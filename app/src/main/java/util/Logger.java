package util;

import android.util.Log;

import com.jacmobile.spiritdetector.BuildConfig;

public class Logger
{
    public static final String TAG = Logger.class.getSimpleName();

    public static void log(String toLog)
    {
        if (BuildConfig.DEBUG) Log.wtf(TAG, toLog);
    }

    public static void debugLog(String toLog)
    {
        if (BuildConfig.DEBUG) Log.d(TAG, toLog);
    }

    public static void exception(String toLog)
    {
        if (BuildConfig.DEBUG) Log.e(TAG, toLog);
    }
}
