package com.brandon.manhunt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandoncole on 8/1/17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private final List<Fragment> mFragList = new ArrayList<>();
    private final List<String> mFragTitleList = new ArrayList<>();

    public void addFrag(Fragment f, String title){
        mFragList.add(f);
        mFragTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragList.get(position);
    }

    @Override
    public int getCount() {
        return  mFragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragTitleList.get(position);

    }
}

