package com.jacmobile.halloween.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacmobile.halloween.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icicle;

public class TabFragment extends Fragment
{
    public static final String TAG = TabFragment.class.getCanonicalName();
    public static final String LAUNCH_INDEX = "li";

    @Bind(R.id.tabs_halloween) TabLayout tabLayout;
    @Bind(R.id.viewpager_halloween) ViewPager viewPager;

    @Icicle int launchIndex;

    /**
     * @param index the ViewPager index to launch to
     * @return
     */
    public static TabFragment newInstance(int index)
    {
        TabFragment fragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAUNCH_INDEX, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_fragment, container, false);
    }

    // Get the ViewPager and set it's PagerAdapter so that it can display items
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);



//
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
//        tabLayout.setupWithViewPager(viewpager);
//
//
//        TextView tab = (TextView) getLayoutInflater(savedInstanceState).inflate(R.layout.custom_tab, null);
//        tab.setText("Library");
//        tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tabbar_library, 0, 0);
//        tabLayout.getTabAt(0).setCustomView(tab);



        viewPager.setAdapter(new TabViewPagerAdapter(getChildFragmentManager(),
                        TabConfig.getTabs(getContext())));
        tabLayout.setupWithViewPager(viewPager);
        if (getArguments() != null) {
            launchIndex = getArguments().getInt(LAUNCH_INDEX);
            viewPager.setCurrentItem(launchIndex);
        }
    }
}
