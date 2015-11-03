package com.jacmobile.halloween.view;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.jacmobile.halloween.R;

public class Tab
{
    public final int titleResId;
    public final String title;
    public final Drawable icon;

    public Tab(int titleResId, String title, Drawable icon)
    {
        this.titleResId = titleResId;
        this.title = title;
        this.icon = icon;
    }

    public Fragment createFragment()
    {
        if (titleResId == R.string.tab_files) return FileStorageFragment.newInstance();
        if (titleResId == R.string.tab_media) return MediaViewerFragment.newInstance();
        return CameraPreviewFragment.newInstance();
    }
}