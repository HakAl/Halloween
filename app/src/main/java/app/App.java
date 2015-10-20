package app;

import android.app.Application;

import di.components.AppComponent;
import di.components.DaggerAppComponent;
import di.modules.AppModule;

public class App extends Application
{
    private AppComponent appComponent;

    @Override public void onCreate()
    {
        super.onCreate();
        this.appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent()
    {
        return appComponent;
    }
}
