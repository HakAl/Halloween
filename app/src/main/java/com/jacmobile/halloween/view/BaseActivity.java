package com.jacmobile.halloween.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jacmobile.halloween.R;

import com.jacmobile.halloween.app.App;
import com.jacmobile.halloween.di.components.AppComponent;
import icepick.Icepick;
import icepick.Icicle;
import com.jacmobile.halloween.util.Logger;

public abstract class BaseActivity extends AppCompatActivity
{
    public static final String TAG = BaseActivity.class.getSimpleName();

    @Icicle private @MultiStateViewState.ViewState int viewState;


    private MultiStateView contentView;

    /** @param toLog String to output if build is debug */
    public void log(String toLog)
    {
        Logger.debugLog(toLog);
    }

    /** Get the AppComponent to inject() into the object graph. */
    public AppComponent getAppComponent()
    {
        return ((App) getApplication()).getAppComponent();
    }


    public void setBaseContentView(int layoutResId)
    {
        this.viewState = MultiStateViewState.CONTENT;
        contentView.setViewForState(layoutResId, viewState);
    }

    /** CONTENT, ERROR, EMPTY, LOADING */
    private void setContentViewState(@MultiStateViewState.ViewState int contentViewState)
    {
        this.viewState = contentViewState;

        if (contentView != null) {
            contentView.setViewState(contentViewState);
        } else {
            // TODO: 10/25/15

        }
    }


    @Override public void onCreate(Bundle savedInstanceState)
    {
        getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view);
        this.contentView = (MultiStateView) findViewById(R.id.content_view);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}