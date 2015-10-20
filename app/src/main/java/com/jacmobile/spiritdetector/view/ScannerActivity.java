package com.jacmobile.spiritdetector.view;

import android.Manifest;
import android.os.Bundle;
import android.view.SurfaceView;

import com.jacmobile.spiritdetector.R;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import camera.CameraPreviewRecorder;
import util.DeviceUtils;
import util.PermissionHelper;

public class ScannerActivity extends BaseActivity
{
    @Inject PermissionHelper permissionHelper;
    @Inject CameraPreviewRecorder cameraPreviewRecorder;

    @Bind(R.id.camera_preview) SurfaceView cameraPreview;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            setViewContents(R.layout.activity_scanner);

            ButterKnife.bind(this);

            tryToStartCamera();

        } else {

            
        }

    }

    private void tryToStartCamera()
    {
        if (DeviceUtils.hasCameraHardware(this)) {
            if (permissionHelper.hasPermission(this, Manifest.permission.CAMERA)) {
                startCameraPreview();
            } else {
                permissionHelper.requestPermission(this, Manifest.permission.CAMERA, new PermissionHelper.PermissionListener()
                {
                    @Override public void permissionRequestResult(String permission, boolean hasPermission)
                    {
                        if (hasPermission) {
                            startCameraPreview();
                        }
                    }
                });
            }
        } else {
            //ask about camera?
            //switch to no camera view?
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionHelper.onRequestPermissionsResult(permissions, grantResults);
    }

    @Override protected void onPause()
    {
        super.onPause();

        cameraPreviewRecorder.onPause();
    }

    @Override protected void onResume()
    {
        super.onResume();

        cameraPreviewRecorder.onResume();
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();

        cameraPreviewRecorder.onDestroy();
    }

    private void startCameraPreview()
    {
        cameraPreviewRecorder.onCreate(cameraPreview);
    }
}
