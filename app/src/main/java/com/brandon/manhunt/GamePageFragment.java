package com.brandon.manhunt;

import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private MediaPlayer alert;
    private static GamePageFragment mGamePageFragment;
    private TextView mDisplayField, mHuntersLocationField, mGameOverDisplay;
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private Button mButton;

    public static GamePageFragment getInstance() {
        if (mGamePageFragment == null) {
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
                Intent i = new Intent(getActivity(), MainPage.class);
                startActivity(i);
            }
        });


        if (savedInstanceState != null) {
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


    public void getInformation(String s) {
        mDisplayField.setText(s);
    }

    public void receiveHuntedLocation(Location huntedLocation, Location hunterLocation){

        double distance = hunterLocation.distanceTo(huntedLocation); // meter

        MediaPlayer farPlayer = MediaPlayer.create(getContext(), R.raw.far_hunter);
        MediaPlayer closePlayer = MediaPlayer.create(getContext(), R.raw.close_hunter);
        MediaPlayer closestPlayer = MediaPlayer.create(getContext(), R.raw.closest_hunter);
        closePlayer.start();
        closestPlayer.start();
        farPlayer.start();
        closePlayer.pause();
        closestPlayer.pause();
        farPlayer.pause();

//distances = > 20.00, 20.00 < 8.00, 8.00 < 3.00
        if (distance > 10.00 && !farPlayer.isPlaying()) {
            if (closePlayer.isPlaying()) {
                closePlayer.pause();
            }
            farPlayer.start();
            farPlayer.setLooping(true);
        } else if (distance <= 10.00 && distance > 5.00 && !closePlayer.isPlaying()) {
            if (farPlayer.isPlaying()) {
                farPlayer.pause();
            } else if (closestPlayer.isPlaying()) {
                closestPlayer.pause();
            }
            closePlayer.start();
            closePlayer.setLooping(true);
        } else if (distance <= 5.00 && distance >= 2.00 && !closestPlayer.isPlaying()) {
            if(closePlayer.isPlaying()) {
                closePlayer.pause();
            }
            closestPlayer.start();
            closestPlayer.setLooping(true);
        } else if (distance < 1.00 && closePlayer.isPlaying()) {
            closestPlayer.stop();
            closePlayer.reset();
            closePlayer.release();
            closestPlayer.reset();
            closestPlayer.release();
            farPlayer.reset();
            farPlayer.release();
            mHuntersLocationField.setText("Do you see them yet?");
        }

    }

    public void receiveHuntersLocations(List<Location> huntersLocations, Location huntedLocation){
        if (huntersLocations.size() > 0) {

            double shortestDistance = getShortestDistance(huntersLocations, huntedLocation);

            /*MediaPlayer huntedPlayer = MediaPlayer.create(getContext(), R.raw.close_hunted);
            huntedPlayer.start();
            huntedPlayer.pause();

            if(shortestDistance < 10.00 && !huntedPlayer.isPlaying())
            {
               /* huntedPlayer.start();
                huntedPlayer.setLooping(true);
            }*/

            if (shortestDistance < 1.00 ){
               /* huntedPlayer.stop();
                huntedPlayer.release();*/
                mDisplayField.setText("You have been caught!");
                mReference.child("GAMEOVER").setValue(true);
            }

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
        return shortestDistance;
    }


    public void setSecondDisplay(String s){
        mHuntersLocationField.append(s);
    }

}
