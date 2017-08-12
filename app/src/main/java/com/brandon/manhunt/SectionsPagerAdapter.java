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

    String[] tabNames = {"1", ",2", "3"};

    public SectionsPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return "One";


            case 1:
                return "Two";


            case 2:
                return "Three";
        }
        return null;
    }

}

