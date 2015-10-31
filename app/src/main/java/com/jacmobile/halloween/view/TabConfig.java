package com.jacmobile.halloween.view;

import android.content.Context;

import com.jacmobile.halloween.R;

import java.util.ArrayList;
import java.util.List;

public class TabConfig
{
    private final static int[] TITLES = {R.string.tab_camera, R.string.tab_files, R.string.tab_media};

    public static List<Tab> getTabs(Context context)
    {
        List<Tab> tabs = new ArrayList<>();
        for (int resId : TITLES) {
            tabs.add(new Tab(resId, context.getString(resId)));
        }
        return tabs;
    }
}
