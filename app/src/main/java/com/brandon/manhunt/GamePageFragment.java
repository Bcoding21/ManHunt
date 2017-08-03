package com.brandon.manhunt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

    private TextView mDipslayField, mClosestHunters;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private static final String TAG = "FRAGMENT_GAME_PAGE";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_page, container, false);

        if (savedInstanceState == null ) {
            //Textview
            mDipslayField = v.findViewById(R.id.display_info);
            mClosestHunters = v.findViewById(R.id.hunters_list);


            //Firebase
            mAuth = FirebaseAuth.getInstance();
            mRef = FirebaseDatabase.getInstance().getReference();

            addUser();
            displayInfo();
            return v;
        }
        else{
            String display = savedInstanceState.getString("display");
            mDipslayField.setText(display);
            return v;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String display = mDipslayField.getText().toString();
        outState.putString("display", display);
    }

    private void addUser() {
        final String user_email = mAuth.getCurrentUser().getEmail();

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("Hunted")) {
                    mRef.child("Hunted").child(user_email.replace("@", "at").replace(".", "dot")).setValue("LOCATION");
                } else if (dataSnapshot.hasChild("Hunted")) {
                    mRef.child("Hunters").child(user_email.replace("@", "at").replace(".", "dot")).setValue("LOCATOIN");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void displayInfo() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Hunted")) {

                    Iterable<DataSnapshot> children = dataSnapshot.child("Hunted").getChildren();
                    String hunted_player = children.iterator().next().getKey();
                    mDipslayField.append(hunted_player);

                } else if (!dataSnapshot.hasChild("Hunted")) {
                    mDipslayField.setText("YOU ARE BEING HUNTED!!");
                    getClosestHunters();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getClosestHunters(){
        DatabaseReference dbz = FirebaseDatabase.getInstance().getReference();

        dbz.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numHunters = 0;
                String closest_hunters = "";

                Iterable<DataSnapshot> snap = dataSnapshot.child("Hunters").getChildren();
                Iterator<DataSnapshot> childs = snap.iterator();

                while (childs.hasNext() && numHunters < 5){
                    closest_hunters += childs.next().getKey() + " is 10 meters away" + "\n";
                }
                mClosestHunters.setText("");
                mClosestHunters.append(closest_hunters);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void ListenForLocationChanges(){

        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference();


    }

}