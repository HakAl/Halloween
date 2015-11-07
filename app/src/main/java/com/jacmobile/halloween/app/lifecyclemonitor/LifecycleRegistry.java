package com.jacmobile.halloween.app.lifecyclemonitor;

import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnPauseCallback;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnResumeCallback;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnStartCallback;
import com.jacmobile.halloween.app.lifecyclemonitor.observers.OnStopCallback;

public interface LifecycleRegistry extends Subject
{
    void register(OnStartCallback observer);
    void register(OnStopCallback observer);
    void register(OnPauseCallback observer);
    void register(OnResumeCallback observer);
}
