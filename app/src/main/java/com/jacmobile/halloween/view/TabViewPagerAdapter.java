package com.jacmobile.halloween.view;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.List;

public class TabViewPagerAdapter extends FragmentPagerAdapter
{
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

//        tabs.get(position).icon.setBounds(0, 0, tabs.get(position).icon.getIntrinsicWidth(),
//                tabs.get(position).icon.getIntrinsicHeight());
//        SpannableString sb = new SpannableString(" ");
//        ImageSpan imageSpan = new ImageSpan(tabs.get(position).icon, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;


        return tabs.get(position).title;
    }
}
