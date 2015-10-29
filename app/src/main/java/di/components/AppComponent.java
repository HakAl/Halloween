package di.components;

import com.jacmobile.spiritdetector.view.BaseActivity;
import com.jacmobile.spiritdetector.view.CameraPreviewFragment;
import com.jacmobile.spiritdetector.view.MainActivity;

import javax.inject.Singleton;

import dagger.Component;
import di.modules.AppModule;
import di.modules.UIModule;

@Singleton
@Component(modules = {AppModule.class, UIModule.class})
public interface AppComponent
{
    void inject(BaseActivity activity);

    void inject(MainActivity activity);

    void inject(CameraPreviewFragment fragment);
}
