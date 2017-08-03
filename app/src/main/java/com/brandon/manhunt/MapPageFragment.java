package com.brandon.manhunt;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by brandoncole on 8/1/17.
 */

public class MapPageFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap Gmap;
    View mView;
    LocationManager locationManager;
    LocationListener mLocationListener;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Boolean mIsHunted;


    public MapPageFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Determine if user is hunter/hunted
        //isHunted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Gets the MapView from the XML layout and creates it
        mView = inflater.inflate(R.layout.fragment_map_page, container, false);

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


    }

    void getCurrentLocation(){}


    private void SendLocation(){ // sends location to firebase

        if (mIsHunted)
        {
            DatabaseReference ref = mDatabase.child("Hunted");

        }
        else{

        }
    }

    private void GetLocations() { // gets location of hunters or hunted from firebase

        if (mIsHunted){

        }

        else{

        }
    }

  /*  private void isHunted(){ // check if user is the hunter or hunted

        DatabaseReference ref = mDatabase.child("Hunted");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> childrenSnapshot = dataSnapshot.getChildren();
                String hunted_name = childrenSnapshot.iterator().next().getKey();
                String database_email = mUser.getEmail();
                String user_email = database_email.replace("at", "@").replace("dot", ".");
                mIsHunted = hunted_name.equals(user_email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

}
