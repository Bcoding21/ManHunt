package com.brandon.manhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by brandoncole on 8/1/17.
 */

public class QuitPageFragment extends Fragment implements View.OnClickListener {

    private static QuitPageFragment mQuitPageFragment;

    public static QuitPageFragment getInstance(){
        if (mQuitPageFragment == null){
            mQuitPageFragment = new QuitPageFragment();
        }
        return mQuitPageFragment;
    }

    private DatabaseReference mRef;
    private String mHuntedEmail, mCurrentUserEmail;
    private GoogleApiClient mClient;
    private LocationListener mListener;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quit_page, container, false);

        v.findViewById(R.id.quit_button).setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();


        return v;
    }

    public void passInformation(String hunted_email, String currentUserName, LocationListener listener, GoogleApiClient client){
        mHuntedEmail = hunted_email;
        mCurrentUserEmail = currentUserName;
        mListener = listener;
        mClient = client;
    }

    @Override
    public void onClick(View view) {

        String a = mCurrentUserEmail;
        String b = mHuntedEmail;

        if (mCurrentUserEmail.equals(mHuntedEmail)){
            mRef.child("Hunted").setValue(null);
        }
        else{
            mRef.child("Hunters").child(mCurrentUserEmail).setValue(null);
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mClient, mListener);
        User.getInstance().setIsPlaying(false);
        Intent myIntent = new Intent(getActivity(), MainPage.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);

    }
}
