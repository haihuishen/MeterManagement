package com.zh.metermanagement.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jpeng on 16-11-14.
 */
public class TabAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titleList;

    public TabAdapter(FragmentManager fm, List<Fragment> fl, List<String> tl) {
        super(fm);
        this.fragmentList = fl;
        this.titleList = tl;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
