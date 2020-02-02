package com.example.uberdeliverydemo;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uberdeliverydemo.Entities.Parcel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RiderFragment extends Fragment {

    private FloatingActionButton currentLocationButton;

    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private Location currentLocation;

    //GPS
    private LocationManager locationManager;
    private LocationListener locationListener;

    //Data
    private ArrayList<Parcel> parcels = new ArrayList<>();

    private Context context;

    public RiderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        setUpMap();

        setupFirebase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    updateMap(lastKnownLocation);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    //Setups

    private void initViews(){
        currentLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.currLocationFAB);
    }

    private void setUpMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(context));
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();

                        Parcel parcel = (Parcel)marker.getTag();
                        ft.replace(R.id.container_frame_back, new PickUpFragment(parcel));
                        ft.commitAllowingStateLoss();
                    }
                });

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //updateMap(location);
                        currentLocation = location;
                        //LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                //region PERMISSIONS
                if (Build.VERSION.SDK_INT < 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
                }
                else{
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                    else{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);

                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (lastKnownLocation != null){
                            updateMap(lastKnownLocation);
                        }
                    }
                }
                //endregion
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                moveToCurrentLocation();
            }
        });
    }

    private void setupFirebase(){
        //region Firebase Setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parcel");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Parcel parcel = dataSnapShotToParcel(dataSnapshot);

                parcels.add(parcel);
                try {
                    if (parcel.getStatus() == Parcel.Status.Registered)
                        addMarker(parcel);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Parcel parcel = dataSnapShotToParcel(dataSnapshot);
                parcels.remove(findParcelById(parcel.getID()));
                updateMap(currentLocation);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion
    }

    private Parcel findParcelById(String id){
        for (Parcel parcel : parcels){
            if (parcel.getID().equals(id))
                return parcel;
        }

        return null;
    }


    //Map operations

    private void moveToCurrentLocation(){


        if (Build.VERSION.SDK_INT < 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else{
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null){

                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                    updateMap(lastKnownLocation);
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                }
            }
        }
    }

    public void updateMap(Location location){

        mMap.clear();

        if (currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        currentLocationMarker.showInfoWindow();
        addMarkers();
    }

    private void addMarker(Parcel parcel) throws IOException {
        LatLng latLng = stringToLatLng(parcel.getFromAddressLatLng());
        String address = getAddressFromLatLng(stringToLatLng(parcel.getFromAddressLatLng()));
        String fullName = parcel.getRecipientFirstName() + parcel.getRecipientLastName();
        String phoneNumber = parcel.getRecipientPhoneNumber();

        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(address).snippet("Contact " + fullName + ": " + phoneNumber));
        marker.setTag(parcel);
    }

    private void addMarkers(){
        if (parcels == null)
            return;

        for (Parcel parcel : parcels){
            try {
                if (parcel.getStatus() == Parcel.Status.Registered)
                    addMarker(parcel);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Converters

    private Parcel dataSnapShotToParcel(DataSnapshot dataSnapshot){
        if (dataSnapshot.child("isFragile").getChildrenCount() > 0 && dataSnapshot.child("isFragile").getValue(String.class) == null){
            throw new IllegalArgumentException();
        }

        return new Parcel(
                Parcel.Status.valueOf(dataSnapshot.child("status").getValue(String.class)),
                dataSnapshot.child("type").getValue(String.class),
                dataSnapshot.child("fragile").getValue(Boolean.class),
                dataSnapshot.child("weight").getValue(String.class),
                dataSnapshot.child("fromAddress").getValue(String.class),
                dataSnapshot.child("toAddress").getValue(String.class),
                dataSnapshot.child("recipientFirstName").getValue(String.class),
                dataSnapshot.child("recipientLastName").getValue(String.class),
                dataSnapshot.child("recipientPhoneNumber").getValue(String.class),
                dataSnapshot.child("recipientEmailAddress").getValue(String.class),
                dataSnapshot.child("receivedDate").getValue(String.class),
                dataSnapshot.child("deliveryDate").getValue(String.class),
                dataSnapshot.child("shippedDate").getValue(String.class),
                dataSnapshot.child("deliveryGuyName").getValue(String.class),
                dataSnapshot.child("id").getValue(String.class),
                dataSnapshot.child("toAddressLatLng").getValue(String.class),
                dataSnapshot.child("senderEmailAddress").getValue(String.class),
                dataSnapshot.child("fromAddressLatLng").getValue(String.class)
        );
    }

    private LatLng stringToLatLng(String latLngString){
        String[] latlong =  latLngString.split(",");
        int s = latlong[0].indexOf('(') + 1;
        int t = latlong[1].indexOf(')') - 1;
        latlong[0] = latlong[0].substring(s);
        latlong[1] = latlong[1].substring(0, t);
        double lat = Double.parseDouble(latlong[0]);
        double lng = Double.parseDouble(latlong[1]);

        return new LatLng(lat, lng);
    }

    //Others

    private String getAddressFromLatLng(LatLng latLng) throws IOException {
        Geocoder geocoder;
        geocoder = new Geocoder(context, Locale.getDefault());

        return geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
    }
}
