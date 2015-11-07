package com.jacmobile.halloween.app.lifecyclemonitor;

import com.jacmobile.halloween.app.lifecyclemonitor.observers.Observer;

public interface Subject
{
    public void unregister(Observer obj);

    //method to notify observers of change
    public void notifyObservers();

    //method to get updates from subject
    public Object getUpdate(Observer obj);
}