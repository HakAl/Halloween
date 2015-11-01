package com.jacmobile.halloween.di.components;

import com.jacmobile.halloween.di.modules.AppModule;
import com.jacmobile.halloween.presenter.sensors.MagnetometerService;
import com.jacmobile.halloween.view.BaseActivity;
import com.jacmobile.halloween.view.CameraPreviewFragment;
import com.jacmobile.halloween.view.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

import com.jacmobile.halloween.di.modules.UIModule;

@Singleton
@Component(modules = {AppModule.class, UIModule.class})
public interface AppComponent
{
    void inject(BaseActivity activity);

    void inject(MainActivity activity);

    void inject(CameraPreviewFragment fragment);

    void inject(MagnetometerService service);
}
