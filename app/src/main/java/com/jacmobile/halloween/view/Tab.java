package com.jacmobile.halloween.view;

import android.support.v4.app.Fragment;

import com.jacmobile.halloween.R;

public class Tab
{
    public final String title;
    public final int titleResId;

    public Tab(int titleResId, String title)
    {
        this.titleResId = titleResId;
        this.title = title;
    }

    public Fragment createFragment()
    {
        if (titleResId == R.string.tab_files) return FileStorageFragment.newInstance();
        if (titleResId == R.string.tab_media) return MediaViewerFragment.newInstance();
        return CameraPreviewFragment.newInstance();
    }
}