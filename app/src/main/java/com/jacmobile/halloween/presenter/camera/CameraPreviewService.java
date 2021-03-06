package com.jacmobile.halloween.presenter.camera;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;

public interface CameraPreviewService extends SurfaceHolder.Callback
{
    void startCameraPreview(@NonNull SurfaceView surfaceView, CameraFailureListener cameraFailureListener);
    void startRecording(@Nullable File outputFile);
    void stopRecording();
    void pauseCameraPreview();
}