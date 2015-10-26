package view;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class MultiStateViewState
{
    public static final int CONTENT = 0;
    public static final int ERROR = 1;
    public static final int EMPTY = 2;
    public static final int LOADING = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONTENT, ERROR, EMPTY, LOADING})
    public @interface ViewState {}
}
