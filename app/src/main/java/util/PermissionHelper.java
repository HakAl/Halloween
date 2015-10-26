package util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

/*
  Usage:

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (M_PermissionHelper.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
            startCameraPreview();
        } else {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(perms, REQUEST_CODE);
        }
    }

  requestPermissions(perms, REQUEST_CODE); calls back here:

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)

 */
@TargetApi(Build.VERSION_CODES.M)
public class PermissionHelper
{
    /**
     * @param activity The requesting Activity context
     * @param permission the desired android.Manifest.permission
     * @return true if permission is granted
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermission(@NonNull Activity activity, @NonNull String permission)
    {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    public static boolean hasPermission(@NonNull Activity activity, @NonNull String[] permissions)
//    {
//        boolean hasPermission =
//        for (String s : permissions) {
//
//        }
//        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
//    }
}
