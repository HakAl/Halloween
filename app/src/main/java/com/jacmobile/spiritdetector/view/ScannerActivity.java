package com.jacmobile.spiritdetector.view;

import android.Manifest;
import android.os.Build;
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
    @Inject CameraPreviewRecorder cameraPreviewRecorder;

    @Bind(R.id.camera_preview) SurfaceView cameraPreview;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getAppComponent().inject(this);

        if (savedInstanceState == null) {
            setViewContents(R.layout.activity_scanner);
            ButterKnife.bind(this);
            tryToStartCamera();
        } else {

        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 9) {
            startCameraPreview();
        } else {
            cameraFailedToStart();
        }
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

    private void tryToStartCamera()
    {
        if (DeviceUtils.hasCameraHardware(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionHelper.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && PermissionHelper.hasPermission(this, Manifest.permission.CAMERA)) {
                    startCameraPreview();
                } else {
                    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(perms, 9);
                }
            }
        } else {
            cameraFailedToStart();
        }
    }

    private void cameraFailedToStart()
    {
        //ask about camera?
        //switch to no camera view?
    }
}
