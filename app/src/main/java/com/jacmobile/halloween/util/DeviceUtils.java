package com.jacmobile.halloween.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;

public class DeviceUtils
{
    /**
     * @return if external storage is available for read and write.
     */
    public static boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        state = TextUtils.isEmpty(state) ? "" : state; //just in case?
        return  Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * @param context {@link android.content.Context}
     * @return if device has Camera
     */
    public static boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * @param context {@link android.content.Context}
     * @return if the vibration occurred
     */
    public static boolean vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(333);
            return true;
        }
        return false;
    }

    private static final String TAG = DeviceUtils.class.getSimpleName();
    private DeviceUtils() { throw new RuntimeException(TAG); }
}
