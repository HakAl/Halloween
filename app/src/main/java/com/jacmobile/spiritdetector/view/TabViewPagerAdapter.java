package com.jacmobile.spiritdetector.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TabViewPagerAdapter extends FragmentPagerAdapter
{
    private List<Tab> tabs;

    public TabViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
        this.tabs = TabConfig.getTabs();
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override public Fragment getItem(int position)
    {
        return tabs.get(position).createFragment();
    }

    /**
     * Return the number of views available.
     */
    @Override public int getCount()
    {
        return tabs.size();
    }
}
