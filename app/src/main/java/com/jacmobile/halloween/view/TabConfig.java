package com.jacmobile.halloween.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.jacmobile.halloween.R;

import java.util.ArrayList;
import java.util.List;

public class TabConfig
{
    private final static int[] TITLES = {R.string.tab_camera, R.string.tab_files, R.string.tab_media};
    private final static int[] IMAGES = {R.drawable.ic_av_videocam, R.drawable.ic_file_folder,
            R.drawable.ic_action_theaters};

    public static List<Tab> getTabs(Context context)
    {
        List<Tab> tabs = new ArrayList<>();
        for (int i = 0; i < TITLES.length; i++) {
            tabs.add(new Tab(TITLES[i], context.getString(TITLES[i]),
                            ContextCompat.getDrawable(context, IMAGES[i])));
        }
        return tabs;
    }
}
