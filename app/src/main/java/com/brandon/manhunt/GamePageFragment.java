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
import java.util.Set;

/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private TextView mDisplayField;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private String mUsername, mEmail;
    public static String mHuntedEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_page, container, false);

        //Textview
        mDisplayField = v.findViewById(R.id.display_info);

        //set username and email
        mUsername = User.getInstance().getDisplayName();
        mEmail = User.getInstance().getEmail();

        // set up Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // First time game setup
        if (savedInstanceState == null) {
            addUser();  // add user as hunted or hunter to firebase
        } else {
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


        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("Hunted")) {
                    User newUser = new User(mUsername, 0.0, 0.0); // longtitude/lattitude
                    mReference.child("Hunted").child(mEmail).setValue(newUser);
                    mDisplayField.setText("You are being hunted");
                    User.getInstance().setIsHunted(true);

                } else if (dataSnapshot.hasChild("Hunted")) {
                    User newUser = new User(mUsername, 0.0, 0.0);
                    mReference.child("Hunters").child(mEmail).setValue(newUser);
                    User.getInstance().setIsHunted(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }





}