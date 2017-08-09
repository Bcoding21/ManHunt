package com.brandon.manhunt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class gamePage extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "MAIN_GAME_PAGE";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DatabaseReference mReference;
    private String mHuntedEmail, mCurrentUserEmail, mHuntedUsername;
    private LocationManager locationManager;
    private long THIRTY_SECONDS = 30000;
    private double Long, Lat;

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
                if (!dataSnapshot.hasChild("Hunted")){

                    String email = User.getInstance().getEmail();
                    User user = new User(email, 0, 0);
                    mReference.child("Hunted").setValue(user);
                    passInfoToGameFragment(true);
                    sendLocation();
                }

                else if (dataSnapshot.hasChild("Hunted")){

                    String email = User.getInstance().getEmail();
                    User user = new User(email, 0, 0);
                    mReference.child("Hunters").child(email).setValue(user);
                    passInfoToGameFragment(false);
                    getLocation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void sendLocation() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, THIRTY_SECONDS / 30, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    //get the latitude
                    double longitude = location.getLongitude();
                    //get the longitude

                    mReference.child("Hunted").child("lat").setValue(latitude);
                    mReference.child("Hunted").child("long").setValue(longitude);
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
            });
        }
    }

    private void getLocation() {

        mReference.child("Hunted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lat = dataSnapshot.child("lat").getValue(Double.class);
                Long = dataSnapshot.child("long").getValue(Double.class);

                MapPageFragment.getInstance().updateMap(Lat, Long);
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
}
