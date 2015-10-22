package com.jacmobile.spiritdetector.view;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class ContentViewState
{
    public static final int CAMERA_PREVIEW = 0;
    public static final int CAMERA_ERROR = 1;
    public static final int EMPTY = 2;
    public static final int LOADING = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CAMERA_PREVIEW, CAMERA_ERROR, EMPTY, LOADING})
    public @interface ViewState {}
}
