package com.brandon.manhunt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
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
    protected String mHuntedEmail, mCurrentUserEmail, mHuntedUsername;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_UPDATE_INTERVAL = 500;
    private static int DISPLACEMENT = 0;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClient;
    private Location mLastlocation;
    private byte mTimesTilHunterHint;
    private byte mTimesTilHuntedHint;
    private boolean mFirstHint, mIsInGame;
    private GamePageFragment mGameFragment;
    private MapPageFragment mMapFragment;
    private QuitPageFragment mQuitFragment;
    private boolean isCurrentlyPlaying;

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

        // set Fragment References
        mGameFragment = GamePageFragment.getInstance();
        mMapFragment = MapPageFragment.getInstance();
        mQuitFragment = QuitPageFragment.getInstance();

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
            Log.d("CHECK HERE", "IN ON START PERMISSIONS");

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_CODE);

        } else {

            if (checkPlayServices()) {
                Log.d("CHECK HERE", "PERMISSIONS");
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


    private void startSession(){

        Log.d("CHECK HERE", "Start session");

        mReference.child("GAMEOVER").setValue(false);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("Hunted")){  // if user is being hunted

                    boolean truth = User.getInstance().isPlaying();
                    String s = mCurrentUserEmail;
                    User user = new User(mCurrentUserEmail, 0, 0, 0, 0);
                    mReference.child("Hunted").setValue(user);


                    passInfoToGameFragment(true);
                    mHuntedEmail = mCurrentUserEmail;

                }

                else if (dataSnapshot.hasChild("Hunted")){ // if user is a hunter

<<<<<<< HEAD
                    User user = new User(mCurrentUserEmail, 0, 0);
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(user);
                    passInfoToGameFragment(false);
                    //getHuntedLocation();
=======
                    mReference.child("Hunted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.child("email").getValue(String.class);
                            if (mCurrentUserEmail.equals(s)){
                                passInfoToGameFragment(true);
                            }
                            else{
                                User user = new User(mCurrentUserEmail, 0, 0, 0, 0);
                                mReference.child("Hunters").child(mCurrentUserEmail).setValue(user);
                                passInfoToGameFragment(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
>>>>>>> fe75cbbac88b247d32cde8550fc03f828b1698d5
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
            mGameFragment.getInformation(s);
            mHuntedEmail = mCurrentUserEmail;
            mQuitFragment.passInformation(mHuntedEmail, mCurrentUserEmail, this, mGoogleApiClient);
        }
        else{
            getHuntedInformation();
        }

    }


<<<<<<< HEAD
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
            return;
        } //else {
            //startrequestiing();
       // }
    //}

        mLocationListener = new LocationListener() {
=======
    private void getHuntedInformation(){
        mReference.child("Hunted").addListenerForSingleValueEvent(new ValueEventListener() {
>>>>>>> fe75cbbac88b247d32cde8550fc03f828b1698d5
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
                mGameFragment.getInformation(s);
                mQuitFragment.passInformation(mHuntedEmail, mCurrentUserEmail, gamePage.this, mGoogleApiClient);
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
                        mGameFragment.recieveHuntedLocation(mLastlocation, myLattitude, myLongitude);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendHuntersLocation(double latitude, double longitude){

        if (mFirstHint){
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLat").setValue(latitude);
            mReference.child("Hunters").child(mCurrentUserEmail).child("hintLong").setValue(longitude);
            mFirstHint = false;
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

        if (mFirstHint){
            mReference.child("Hunted").child("hintLat").setValue(latitude);
            mReference.child("Hunted").child("hintLong").setValue(longitude);
            mFirstHint = false;
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

                        mGameFragment.receiveHuntersInformation(hunters_locations, mLastlocation.getLatitude(),
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

        String a = mCurrentUserEmail;
        String b = mHuntedEmail;

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
                   mGameFragment.getInstance().setGameOver(mGoogleApiClient, gamePage.this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setIsPlaying(){
        checkHunted();
    }

<<<<<<< HEAD
    //@Override
    public void onRequestPermissionsResult() {
        //if yes
        //startrequesting
    }

    private void listenForGameOver(){

        mReference.child("GAMEOVER").addValueEventListener(new ValueEventListener() {
=======
    private void checkHunted() {
        mReference.child("Hunted").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
>>>>>>> fe75cbbac88b247d32cde8550fc03f828b1698d5
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mCurrentUserEmail.equals(dataSnapshot.getValue(String.class))){
                    isCurrentlyPlaying = true;
                }
                else{
                    checkHunters();
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

    @Override
<<<<<<< HEAD
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "YOU HAVE LEFT THE GAME", Toast.LENGTH_SHORT).show();

=======
    public void onBackPressed() {
        super.onBackPressed();
>>>>>>> fe75cbbac88b247d32cde8550fc03f828b1698d5
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
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = null;

    }
}
