package com.brandon.manhunt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by brandoncole on 8/1/17.
 */

public class QuitPageFragment extends Fragment {

    private static QuitPageFragment mQuitPageFragment;

    public static QuitPageFragment getInstance(){
        if (mQuitPageFragment == null){
            mQuitPageFragment = new QuitPageFragment();
        }
        return mQuitPageFragment;
    }

    private static final String TAG = "FRAGMENT_QUIT_PAGE";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quit_page, container, false);




        return v;
    }
}
