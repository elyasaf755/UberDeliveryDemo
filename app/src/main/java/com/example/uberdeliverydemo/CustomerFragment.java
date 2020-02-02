package com.example.uberdeliverydemo;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uberdeliverydemo.Entities.Member;
import com.example.uberdeliverydemo.Entities.Parcel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerFragment extends Fragment {

    private Button findButton;
    private Button checkButton;
    private Button addAddButton;

    private CheckBox isFragileCheckBox;
    private Button addButton;
    private Spinner weightSpinner;
    private Spinner typeSpiner;
    private TextView locationTextView;
    private TextView emailAddressTextView;
    private TextView targetNameTextView;
    private Button historyButton;
    private TextView deliveryDateTextView;

    private String address;
    private LatLng latLng;

    //GPS
    LocationManager locationManager;
    LocationListener locationListener;

    //FireBase
    FirebaseDatabase database;
    DatabaseReference myRef;

    //Data
    private int parcelsCount;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Parcel> parcels = new ArrayList<>();

    private Context context;

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setupLocation();
        try {
            setupLocation();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        setupFirebase();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    //Setups

    private void setupLocation() throws IOException {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    getCurrentAddress(new LatLng(location.getLatitude(), location.getLongitude()));
                    //setAddress(location.getLatitude(), location.getLongitude());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            //if permission granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            double lat;
            double lng;

            if (lastKnownLocation != null){
                lat = lastKnownLocation.getLatitude();
                lng = lastKnownLocation.getLongitude();

                getCurrentAddress(new LatLng(lat, lng));
                //setAddress(lat, lng);
            }
        }
    }

    private void setAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        address = addresses.get(0).getAddressLine(0);
        latLng = new LatLng(latitude, longitude);

        if (locationTextView != null){
            locationTextView.setText(address);
        }
    }

    public LatLng getLatLngFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private String getCurrentAddress(LatLng latLng) throws IOException {
        Geocoder geocoder;
        geocoder = new Geocoder(context, Locale.getDefault());

        address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);

        return address;
    }

    private void setupFirebase(){
        //region Firebase Setup
        database = FirebaseDatabase.getInstance();

        //Get Members
        myRef = database.getReference("Member");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                members.add(dataSnapShotToMember(dataSnapshot));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get Parcels Count
        DatabaseReference dbRef = database.getReference("Config");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parcelsCount = dataSnapshot.child("ParcelsCount").getValue(int.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //endregion
    }

    private void initializeViews(View view) {
        isFragileCheckBox = view.findViewById(R.id.isFragileCheckBox);
        typeSpiner = view.findViewById(R.id.packageTypeSpinner2);
        typeSpiner.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, Parcel.Types));
        weightSpinner = view.findViewById(R.id.packageWeightSpinner);
        weightSpinner.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, Parcel.Weights));
        addButton = view.findViewById(R.id.addButton);
        addButton.setEnabled(false);
        historyButton=view.findViewById(R.id.historyButton);
        locationTextView = view.findViewById(R.id.packageLocationTextView);
        locationTextView.setText("Destination Address");
        emailAddressTextView = view.findViewById(R.id.emailAddressPlainText);
        emailAddressTextView.setText(null);
        targetNameTextView = view.findViewById(R.id.targetNameTextView);
        deliveryDateTextView = view.findViewById(R.id.deliveryDateTextView);
        deliveryDateTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findButton = (Button) view.findViewById(R.id.findButton);
        findButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                findMember(v);
            }
        });

        checkButton = (Button) view.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkInfo(v);
            }
        });

        addAddButton = (Button) view.findViewById(R.id.addButton);
        addAddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addParcel(v);
            }
        });
    }


    //Search Operations

    private String getParcelId(){

        return String.valueOf(parcelsCount);
    }

    private Member getMemberByEmailAddress(String emailAddress){
        for (Member member : members){
            if (member.getEmailAddress().equals(emailAddress)){
                return member;
            }
        }

        return null;
    }


    //Converters

    private Member dataSnapShotToMember(DataSnapshot dataSnapshot){
        if (dataSnapshot.child("firstName").getValue(String.class) == null){
            throw new IllegalArgumentException();
        }

        return new Member(
                dataSnapshot.child("id").getValue(String.class),
                dataSnapshot.child("firstName").getValue(String.class),
                dataSnapshot.child("lastName").getValue(String.class),
                dataSnapshot.child("phoneNumber").getValue(String.class),
                dataSnapshot.child("address").getValue(String.class),
                dataSnapshot.child("latLng").getValue(String.class),
                dataSnapshot.child("emailAddress").getValue(String.class)
        );
    }

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


    //Actions

    public void addParcel(View view){

        if (checkFields() == false){
            addButton.setEnabled(false);
            toastLong("Fail! Please re-check fields.");
            return;
        }


        Member member = getMemberByEmailAddress(emailAddressTextView.getText().toString());

        Parcel parcel = new Parcel(
                Parcel.Status.Registered,
                typeSpiner.getSelectedItem().toString(),
                isFragileCheckBox.isChecked(),
                weightSpinner.getSelectedItem().toString(),
                MainActivity.member.getAddress(),
                member.getAddress(),
                member.getFirstName(),
                member.getLastName(),
                member.getPhoneNumber(),
                member.getEmailAddress(),
                deliveryDateTextView.getText().toString(),
                "NOT YET DELIVERED",
                "NOT YET SHIPPED",
                "NOT YET TAKEN BY DELIVERY GUY",
                getParcelId(),
                member.getLatLng(),
                MainActivity.member.getEmailAddress(),
                MainActivity.member.getLatLng()
        );

        //Get Parcels
        myRef = database.getReference("Parcel");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                parcels.add(dataSnapShotToParcel(dataSnapshot));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child(String.valueOf(parcelsCount)).setValue(parcel);

        //Increase parcel counter
        DatabaseReference dbref = database.getReference("Config").child("ParcelsCount");
        parcelsCount += 1;

        dbref.setValue(parcelsCount);

        toastLong("Success! Your parcel have been added.");

        replaceFragment(new RiderFragment());
    }

    public void findMember(View view){

        Member member = getMemberByEmailAddress(emailAddressTextView.getText().toString());

        if (member != null){
            targetNameTextView.setText(member.getFirstName() + " " + member.getLastName());
            locationTextView.setText(member.getAddress());
            toastShort("Success! Member found!");
        }
        else{
            targetNameTextView.setText("");
            toastShort("Fail! Member not found!");
        }
    }

    private void checkInfo(View view){

        if (checkFields() == false){
            toastShort("Fail! Please re-check fields.");
        }
        else{
            toastShort("Success! You can add now.");
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        ft.replace(R.id.container_frame_back, fragment);
        ft.commitAllowingStateLoss();
    }


    //Validations

    private boolean checkFields(){
        if (checkLocation() && checkWeight() && checkPersonalInfo() && checkType()){
            addButton.setEnabled(true);
            return true;
        }
        else{
            addButton.setEnabled(false);
            return false;
        }
    }

    private boolean checkWeight(){
        return weightSpinner.getSelectedItem().toString() != "Select Weight";
    }

    private boolean checkType(){
        return typeSpiner.getSelectedItem().toString() != "Select Type";
    }
    //phone + name
    private boolean checkPersonalInfo(){
        Member member = getMemberByEmailAddress(emailAddressTextView.getText().toString());
        if (member == null){
            return false;
        }

        return (member.getFirstName() + " " + member.getLastName()).equals(targetNameTextView.getText().toString());
    }

    private boolean checkLocation(){
        return locationTextView.getText().toString() != "Location" && checkInternetConnection();
    }

    private boolean checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else{
            return false;
        }
    }

    //Others

    private void toastLong(String string){
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    private void toastShort(String string){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
