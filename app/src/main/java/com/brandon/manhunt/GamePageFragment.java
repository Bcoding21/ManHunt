package com.brandon.manhunt;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private static GamePageFragment mGamePageFragment;
    private TextView mDisplayField, mHuntersLocationField, mGameOverDisplay;
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

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

        public void recieveHuntedLocation(Location huntedLocation, double lattitude, double longtidue){

        Location myLocation = new Location("");
        myLocation.setLatitude(lattitude);
        myLocation.setLongitude(longtidue);

        double distance = myLocation.distanceTo(huntedLocation);

        if (distance < 3.00){
            mHuntersLocationField.setText("YOU WOULD HAVE CAUGHT HIM!");
            mReference.child("GAMEOVER").setValue(true);
        }
    }

    public void receiveHuntersInformation(List<Location> location, double currentLat, double currentLong){
        if (location.size() > 0) {
        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLong);

            double smallestDistance = location.get(0).distanceTo(currentLocation);

            for (int i = 0; i < location.size(); i++) {
                double testDistance = smallestDistance = location.get(i).distanceTo(currentLocation);
                if (smallestDistance > testDistance) {
                    smallestDistance = testDistance;
                }
            }


            if (smallestDistance < 3.00) {
                mHuntersLocationField.setText("YOU WOULD HAVE BEEN CAUGHT!");
                mReference.child("GAMEOVER").setValue(true);
            }
        }
    }

    public void setGameOver(final GoogleApiClient client, final LocationListener listener){
        mReference.child("Hunted").setValue(null);
        mReference.child("Hunters").setValue(null);
        mReference.child("GAMEOVER").setValue(false);

        if (client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, listener);
            client.disconnect();
        }

        new CountDownTimer(10000, 1000){

            @Override
            public void onTick(long l) {
                mGameOverDisplay.setText("GAME OVER. LEAVING IN " + l / 1000 + " seconds");
            }

            @Override
            public void onFinish() {

                Intent myIntent = new Intent(getActivity(), MainPage.class);
                startActivity(myIntent);

            }
        }.start();
    }

    public void setSecondDisplay(String s){
        mHuntersLocationField.setText(s);
    }

}
