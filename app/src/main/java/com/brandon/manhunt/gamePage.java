package com.brandon.manhunt;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class gamePage extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "MAIN_GAME_PAGE";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DatabaseReference mReference;
    private String mHuntedEmail, mCurrentUserEmail, mHuntedUsername;
    private LocationManager locationManager;
    private double mLongitude, mLatitude;
    private LocationListener mLocationListener;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_UPDATE_INTERVAL = 500;
    private static int DISPLACEMENT = 0;
    private boolean mRequestingLocationUpdate = false;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClient;
    private Location mLastlocation;
    private ProgressDialog mProgress;
    private byte mTimesTilHunterHint;
    private byte mTimesTilHuntedHint;
    private boolean firstHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        // set viewPager and tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);

        // set and Create Adapter/TabLayout
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);

        // set Up location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Firebase
        mReference = FirebaseDatabase.getInstance().getReference();

        // set current user email
        mCurrentUserEmail = User.getInstance().getEmail();

        mProgress = new ProgressDialog(this);

        // set delay till hint
        mTimesTilHuntedHint = 10;
        mTimesTilHunterHint = 10;
        firstHint = true;

        // Permission check
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_CODE);
        } else {

            if (checkPlayServices()) {

                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        startSession();

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }

    }



    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            else{
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                    }
                }
                break;
        }
    }


    private void startSession(){

        mReference.child("GAMEOVER").setValue(false);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("Hunted")){  // if user is being hunted


                    User user = new User(mCurrentUserEmail, 0, 0, 0, 0);
                    mReference.child("Hunted").setValue(user);
                    passInfoToGameFragment(true);
                    mHuntedEmail = mCurrentUserEmail;


                }

                else if (dataSnapshot.hasChild("Hunted")){ // if user is a hunter

                    User user = new User(mCurrentUserEmail, 0, 0, 0, 0);
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(user);
                    passInfoToGameFragment(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listenForEndGame();
    }

    private void passInfoToGameFragment(boolean isHunted){

        if (isHunted) {
            String s = "YOU ARE BEING HUNTED!";
            GamePageFragment.getInstance().getInformation(s);
        }
        else{
            getHuntedInformation();
        }

    }


    private void getHuntedInformation(){
        mReference.child("Hunted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHuntedEmail = dataSnapshot.child("email").getValue(String.class);
                setHuntedName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setHuntedName(){
        mReference.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String)dataSnapshot.child(mHuntedEmail).getValue();
                mHuntedUsername = name;

                String s = "You are hunting: " + mHuntedUsername;
                GamePageFragment.getInstance().getInformation(s);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void requestLocationUpdates(){

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void getHuntedLocation(final double myLattitude, final double myLongitude){
        mReference.child("Hunted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("hintLat") && dataSnapshot.hasChild("hintLong")) {
                        double latitude = dataSnapshot.child("hintLat").getValue(Double.class);
                        double longitude = dataSnapshot.child("hintLong").getValue(Double.class);
                        mLastlocation.setLongitude(longitude);
                        mLastlocation.setLatitude(latitude);
                        GamePageFragment.getInstance().recieveHuntedLocation(mLastlocation, myLattitude, myLongitude);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendHuntersLocation(double latitude, double longitude){

        if (firstHint){
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLat").setValue(latitude);
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLong").setValue(longitude);
            firstHint = false;
        }
        mReference.child("Hunters").child(mCurrentUserEmail).child("lat").setValue(latitude);
        mReference.child("Hunters").child(mCurrentUserEmail).child("long").setValue(longitude);

        if (mTimesTilHunterHint == 0){
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLat").setValue(latitude);
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLong").setValue(longitude);
            mTimesTilHunterHint = 10;
        }
        mTimesTilHunterHint--;
    }

    private void sendHuntedLocation() {
        double latitude = mLastlocation.getLatitude();
        double longitude = mLastlocation.getLongitude();

        if (firstHint){
            mReference.child("Hunted").child("hintLat").setValue(latitude);
            mReference.child("Hunted").child("hintLong").setValue(longitude);
            firstHint = false;
        }
        mReference.child("Hunted").child("lat").setValue(latitude);
        mReference.child("Hunted").child("long").setValue(longitude);

        if (mTimesTilHuntedHint == 0){
            mReference.child("Hunted").child("hintLat").setValue(latitude);
            mReference.child("Hunted").child("hintLong").setValue(longitude);
            mTimesTilHuntedHint = 10;
        }
        mTimesTilHuntedHint--;
    }

    private void getHuntersLocaiton() {
        mReference.child("Hunters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> hunters_children = data.iterator();
                    List<Location> hunters_locations = new ArrayList<Location>();
                    double longitude, latitude;
                    DataSnapshot son = null;

                        while (hunters_children.hasNext()) {

                            son = hunters_children.next();

                            if (son.hasChild("hintLat") && son.hasChild("hintLong")) {

                            latitude = son.child("hintLat").getValue(Double.class);
                            longitude = son.child("hintLong").getValue(Double.class);

                            Location location = new Location("");
                            location.setLongitude(longitude);
                            location.setLatitude(latitude);

                            hunters_locations.add(location);
                        }

                        GamePageFragment.getInstance().receiveHuntersInformation(hunters_locations, mLastlocation.getLatitude(),
                                mLastlocation.getLongitude());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastlocation = location;

        if (mLastlocation != null) {
            if (mCurrentUserEmail.equals(mHuntedEmail)) {
                sendHuntedLocation();
                getHuntersLocaiton();
            } else {
                sendHuntersLocation(location.getLatitude(), location.getLongitude());
                getHuntedLocation(location.getLatitude(), location.getLongitude());

            }
        }
    }

    private void listenForEndGame(){
        mReference.child("GAMEOVER").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)){
                   GamePageFragment.getInstance().setGameOver(mGoogleApiClient, gamePage.this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }

    }
}
