package com.brandon.manhunt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class gamePage extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "MAIN_GAME_PAGE";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DatabaseReference mReference;
    private String mHuntedEmail, mCurrentUserEmail, mHuntedUsername;
    private LocationManager locationManager;
    private long THIRTY_SECONDS = 30000;
    private double mLongitude, mLatitude;
    private LocationListener mLocationListener;

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

        startSession();
    }

    private void startSession(){

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("Hunted")){  // if user is being hunted

                    mReference.child("GAMEOVER").setValue(false);
                    User user = new User(mCurrentUserEmail, 0, 0);
                    mReference.child("Hunted").setValue(user);
                    passInfoToGameFragment(true);
                    mHuntedEmail = mCurrentUserEmail;


                }

                else if (dataSnapshot.hasChild("Hunted")){ // if user is a hunter

                    User user = new User(mCurrentUserEmail, 0, 0);
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(user);
                    passInfoToGameFragment(false);
                    getHuntedLocation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void passInfoToGameFragment(boolean isHunted){

        if (isHunted) {
            sendLocation(isHunted);
            String s = "YOU ARE BEING HUNTED!";
            GamePageFragment.getInstance().getInformation(s);
            listenForGameOver();

        }
        else{
            sendLocation(isHunted);
            getHuntedInformation();
        }
    }

    private void sendLocation(final boolean isHunted) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                //get the latitude
                double longitude = location.getLongitude();
                //get the longitude

                String name1 = mCurrentUserEmail;
                String name2 = mHuntedEmail;

                if (isHunted){
                    mReference.child("Hunted").child("lat").setValue(latitude);
                    mReference.child("Hunted").child("long").setValue(longitude);
                    getHuntersLocation(latitude, longitude);
                }
                else{
                    mReference.child("Hunters").child(mCurrentUserEmail).child("lat").setValue(latitude);
                    mReference.child("Hunters").child(mCurrentUserEmail).child("long").setValue(longitude);
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7300, 0, mLocationListener);
        }
    }

    private void getHuntedLocation() {

        mReference.child("Hunted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    GamePageFragment.getInstance().getInformation("HUNTED HAS LEFT. WAITING FOR ANOTHER...");
                }
                else {
                    mLatitude = dataSnapshot.child("lat").getValue(Double.class);
                    mLongitude = dataSnapshot.child("long").getValue(Double.class);

                    Location location = new Location("");
                    location.setLatitude(mLatitude);
                    location.setLongitude(mLongitude);

                    GamePageFragment.getInstance().recieveLocation(location);
                    MapPageFragment.getInstance().updateMap(mLatitude, mLongitude);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getHuntersLocation(final double Latit, final double Longit){

        mReference.child("Hunters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    List<Location> location_list = new ArrayList<>();

                    Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> snap = data.iterator();

                    while (snap.hasNext()) {

                        DataSnapshot location = snap.next();
                        mLatitude = location.child("lat").getValue(Double.class);
                        mLongitude = location.child("long").getValue(Double.class);

                        Location user_location = new Location("");
                        user_location.setLatitude(mLatitude);
                        user_location.setLongitude(mLongitude);

                        location_list.add(user_location);
                    }

                    GamePageFragment.getInstance().getHuntersInformation(location_list, Latit, Longit);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                listenForGameOver();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(gamePage.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    private void listenForGameOver(){

        mReference.child("GAMEOVER").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)){
                    endGame();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void endGame(){


        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                GamePageFragment.getInstance().setSecondDisplay("Leaving session in: " + l / 1000);
            }

            @Override
            public void onFinish() {
                if (mCurrentUserEmail.equals(mHuntedEmail)) { // if hunted
                    GamePageFragment.getInstance().getInformation("YOU HAVE BEEN CAUGHT \n GAME OVER!");
                    mReference.child("Hunted").setValue(null);
                }

                else{
                    GamePageFragment.getInstance().getInformation("THE HUNTED HAS BEEN CAUGHT \n GAME OVER!");
                    mReference.child("Hunters").child(mCurrentUserEmail).setValue(null);
                }

                startActivity(new Intent(gamePage.this, MainPage.class));
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "YOU HAVE LEFT THE GAME", Toast.LENGTH_SHORT).show();

        locationManager.removeUpdates(mLocationListener);
        locationManager = null;

        if (mCurrentUserEmail.equals(mHuntedEmail)){
            mReference.child("Hunted").setValue(null);
        }
        else{
            mReference.child("Hunters").child(mCurrentUserEmail).setValue(null);
        }
    }
}
