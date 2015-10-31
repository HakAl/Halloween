package com.jacmobile.halloween.view;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import com.jacmobile.halloween.R;

import butterknife.ButterKnife;
import icepick.Icicle;
import com.jacmobile.halloween.util.DeviceUtils;
import com.jacmobile.halloween.util.PermissionHelper;

/**
 * tabs --> camera preview / file storage / media viewer
 */
public class MainActivity extends BaseActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int CAMERA_REQUEST_CODE = 9;

    @Icicle public boolean isCameraAvailable = false;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getAppComponent().inject(this);

        if (savedInstanceState == null) {

            setBaseContentView(R.layout.activity_main);
            attemptStartCamera();

        } else {
            if (isCameraAvailable) {
                startApplication();
            } else {
                attemptStartCamera();
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            this.isCameraAvailable = PermissionHelper.hasPermission(this, permissions);
            if (isCameraAvailable) {
                startApplication();
            }
        } else {
            //no camera permission
            log("no camera permission");
        }
    }

    private void cameraFailedToStart()
    {
        log("cameraFailedToStart()");
        isCameraAvailable = false;
        //switch to no camera view
    }

    private void startApplication()
    {
        ButterKnife.bind(this);
        isCameraAvailable = true;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TabFragment.newInstance(0))
                .commitAllowingStateLoss();
    }

    private void attemptStartCamera()
    {
        if (DeviceUtils.hasCameraHardware(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionHelper.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && PermissionHelper.hasPermission(this, Manifest.permission.CAMERA)) {
                    startApplication();
                } else {
                    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(perms, 9);
                }
            }
        } else {
            cameraFailedToStart();
        }
    }
}
