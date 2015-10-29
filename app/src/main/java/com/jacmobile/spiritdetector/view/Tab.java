package com.jacmobile.spiritdetector.view;

import android.support.v4.app.Fragment;

import com.jacmobile.spiritdetector.R;

public class Tab
{
    private final int titleResId;

    public Tab(int titleResId)
    {
        this.titleResId = titleResId;
    }

    public Fragment createFragment()
    {
        if (titleResId == R.string.tab_files) return FileStorageFragment.newInstance();
        if (titleResId == R.string.tab_media) return MediaViewerFragment.newInstance();
        return CameraPreviewFragment.newInstance();
    }

    public int getTitleResId()
    {
        return titleResId;
    }
}