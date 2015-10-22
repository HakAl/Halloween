package camera;

import android.view.SurfaceView;

import java.io.File;

public interface CameraPreviewManager
{
    void onCreate(SurfaceView surfaceView);
    void onResume();
    void onPause();
    void onDestroy();
    boolean record(File outputFile);
    boolean stop();
}
