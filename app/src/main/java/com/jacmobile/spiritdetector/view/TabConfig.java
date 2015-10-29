package com.jacmobile.spiritdetector.view;

import com.jacmobile.spiritdetector.R;

import java.util.ArrayList;
import java.util.List;

public class TabConfig
{
    public static List<Tab> getTabs()
    {
        List<Tab> tabs = new ArrayList<>();
        tabs.add(new Tab(R.string.tab_camera));
        tabs.add(new Tab(R.string.tab_files));
        tabs.add(new Tab(R.string.tab_media));
        return tabs;
    }
}
