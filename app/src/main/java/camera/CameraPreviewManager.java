package camera;

import android.view.SurfaceView;

public interface CameraPreviewManager
{
    void onCreate(SurfaceView surfaceView);
    void onResume();
    void onPause();
    void onDestroy();
    boolean record(String outputFile);
    boolean stop();
}
