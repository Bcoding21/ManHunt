package com.brandon.manhunt;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;


/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private static GamePageFragment mGamePageFragment;
    private TextView mDisplayField, mHuntersLocationField, mGameOverDisplay;
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private Button mButton;

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
        mGameOverDisplay = v.findViewById(R.id.game_over_display);

        mButton = v.findViewById(R.id.return_to_mm);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GamePageFragment.class);
                startActivity(i);
            }
        });


       if (savedInstanceState != null){
            String display = savedInstanceState.getString("display");
            String display2 = savedInstanceState.getString("display2");
            mDisplayField.setText(display);
            mHuntersLocationField.setText(display2);
        }
        return v;
    }

    public void displayExitButton(){
        mButton.setVisibility(View.VISIBLE);
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

    public void receiveHuntedLocation(Location huntedLocation, Location hunterLocation){

        double distanceFromHunted = hunterLocation.distanceTo(huntedLocation); // meter

    }

    public void receiveHuntersLocations(List<Location> huntersLocations, Location huntedLocation){
        if (huntersLocations.size() > 0) {
            double shortestDistanceFromHunted = getShortestDistance(huntersLocations, huntedLocation);



        }
    }

    private double getShortestDistance(List<Location> huntersLocations, Location huntedLocation){
        double shortestDistance = huntersLocations.get(0).distanceTo(huntedLocation);

        for (int i = 0; i < huntersLocations.size(); i++) {
            double someDistance = huntersLocations.get(0).distanceTo(huntedLocation);
            if (someDistance < shortestDistance) {
                shortestDistance = someDistance;
            }
        }
        return  shortestDistance;
    }



    public void setSecondDisplay(String s){
        mHuntersLocationField.append(s);
    }

}
