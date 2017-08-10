package com.brandon.manhunt;


import android.location.Location;
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

import java.util.List;


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

    public void recieveLocation(Location location){
        mHuntersLocationField.setText("LAT: " + location.getLatitude() + " \n LONG: " + location.getLongitude() + "\n");
    }

    public void getHuntersInformation(List<Location> location, double currentLat, double currentLong){
        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLong);

        double smallestDistance = location.get(0).distanceTo(currentLocation);

        for (int i = 0; i < location.size(); i++){
            smallestDistance = location.get(i).distanceTo(currentLocation);
        }

        mHuntersLocationField.setText("The closest hunter is " + smallestDistance + "meters away");
    }

}
