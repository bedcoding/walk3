package com.example.ggavi.registeration.ahn3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by com on 2016-10-14.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs //
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                open2_TabFragment1 TabFragment1 = new open2_TabFragment1();
                return TabFragment1;
            case 1:
                open2_TabFragment2 TabFragment2 = new open2_TabFragment2();
                return TabFragment2;
            case 2:
                open2_TabFragment3 TabFragment3 = new open2_TabFragment3();
                return TabFragment3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
