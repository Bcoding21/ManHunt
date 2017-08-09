package com.brandon.manhunt;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private static GamePageFragment mGamePageFragment;
    private TextView mDisplayField, mHuntersLocationField;

    public static GamePageFragment getInstance(){
        if (mGamePageFragment == null){
            mGamePageFragment = new GamePageFragment();
        }
        return mGamePageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_page, container, false);

        //Textview
        mDisplayField = v.findViewById(R.id.display_info);
        mHuntersLocationField = v.findViewById(R.id.hunters_location);


       if (savedInstanceState != null){
            String display = savedInstanceState.getString("display");
            String display2 = savedInstanceState.getString("display2");
            mDisplayField.setText(display);
            mHuntersLocationField.setText(display2);
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String display = mDisplayField.getText().toString();
        String display2 = mHuntersLocationField.getText().toString();

        outState.putString("display2", display2);
        outState.putString("display", display);
    }


    public void getInformation(String s){
        mDisplayField.setText(s);
    }

    public void recieveLocation(double Lat, double Long){
        mHuntersLocationField.setText("LAT: " + Lat + " \n LONG: " + Long);
    }
}
