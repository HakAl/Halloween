package com.jacmobile.halloween.presenter.camera;

import android.app.Service;
import android.content.Context;
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

    public static final int DEFAULT_MAX_DURATION = 60000;

    private Camera camera;
    private SurfaceHolder cameraSurface;
    private MediaRecorder mediaRecorder;
    private CameraFailureListener cameraFailureListener;

////Static launcher methods

    public static void start(Context context)
    {
        context.startService(new Intent(context, CameraPreviewRecorder.class));
    }

    public static void stop(Context context)
    {
        context.stopService(new Intent(context, CameraPreviewRecorder.class));
    }

    /**
     * Shows a Camera preview in the supplied SurfaceView. First attempts to use the device's back
     * camera and if it's not present, tries the front.
     *
     * @param surfaceView           the SurfaceView to show the camera preview on
     * @param cameraFailureListener a callback to report errors to
     */
    @Override public void startCameraPreview(@NonNull SurfaceView surfaceView, CameraFailureListener cameraFailureListener)
    {
        cameraSurface = surfaceView.getHolder();
        cameraSurface.addCallback(this);
        cameraSurface.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        this.camera = attemptSetupCamera();
        if (camera == null) {
            cameraFailureListener.onCameraSetupFailed();
        }
        this.cameraFailureListener = cameraFailureListener;
        camera.setDisplayOrientation(90);
    }

    /**
     * Record the Camera preview and save it to a File. Stop recording by calling
     * CameraPreviewRecorder.pauseCameraPreview()
     *
     * @param outputFile the File to write the recording to.
     */
    @Override public void startRecording(File outputFile)
    {
        if (DeviceUtils.isExternalStorageWritable()) {
            mediaRecorder = getMediaRecorder(outputFile);
            if (mediaRecorder != null) {
                mediaRecorder.setPreviewDisplay(cameraSurface.getSurface());
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException | RuntimeException e) {
                    Logger.exception("MediaRecorder\n" + e.getMessage());
                    releaseMediaRecorder();
                    cameraFailureListener.onCameraSetupFailed();
                }
            } else {
                Logger.exception("Unable to setup MediaRecorder.");
                cameraFailureListener.onCameraSetupFailed();
            }
        } else {
            releaseMediaRecorder();
        }
    }

    /**
     * Pause the Camera preview from displaying of the SurfaceView
     */
    @Override public void pauseCameraPreview()
    {
        if (camera == null || mediaRecorder == null) {
            if (mediaRecorder != null) {
                releaseMediaRecorder();
            } else {
                Logger.exception("MediaRecorder was null, unable to pauseCameraPreview recording. Was recording started?");
            }
            if (camera != null) {
                camera.stopPreview();
                releaseCamera();
            } else {
                Logger.exception("Camera was null, unable to release.");
            }
            this.cameraFailureListener = null;
        } else {
            releaseMediaRecorder();
            releaseCamera();
            this.cameraFailureListener = null;
        }
    }

    /**
     * Stop recording, only call after recording started.
     */
    @Override public void stopRecording()
    {
        if (mediaRecorder != null) {
            releaseMediaRecorder();
        } else {
            Logger.exception("MediaRecorder was null, unable to pauseCameraPreview recording. Was recording started?");
        }
    }

    @Nullable private Camera attemptSetupCamera()
    {
        return Camera.getNumberOfCameras() > 0 ? getBackOrFrontCamera() : null;
    }

    @Nullable private Camera getBackOrFrontCamera()
    {
        Camera camera = openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        return camera == null ? openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT) : camera;
    }

    @Nullable private Camera openCamera(int which)
    {
        return Camera.open(which);
    }

    private Camera.Size getBestPreviewSize(int w, int h, Camera.Parameters p)
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
        return (result);
    }

    @Nullable private MediaRecorder getMediaRecorder(File outputFile)
    {
        if (camera != null) {
            camera.lock();
            camera.unlock();
        } else {
            camera = attemptSetupCamera();
        }
        if (camera == null) {
            cameraFailureListener.onCameraSetupFailed();
            stopSelf();
            return null;
        }

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if (outputFile.exists()) outputFile.delete();
            mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setMaxDuration(DEFAULT_MAX_DURATION);
            mediaRecorder.setOnInfoListener(getMaxDurationListener());
            mediaRecorder.setOrientationHint(90);
        }
        return mediaRecorder;
    }


    private void releaseMediaRecorder()
    {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (camera != null) {
            camera.lock();
        }
    }

    private void releaseCamera()
    {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
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
                            releaseMediaRecorder();
                        } catch (RuntimeException stopException) {
                            Logger.debugLog(stopException.toString());
                        }
                    } else {
                        Logger.debugLog("MediaRecorder was null.");
                    }
                }
            }
        };
    }

////Service

    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    @Override public void onCreate()
    {
        super.onCreate();
        ((App) getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return null;
    }

////SurfaceHolder.Callback

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Logger.exception("surfaceChanged() called");
        if (camera == null) {
            camera = attemptSetupCamera();
        }

        if (camera != null) {

            Camera.Parameters params = camera.getParameters();

            if (params != null) {
                Camera.Size size = getBestPreviewSize(w, h, params);
                if (size != null) {
                    params.setPreviewSize(size.width, size.height);
                    camera.setParameters(params);
                    camera.startPreview();
                } else {
                    cameraFailureListener.onCameraSetupFailed();
                    Logger.exception("Unable to start Camera preview. Exception in onSurfaceChanged()");
                }
            } else {
                cameraFailureListener.onCameraSetupFailed();
                Logger.exception("Unable to start Camera preview. Exception in onSurfaceChanged()");
            }

            try {
                camera.setPreviewDisplay(cameraSurface);
            } catch (Throwable t) {
                cameraFailureListener.onCameraSetupFailed();
                Logger.exception("Unable to start Camera preview. Exception in onSurfaceChanged()");
            }

        } else {
            cameraFailureListener.onCameraSetupFailed();
            Logger.exception("Unable to start Camera preview. Exception in onSurfaceChanged()");
        }
    }

    @Override public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder)
    {
    }
}
