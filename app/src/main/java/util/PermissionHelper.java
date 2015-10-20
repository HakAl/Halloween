package util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * <strong>APIs used in this class require Android M SDK. API 23 or greater.</strong>
 * <p/>
 * https://developer.android.com/preview/features/runtime-permissions.html
 * <p/>
 * This class represents an strategy for asking users for permission as needed. With the idea being
 * that apps have greatest success when asking for permission as it is needed. The user sees the
 * feature in context and no further explanation is required.
 */
@TargetApi(Build.VERSION_CODES.M)
@Singleton public class PermissionHelper
{
    public interface PermissionListener
    {
        void permissionRequestResult(String permission, boolean hasPermission);
    }

    //flags used to define which permission is being checked on
    public static final int CONTACTS_PERMISSION = 30;
    public static final int CAMERA_PERMISSION = 31;
    public static final int STORAGE_PERMISSION = 32;
    public static final int LOCATION_PERMISSION = 33;
    public static final int PHONE_PERMISSION = 34;
    public static final int CALENDAR_PERMISSION = 35;
    public static final int MICROPHONE_PERMISSION = 36;
    public static final int SENSORS_PERMISSION = 37;
    public static final int SMS_PERMISSION = 38;

    private PermissionListener permissionListener;

    @Inject PermissionHelper() {}

    /**
     * Always call this before attempting to use a feature that requires permission. When the
     * asynchronous call completes, the results will be delivered to onRequestPermissionsResult() in
     * <code>ABaseActivity</code>, which will call the method corresponding to that name in this
     * class.
     *
     * @param activity           onRequestPermissionsResult() will be called in the Activity context
     * @param permission         a permission defined in android.Manifest.class
     * @param permissionListener the place to deliver requested results
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission(@NonNull Activity activity,
                                  @NonNull String permission,
                                  @NonNull PermissionListener permissionListener)
    {
        this.permissionListener = permissionListener;
        boolean hasPermission = hasPermission(activity, permission);

        if (hasPermission) {
            permissionListener.permissionRequestResult(permission, true);
        } else {
            switch (permission) {
                case Manifest.permission.READ_CALENDAR:
                case Manifest.permission.WRITE_CALENDAR:
                    activity.requestPermissions(new String[]{permission}, CALENDAR_PERMISSION);
                    break;
                case Manifest.permission.CAMERA:
                    activity.requestPermissions(new String[]{permission}, CAMERA_PERMISSION);
                    break;
                case Manifest.permission.READ_CONTACTS:
                case Manifest.permission.WRITE_CONTACTS:
                case Manifest.permission.GET_ACCOUNTS:
                    activity.requestPermissions(new String[]{permission}, CONTACTS_PERMISSION);
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                    activity.requestPermissions(new String[]{permission}, LOCATION_PERMISSION);
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    activity.requestPermissions(new String[]{permission}, MICROPHONE_PERMISSION);
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                case Manifest.permission.CALL_PHONE:
                case Manifest.permission.READ_CALL_LOG:
                case Manifest.permission.WRITE_CALL_LOG:
                case Manifest.permission.ADD_VOICEMAIL:
                case Manifest.permission.USE_SIP:
                case Manifest.permission.PROCESS_OUTGOING_CALLS:
                    activity.requestPermissions(new String[]{permission}, PHONE_PERMISSION);
                    break;
                case Manifest.permission.BODY_SENSORS:
                    activity.requestPermissions(new String[]{permission}, SENSORS_PERMISSION);
                    break;
                case Manifest.permission.SEND_SMS:
                case Manifest.permission.RECEIVE_SMS:
                case Manifest.permission.READ_SMS:
                case Manifest.permission.RECEIVE_WAP_PUSH:
                case Manifest.permission.RECEIVE_MMS:
                    activity.requestPermissions(new String[]{permission}, SMS_PERMISSION);
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    activity.requestPermissions(new String[]{permission}, STORAGE_PERMISSION);
                    break;
            }
        }
    }

    public boolean hasPermission(@NonNull Activity activity, @NonNull String permission)
    {
        return checkPermission(activity, permission);
    }

    /**
     * <strong>Note:</strong> It is possible that the permissions request interaction with the user
     * is interrupted. In this case you will receive empty permissions and results arrays which
     * should be treated as a cancellation.
     *
     * @param permissions  The requested permissions.
     * @param grantResults If the permission was approved
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (permissions.length != 0 && permissionListener != null) {
            permissionListener.permissionRequestResult(permissions[0],
                    grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * @param activity The requesting BaseActivity context
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission(Activity activity, String permission)
    {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}
