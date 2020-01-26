package com.example.uberdeliverydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uberdeliverydemo.ui.LoginActivity;
import com.example.uberdeliverydemo.ui.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void redirectActivity(){
        Intent intent = new Intent(getApplicationContext(), RiderActivity.class);
        startActivity(intent);
    }

    public void navClicked(View ciew){
        Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
        startActivity(intent);
    }

    public void mapsClicked(View view){
        redirectActivity();
    }

    public void redirectLogin(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void redirectReg(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}
