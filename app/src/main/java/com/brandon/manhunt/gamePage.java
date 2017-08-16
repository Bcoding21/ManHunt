package com.brandon.manhunt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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
    protected String mHuntedEmail, mCurrentUserEmail, mHuntedUsername;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_UPDATE_INTERVAL = 500;
    private static int DISPLACEMENT = 0;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClient;
    private byte mTimesTilHunterHint;
    private byte mTimesTilHuntedHint;
    private boolean mFirstHint, mIsInGame;
    private boolean isCurrentlyPlaying;
    private Button mButton;

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

        //Firebase
        mReference = FirebaseDatabase.getInstance().getReference();

        // set current user
        User.getInstance().setIsPlaying(true);
        mCurrentUserEmail = User.getInstance().getEmail();
        setIsPlaying();

        // set delay till hint
        mTimesTilHuntedHint = 10;
        mTimesTilHunterHint = 10;
        mFirstHint = true;

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
        Log.d("CHECK HERE", "LOCATION REQUEST");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d("CHECK HERE", "BUILD GOOGLE API CLIENT");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        Log.d("CHECK HERE", "CHECK PLAY SERVICES");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            else{
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
            }

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("CHECK HERE", "REQUEST PERMISSIONS");
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

    public static class userLocation{

        public double getLat() {
            return mLat;
        }

        public double getLong() {
            return mLong;
        }

        private double mLat;
        private double mLong;

        userLocation(){
            mLat = 0.0;
            mLong = 0.0;
        }

        userLocation(double lat, double Long){
            mLat = lat;
            mLong = Long;
        }

    }


    private void startSession(){

        Log.d("CHECK HERE", "Start session");

        mReference.child("GAMEOVER").setValue(false);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("Hunted")){  // if user is being hunted

                    boolean truth = User.getInstance().isPlaying();
                    String s = mCurrentUserEmail;
                    User user = new User(mCurrentUserEmail, 0, 0);
                    mReference.child("Hunted").setValue(user);
                    mReference.child("Hunted").child("hintLocation").setValue(new userLocation(0,0));
                    passInfoToGameFragment(true);
                    mHuntedEmail = mCurrentUserEmail;

                }

                else if (dataSnapshot.hasChild("Hunted")){ // if user is a hunter

                    User user = new User(mCurrentUserEmail, 0, 0);
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(user);
                    mReference.child("Hunters").child(mCurrentUserEmail).child("hintLocation").setValue(new
                            userLocation(0,0));
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
            mSectionsPagerAdapter.getGamePageFragment().getInformation(s);
            mHuntedEmail = mCurrentUserEmail;
            mSectionsPagerAdapter.getQuitPageFragment().passInformation(mHuntedEmail, mCurrentUserEmail, this, mGoogleApiClient);
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
                mSectionsPagerAdapter.getGamePageFragment().getInformation(s);
                mSectionsPagerAdapter.getQuitPageFragment().passInformation(mHuntedEmail, mCurrentUserEmail, gamePage.this, mGoogleApiClient);
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

    private void getHuntedLocation(final Location hunterLocation){
        mReference.child("Hunted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("lat") && dataSnapshot.hasChild("long")) {
                        double latitude = dataSnapshot.child("lat").getValue(Double.class);
                        double longitude = dataSnapshot.child("long").getValue(Double.class);
                        Location huntedLocation = new Location("");
                        huntedLocation.setLatitude(latitude);
                        huntedLocation.setLongitude(longitude);
                        mSectionsPagerAdapter.getGamePageFragment().receiveHuntedLocation(huntedLocation, hunterLocation);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getHuntedHintLocation(final Location huntedLocation){
        mReference.child("Hunted").child("hintLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("lat").getValue(Double.class) != null && dataSnapshot.child("long").getValue(Double.class) != null) {
                        double lat = dataSnapshot.child("lat").getValue(Double.class);
                        double Long = dataSnapshot.child("long").getValue(Double.class);
                        Location hunterLocation = new Location("");
                        hunterLocation.setLatitude(lat);
                        hunterLocation.setLongitude(Long);
                        String s = "Hunted coordinates\nLat: " + lat + "\nLong: " + Long;
                        //mSectionsPagerAdapter.getGamePageFragment().getInformation(s);
                        mSectionsPagerAdapter.getMapPageFragment().updateMap(lat, Long);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendHuntedLocation(Location huntedLocation) {
        double latitude = huntedLocation.getLatitude();
        double longitude = huntedLocation.getLongitude();
        if (mFirstHint){
            mReference.child("Hunted").child("hintLocation").child("lat").setValue(latitude);
            mReference.child("Hunted").child("hintLocation").child("long").setValue(longitude);
            mFirstHint = false;
        }
        mReference.child("Hunted").child("lat").setValue(latitude);
        mReference.child("Hunted").child("long").setValue(longitude);

        if (mTimesTilHuntedHint == 0){
            mReference.child("Hunted").child("hintLocation").child("lat").setValue(latitude);
            mReference.child("Hunted").child("hintLocation").child("long").setValue(longitude);
            mTimesTilHuntedHint = 10;
        }
        mTimesTilHuntedHint--;
    }

    private void getHuntersHintLocation(final Location myLocation){
        mReference.child("Hunters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> snap = children.iterator();
                    List<Location> locations = new ArrayList<Location>();
                    DataSnapshot data = snap.next();
                    if (data.exists()) {
                        DataSnapshot data2 = data.child("hintLocation");
                        if (data2.getValue() != null) {
                            double Lat = data2.child("lat").getValue(Double.class);
                            double Long = data2.child("long").getValue(Double.class);
                            Location location = new Location("");
                            location.setLatitude(Lat);
                            location.setLongitude(Long);
                            mSectionsPagerAdapter.getMapPageFragment().updateMap(Lat, Long);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendHuntersLocation(Location huntersLocation){

        double latitude = huntersLocation.getLatitude();
        double longitude = huntersLocation.getLongitude();

        if (mFirstHint){
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLocation").child("lat").setValue(latitude);
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLocation").child("long").setValue(longitude);
            mFirstHint = false;
        }
        mReference.child("Hunters").child(mCurrentUserEmail).child("lat").setValue(latitude);
        mReference.child("Hunters").child(mCurrentUserEmail).child("long").setValue(longitude);

        if (mTimesTilHunterHint == 0){

            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLocation").child("lat").setValue(latitude);
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLocation").child("long").setValue(longitude);
            mTimesTilHunterHint = 10;
        }
        mTimesTilHunterHint--;
    }


    private void getHuntersLocation(final Location huntedLocation) {
        mReference.child("Hunters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> hunters_children = data.iterator();
                    List<Location> huntersLocation = new ArrayList<Location>();
                    double longitude, latitude;
                    DataSnapshot son = null;

                    while (hunters_children.hasNext()) {

                        son = hunters_children.next();
                        if (son.hasChild("lat") && son.hasChild("long")) {

                            latitude = son.child("lat").getValue(Double.class).doubleValue();
                            longitude = son.child("long").getValue(Double.class).doubleValue();

                            if (latitude != 0 || longitude != 0) {
                                Location hunterLocation = new Location("");
                                hunterLocation.setLongitude(longitude);
                                hunterLocation.setLatitude(latitude);
                                huntersLocation.add(hunterLocation);
                            }
                        }
                        mSectionsPagerAdapter.getGamePageFragment().receiveHuntersLocations(huntersLocation, huntedLocation);
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


        if (location != null) {
            if (mCurrentUserEmail.equals(mHuntedEmail)) {
                sendHuntedLocation(location);
                getHuntersLocation(location);
                getHuntersHintLocation(location);
            } else {
                sendHuntersLocation(location);
                getHuntedLocation(location);
                getHuntedHintLocation(location);
            }
        }


    }

    private void listenForEndGame(){
        mReference.child("GAMEOVER").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)){
                    setGameOver(mGoogleApiClient, gamePage.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setGameOver(final GoogleApiClient client, final LocationListener listener){

        mSectionsPagerAdapter.getMapPageFragment().displayHuntedCaughtMessage();

        if (client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, listener);
            client.disconnect();
        }
        mReference.child("Hunted").setValue(null);
        mReference.child("Hunters").setValue(null);
        mReference.child("GAMEOVER").setValue(false);
        mSectionsPagerAdapter.getGamePageFragment().displayExitButton();
    }

    private void setIsPlaying(){
        checkHunted();
    }


    private void checkHunted() {
        mReference.child("Hunted").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    if (mCurrentUserEmail.equals(dataSnapshot.getValue(String.class))) {
                        isCurrentlyPlaying = true;
                    } else {
                        checkHunters();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkHunters() {
        mReference.child("Hunters").child(mCurrentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                isCurrentlyPlaying = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (mCurrentUserEmail.equals(mHuntedEmail)){
            mReference.child("Hunted").setValue(null);
        }
        else{
            mReference.child("Hunters").child(mCurrentUserEmail).setValue(null);
        }
        Intent myIntent = new Intent(gamePage.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            Toast.makeText(this, "Location request stopped", Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = null;
    }
}