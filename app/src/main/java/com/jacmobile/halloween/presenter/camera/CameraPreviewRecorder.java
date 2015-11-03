package com.jacmobile.halloween.presenter.camera;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;

import com.jacmobile.halloween.app.App;
import com.jacmobile.halloween.util.DeviceUtils;
import com.jacmobile.halloween.util.Logger;

/**
 * Manifest.permission_group.STORAGE
 * <p/>
 * Management of Permissions is strongly tied to the life-cycle of an Activity. It is the
 * responsibility of the view to handle acquiring Camera permission.
 */
public class CameraPreviewRecorder extends Service implements CameraPreviewService
{
    public static final String TAG = CameraPreviewRecorder.class.getSimpleName();

    public static final int DEFAULT_MAX_DURATION = 300000; //5 minutes

    private Camera mCamera;
    private SurfaceHolder viewHolder;
    private MediaRecorder mediaRecorder;
    private CameraFailureListener cameraFailureListener;

    @Override public void onDestroy()
    {
        this.cameraFailureListener = null;
        this.mCamera = null;
        this.mediaRecorder = null;
        this.cameraFailureListener = null;
        super.onDestroy();
    }

    @Override public void onCreate()
    {
        super.onCreate();
        ((App) getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    @Override public boolean stop()
    {
        this.viewHolder = null;
        if ((mCamera != null) && (mediaRecorder != null)) {
            stopMediaRecorder();
            releaseCamera();
            return true;
        } else {
            if (mediaRecorder != null) {
                stopMediaRecorder();
            } else {
                Logger.exception("MediaRecorder was null, unable to stop recording.");
            }
            if (mCamera != null) {
                releaseCamera();
            } else {
                Logger.exception("Camera was null, unable to release.");
            }
            return false;
        }
    }

    @Override public void startCameraPreview(@NonNull SurfaceView surfaceView, CameraFailureListener cameraFailureListener)
    {
        this.cameraFailureListener = cameraFailureListener;

        viewHolder = surfaceView.getHolder();
        viewHolder.addCallback(this);
        viewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        this.mCamera = attemptSetupCamera();
        if (mCamera == null) cameraFailureListener.onCameraSetupFailed();
        stop();
    }

    @Override public boolean startRecording(@NonNull File outputFile)
    {
        if (DeviceUtils.isExternalStorageWritable()) {
            mCamera.lock();
            mCamera.unlock();
            mediaRecorder = getMediaRecorder();
            mediaRecorder.setOutputFile(outputFile.getPath());
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                return true;
            } catch (IllegalStateException e) {
                Logger.exception("MediaRecorder\n" + e.getMessage());
                stop();
                return false;
            } catch (IOException | RuntimeException e) {
                Logger.exception("MediaRecorder\n" + e.getMessage());
                stop();
                return false;
            }
        } else {
            stop();
            return false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Camera.Parameters params = mCamera.getParameters();
        if (params != null) {
            Camera.Size size = getBestPreviewSize(w, h, params);

            if (size != null) {
                params.setPreviewSize(size.width, size.height);
                mCamera.setParameters(params);
                mCamera.startPreview();
            } else {
                cameraFailureListener.onCameraSetupFailed();
            }
        } else {
            cameraFailureListener.onCameraSetupFailed();
        }

        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(viewHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void releaseCamera()
    {
        mCamera.stopPreview();
        mCamera.lock();
        mCamera.release();
        mCamera = null;
    }

    private void stopMediaRecorder()
    {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private Camera.Size getBestPreviewSize(int w, int h, @NonNull Camera.Parameters p)
    {
        Camera.Size result = null;
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width <= w && size.height <= h) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) result = size;
                }
            }
        }

        return result;
    }

    private MediaRecorder getMediaRecorder()
    {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setMaxDuration(DEFAULT_MAX_DURATION);
            mediaRecorder.setOnInfoListener(getMaxDurationListener());
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setOrientationHint(90);
        }
        return mediaRecorder;
    }

    private Camera attemptSetupCamera()
    {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras > 0) {
            try {
                Camera camera = null;
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                if (camera == null) {
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    if (camera != null) {
                        return camera;
                    }  else {
                        return getCameraFailure("No Cameras detected.");
                    }
                } else {
                    return camera;
                }
            } catch (Exception e) {
                return getCameraFailure(e.toString());
            }
        } else {
            return getCameraFailure("No Cameras detected.");
        }
    }

    @Nullable private Camera getCameraFailure(String e)
    {
        // Camera is not available (in use or does not exist)
        Logger.exception(TAG + "\n" + e);
        return null;
    }

    private MediaRecorder.OnInfoListener getMaxDurationListener()
    {
        return new MediaRecorder.OnInfoListener()
        {
            @Override public void onInfo(MediaRecorder mr, int what, int extra)
            {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    if (mediaRecorder != null) {
                        try {
                            mediaRecorder.stop();
                        } catch (RuntimeException stopException) {
                            Logger.debugLog(stopException.toString());
                        }
                    } else {
                        Logger.debugLog("MediaRecorder was null.");
                    }
                    stop();
                }
            }
        };
    }

////unused

    @Override public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return null;
    }
}
