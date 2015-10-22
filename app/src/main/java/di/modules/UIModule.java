package di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import camera.CameraPreviewRecorder;

@Module public class UIModule
{
    @Provides @Singleton CameraPreviewRecorder cameraPreviewRecorder()
    {
        return new CameraPreviewRecorder();
    }
}
