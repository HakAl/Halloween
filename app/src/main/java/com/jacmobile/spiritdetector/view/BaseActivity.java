package com.jacmobile.spiritdetector.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jacmobile.spiritdetector.R;

import app.App;
import di.components.AppComponent;
import util.Logger;
import view.MultiStateView;

public class BaseActivity extends AppCompatActivity
{
    private MultiStateView contentView;

    public void log(String toLog)
    {
        Logger.debugLog(toLog);
    }

    public AppComponent getAppComponent()
    {
        return ((App) getApplication()).getAppComponent();
    }

    public void setViewContents(int resId)
    {
        contentView.setViewForState(resId, ContentViewState.CAMERA_PREVIEW);
    }

//  CAMERA_PREVIEW, CAMERA_ERROR, EMPTY, LOADING

    public void setContentViewState(@ContentViewState.ViewState int contentViewState)
    {
        contentView.setViewState(contentViewState);
    }

////Android life-cycle methods

    @Override public void onCreate(Bundle savedInstanceState)
    {
        getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view);
        this.contentView = (MultiStateView) findViewById(R.id.content_view);
    }

    @Override protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
//        contentView.getViewState()

    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
//        setContentViewState();
    }
}