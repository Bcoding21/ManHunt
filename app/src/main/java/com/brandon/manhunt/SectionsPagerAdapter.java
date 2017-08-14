package com.brandon.manhunt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandoncole on 8/1/17.
 */

public class SectionsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private final byte NUMBER_OF_TABS = 3;


    public SectionsPagerAdapter(FragmentManager manager) {
        super(manager);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return GamePageFragment.getInstance();
            case 1:
                return MapPageFragment.getInstance();
            case 2:
                return QuitPageFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "INFORMATION";
            case 1:
                return "MAP";
            case 2:
                return "EXIT HUNT";
        }
        return null;
    }



}

