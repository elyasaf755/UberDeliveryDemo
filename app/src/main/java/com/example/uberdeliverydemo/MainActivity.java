package com.example.uberdeliverydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.uberdeliverydemo.Entities.Member;

public class MainActivity extends AppCompatActivity {

    private Button customerButton, riderButton, historyButton;

    public static Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //Show default fragment
        addFragment(new RiderFragment(), false, "two");
    }

    private void initViews(){
        customerButton = (Button) findViewById(R.id.customerButton);
        riderButton = (Button) findViewById(R.id.riderButton);
        historyButton = (Button) findViewById(R.id.historyButton);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new CustomerFragment(), false, "one");
            }
        });

        riderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new RiderFragment(), false, "two");
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new HistoryFragment(), false, "three");
            }
        });
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        ft.replace(R.id.container_frame_back, fragment, tag);
        ft.commitAllowingStateLoss();
    }
}
