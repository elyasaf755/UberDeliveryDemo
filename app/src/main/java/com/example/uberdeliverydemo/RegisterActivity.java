package com.example.uberdeliverydemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.uberdeliverydemo.Entities.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class RegisterActivity extends AppCompatActivity {

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

    private void fillFields(){
        emailET.setText("b@gmail.com");
        passwordET.setText("111111111");
        repasswordET.setText("111111111");
        firstNameET.setText("b");
        lastNameET.setText("b");
        idET.setText("1");
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
                dataSnapshot.child("emailAddress").getValue(String.class)
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser firebaseUser) {

    }


    public void onRegClicked(View view) {
        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String id = idET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();

        //region Add user to DB
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("onComplete:RegActivity", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("onComplete:RegActivity", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                // ...
            }
        });
        //endregion

        register(id, firstName, lastName, email, phoneNumber);

    }

    public void register(String id, String firstName, String lastName, String emailAddress, String phoneNumber) {

        Member member = new Member(id, firstName, lastName, emailAddress, phoneNumber);

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


}
