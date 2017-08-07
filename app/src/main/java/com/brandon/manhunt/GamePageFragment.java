package com.brandon.manhunt;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private TextView mDisplayField, mClosestHunters;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mCurrentUserEmail, mCurrentUserName, mHuntedEmail;
    private final int ONE_MINUTE = 60000, THIRTY_SECONDS = 30000;
    public static final Handler handle = new Handler();
    public static Runnable r;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_page, container, false);

        //Textview
        mDisplayField = v.findViewById(R.id.display_info);
        mClosestHunters = v.findViewById(R.id.hunters_list);


        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // set email
        String unformattedEmail = mUser.getEmail();
        mCurrentUserEmail = unformattedEmail.replace("@", "at").replace(".", "dot");


        // set username
        Bundle b = getArguments();
        mCurrentUserName = b.getString("name");

        // First time game setup
        if (savedInstanceState == null){
            addUser();  // add user as hunted or hunter to firebase
        }
        else{
            String displayInfo = savedInstanceState.getString("display");
            mDisplayField.setText(displayInfo);
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String display = mDisplayField.getText().toString();
        outState.putString("display", display);
    }


    private void addUser() {

        DatabaseReference query = mReference.child("Hunted");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {

                    User newUser = new User(mCurrentUserName, 0.0, 0.0); // longtitude/lattitude
                    mReference.child("Hunted").child(mCurrentUserEmail).setValue(newUser);
                    mDisplayField.setText("You are being hunted");
                    sendHuntedLocation();


                } else if (dataSnapshot.exists()) {

                    User newUser = new User(mCurrentUserName, 0.0, 0.0);
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(newUser);
                    Map<String, Objects> myMap = (HashMap)dataSnapshot.getValue();
                    for (String key : myMap.keySet()){
                        mHuntedEmail= key;
                    }
                    receiveHuntedLocation();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void   receiveHuntedLocation(){
        DatabaseReference query = mReference.child("Hunted").child(mHuntedEmail);

        query.addValueEventListener(new ValueEventListener() {
            double lat = 0;
            double Longit = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lat = (double)dataSnapshot.child("lat").getValue();
                Longit = (double)dataSnapshot.child("long").getValue();

                mClosestHunters.setText("LONG: " + Longit + "\n LAT: " + lat);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendHuntedLocation() {

             final DatabaseReference query = mReference.child("Hunted");
             r = new Runnable() {
                 double lat = 0.1;
                double Long = 0.1;

                public void run() {
                    query.child(mCurrentUserEmail).child("lat").setValue(++lat);
                    query.child(mCurrentUserEmail).child("long").setValue(++Long);
                    handle.postDelayed(this, 250);
                }
            };
            handle.postDelayed(r, 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handle.removeCallbacks(r);
    }
}