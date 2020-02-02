package com.example.uberdeliverydemo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uberdeliverydemo.Entities.Member;
import com.example.uberdeliverydemo.Entities.Parcel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.Places;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );

    float x1, x2, y1, y2;

    private FirebaseAuth mAuth;

    //FireBase
    FirebaseDatabase database;
    DatabaseReference myRef;

    EditText emailET;
    EditText passwordET;
    EditText repasswordET;
    EditText firstNameET;
    EditText lastNameET;
    EditText idET;
    EditText phoneNumberET;
    AutocompleteSupportFragment autocompleteFragment;

    private String address;
    private LatLng latLng;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    // Initialize Places.

    // Create a new Places client instance.
    PlacesClient placesClient;

    //Data
    private int parcelsCount;
    private ArrayList<Member> members = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        setUpFirebase();

        fillFields();

        initPlaces();

        onStart();
    }

    private void setUpFirebase(){
        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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

        //endregion
    }

    private void initViews(){
        emailET = (EditText) findViewById(R.id.et_email);
        passwordET = (EditText) findViewById(R.id.et_password);
        repasswordET = (EditText) findViewById(R.id.et_repassword);
        firstNameET = (EditText) findViewById(R.id.et_firstName);
        lastNameET = (EditText) findViewById(R.id.et_lastName);
        idET = (EditText) findViewById(R.id.et_id);
        phoneNumberET = (EditText) findViewById(R.id.et_phoneNumber);
    }

    private void initPlaces(){
        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_address_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                address = place.getAddress();
                latLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("places", "An error occurred: " + status);
            }
        });
    }

    private void fillFields(){
        emailET.setText("b@gmail.com");
        passwordET.setText("111111111");
        repasswordET.setText("111111111");
        firstNameET.setText("b");
        lastNameET.setText("b");
        idET.setText("2");
        phoneNumberET.setText("0546401267");
    }

    private Member dataSnapShotToMember(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("firstName").getValue(String.class) == null) {
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

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateUI(FirebaseUser firebaseUser) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    public void onRegClicked(View view) {
        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String emailAddress = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String id = idET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();

        //region Add user to DB
        mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    onRegistrationSuccess();
                } else {
                    onRegistrationFailed();
                }
            }
        });
        //endregion

        register(id, firstName, lastName, phoneNumber, address, latLng.toString(), emailAddress);

    }

    private void onRegistrationSuccess(){
        toastLong("Registration succeed!");
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void onRegistrationFailed(){
        toastLong("Registration failed!");
    }

    public void register(String id, String firstName, String lastName, String phoneNumber, String address, String latLng, String emailAddress) {

        Member member = new Member(id, firstName, lastName, phoneNumber, address, latLng, emailAddress);

        myRef.child(id).setValue(member);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                //If swiped right
                if (x1 > x2){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                break;
        }

        return super.onTouchEvent(event);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void toastLong(String string){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }
}
