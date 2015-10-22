package camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;

import util.DeviceUtils;
import util.FileStorage;
import util.Logger;

/**
 * Manifest.permission_group.STORAGE
 *
 * Management of Permissions is strongly tied to the life-cycle of an Activity.
 * It is the responsibility of the view to handle acquiring Camera permission.
 *
 *
 */
public class CameraPreviewRecorder implements CameraPreviewListener
{
    public static final String TAG = CameraPreviewRecorder.class.getSimpleName();
    public static final int DEFAULT_MAX_DURATION = 300000; //5 minutes

    private Camera mCamera;
    public SurfaceHolder viewHolder;
    private MediaRecorder mediaRecorder;
    private File mediaFile;
    private boolean inView;

    @Override public void onCreate(@NonNull SurfaceView surfaceView)
    {
        inView = false;

        viewHolder = surfaceView.getHolder();
        viewHolder.addCallback(this);
        viewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Call in Activity onResume() life-cycle method.
     */
    @Override public void onResume()
    {
        this.mCamera = attemptSetupCamera();
    }

    /**
     * Call in Activity onPause() life-cycle method.
     */
    @Override public void onPause()
    {
        releaseMediaRecorder();
        if (mCamera != null) {
            if (inView) {
                mCamera.stopPreview();
                inView = false;
            }
        }
        releaseCamera();
    }

    /**
     * Call in Activity onDestroy() life-cycle method.
     */
    @Override public void onDestroy()
    {
        releaseMediaRecorder();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override public boolean stop()
    {
        releaseMediaRecorder();
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                return true;
            } catch (RuntimeException stopException) {
                if (mediaFile != null && mediaFile.exists()) {
                    mediaFile.delete();
                }
                return false;
            }
        } else {
            // TODO: 10/18/15
            return false;
        }
    }

//    @Override public boolean record(String outputFile)
    @Override public boolean record(File outputFile)
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
                releaseMediaRecorder();
                return false;
            } catch (IOException | RuntimeException e) {
                Logger.exception("MediaRecorder\n" + e.getMessage());
                releaseMediaRecorder();
                return false;
            }
        } else {
            releaseMediaRecorder();
            return false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        if (mCamera == null) {
            mCamera = attemptSetupCamera();
        }

        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            if (params != null) {

                Camera.Size size = getBestPreviewSize(w, h, params);

                if (size != null) {

                    params.setPreviewSize(size.width, size.height);
                    mCamera.setParameters(params);
                    mCamera.startPreview();
                    inView = true;

                } else {
                    // TODO: 10/18/15
                }
            } else {
                // TODO: 10/18/15
            }

            try {
                mCamera.setPreviewDisplay(viewHolder);
//          Make her portrait.
                mCamera.setDisplayOrientation(90);
            } catch (Throwable t) {
                Logger.exception("Camera not loaded. Exception in setViewDisplay()");
                // TODO: 10/18/15
            }
        } else {
            // TODO: 10/18/15
        }
    }

    @Override public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Use Android life-cycle methods
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

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    File getMovieFile(String s)
    {
        File file = DeviceUtils.isExternalStorageWritable()
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                : Environment.getDataDirectory();

        if (file != null) {
            file = new FileStorage.Builder()
                    .directoryPath(file.getPath())
                    .fileName(s)
                    .createFile();
        }

        return file;
    }

    private String getDirectory()
    {
        return DeviceUtils.isExternalStorageWritable()
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath()
                : Environment.DIRECTORY_MOVIES;
    }

    private MediaRecorder getMediaRecorder()
    {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setMaxDuration(DEFAULT_MAX_DURATION);
            mediaRecorder.setOnInfoListener(getMaxDurationListener());
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
//		Tag video with a 90 degree angle to tell VideoView how to display it
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

        if (mCamera != null) {
            mCamera.lock();
        }
    }

    /** Release the camera for other applications. */
    private void releaseCamera()
    {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
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

    // TODO: 10/18/15
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
                            if (mediaFile != null && mediaFile.exists() && mediaFile.delete()) {
                                Logger.debugLog("An error occurred stopping MediaRecorder. The corrupted file was deleted.");
                            } else {
                                Logger.debugLog("An error occurred stopping MediaRecorder. The corrupted file was not deleted.");
                            }
                        }
                    } else {
                        Logger.debugLog("MediaRecorder was null.");
                    }
                    releaseMediaRecorder();
                }
            }
        };
    }
}
