package di.modules;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import app.App;
import util.ForApplication;

@Module public class AppModule
{
    private final App app;

    public AppModule(App app)
    {
        this.app = app;
    }

    @Provides @Singleton @ForApplication Context provideApplication()
    {
        return this.app;
    }
}
