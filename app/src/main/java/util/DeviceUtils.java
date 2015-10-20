package util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

public class DeviceUtils
{
    private static final String TAG = DeviceUtils.class.getSimpleName();

    private DeviceUtils() { throw new RuntimeException(TAG); }

    /** Check if external storage is available for read and write. */
    public static boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        state = TextUtils.isEmpty(state) ? "" : state; //just in case?
        return  Environment.MEDIA_MOUNTED.equals(state);
    }

    /** Check if this device has a camera */
    public static boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
