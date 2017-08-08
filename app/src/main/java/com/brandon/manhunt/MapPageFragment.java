package com.brandon.manhunt;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;
import static com.brandon.manhunt.GamePageFragment.mHuntedEmail;

/**
 * Created by brandoncole on 8/1/17.
 */

public class MapPageFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap Gmap;
    View mView;
    public Handler handle = new Handler();
    private Runnable r;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private String mEmail, mUsername;
    LocationManager locationManager;
    LocationListener mLocationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Gets the MapView from the XML layout and creates it
        mView = inflater.inflate(R.layout.fragment_map_page, container, false);

        //Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // setup email and username
        mEmail = User.getInstance().getEmail();
        mUsername = User.getInstance().getDisplayName();

        return mView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(R.id.Map);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        Gmap = googleMap;
        Gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setIndoorEnabled(true);
        //Location location;
        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();
        //LatLng latLng = new LatLng (latitude, longitude);

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(37.42011307755486, -122.08767384446583)));
        //googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.42011307755486, -122.08767384446583),17.2f));

        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(37.42011307755486, -122.08767384446583))
                .radius(100)
                .strokeColor(R.color.colorPrimary)
                .fillColor(R.color.colorPrimary));

        if (User.getInstance().isHunted()){
            sendHuntedLocation();
        }
        else{
           // receiveHuntedLocation();
        }
    }

    public MapPageFragment(){}

    private void receiveHuntedLocation(){

       // getHuntedEmail();

        String name = mHuntedEmail;
        DatabaseReference query = mReference.child("Hunted").child(name);

        query.addValueEventListener(new ValueEventListener() {
            double lat = 0;
            double Longit = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lat = (double)dataSnapshot.child("lat").getValue();
                Longit = (double)dataSnapshot.child("long").getValue();
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

                //TODO send locations

                handle.postDelayed(this, 250);
            }
        };
        handle.postDelayed(r, 0);
    }


    private void getHuntedEmail(){

        DatabaseReference query = mReference.child("Hunted");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Objects> myMap = (HashMap) dataSnapshot.getValue();
                    for (String key : myMap.keySet()) {
                        mHuntedEmail = key;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handle.removeCallbacks(r);
    }


}
