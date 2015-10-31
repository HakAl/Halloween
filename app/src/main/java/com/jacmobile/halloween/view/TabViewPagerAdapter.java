package com.jacmobile.halloween.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TabViewPagerAdapter extends FragmentPagerAdapter
{
    private String[] titles;
    private List<Tab> tabs;

    public TabViewPagerAdapter(FragmentManager fm, List<Tab> tabs)
    {
        super(fm);
        this.tabs = tabs;
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

    @Override public CharSequence getPageTitle(int position)
    {
        return tabs.get(position).title;
    }
}
