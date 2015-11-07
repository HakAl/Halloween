package com.jacmobile.halloween.app.lifecyclemonitor.observers;

import com.jacmobile.halloween.app.lifecyclemonitor.Subject;

public interface Observer
{
    //update the observer, used by subject
    void update();

    //attach with subject to observe
    void setSubject(Subject sub);
}
