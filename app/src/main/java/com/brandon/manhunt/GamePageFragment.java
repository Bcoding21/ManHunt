package com.brandon.manhunt;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by brandoncole on 8/1/17.
 */

public class GamePageFragment extends Fragment {

    private TextView mDisplayField, mClosestHunters;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mCurrentUserEmail, mCurrentUserName;


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
            displayInfo(); // display if user is hunted/hunter to screen
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

                    User newUser = new User(mCurrentUserName, 0.0, 0.0);
                    mReference.child("Hunted").child(mCurrentUserEmail).setValue(newUser);

                } else if (dataSnapshot.exists()) {

                    User newUser = new User(mCurrentUserName, 0.0, 0.0);
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(newUser);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void displayInfo() {

        DatabaseReference query = mReference.child("Hunted");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    mDisplayField.setText("You are being hunted!");
                }
                else{
                    String huntedName = null;

                    for (DataSnapshot data : dataSnapshot.getChildren()){ // only one child
                        huntedName = data.child("displayName").getValue().toString();
                    }

                    mDisplayField.append(huntedName);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






}