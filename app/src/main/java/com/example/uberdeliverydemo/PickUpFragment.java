package com.example.uberdeliverydemo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.uberdeliverydemo.Entities.Parcel;

public class PickUpFragment extends Fragment {

    private final Parcel mParcel;

    Button pickUpButton;
    CheckBox fragileCheckBox;
    TextView weightTextView;
    TextView typeTextView;
    TextView locationTextView;
    TextView emailAddressTextView;
    TextView fullNameTextView;
    TextView phoneNumberTextView;


    public PickUpFragment() {
        // Required empty public constructor
        mParcel = new Parcel();
    }

    public PickUpFragment(Parcel parcel) {
        mParcel = parcel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
    }

    private void initViews(){
        pickUpButton = getActivity().findViewById(R.id.pickUpButton);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();

                ft.replace(R.id.container_frame_back, new RiderFragment());
                ft.commitAllowingStateLoss();
            }
        });

        fragileCheckBox = getActivity().findViewById(R.id.isFragileCheckBox);
        fragileCheckBox.setChecked(mParcel.getFragile());

        weightTextView = getActivity().findViewById(R.id.packageWeightTextView);
        weightTextView.setText(mParcel.getWeight());

        typeTextView = getActivity().findViewById(R.id.packageTypeTextView);
        typeTextView.setText(mParcel.getType());

        locationTextView = getActivity().findViewById(R.id.packageLocationTextView);
        locationTextView.setText(mParcel.getFromAddress());

        emailAddressTextView = getActivity().findViewById(R.id.emailAddressTextView);
        emailAddressTextView.setText(mParcel.getRecipientEmailAddress());

        fullNameTextView = getActivity().findViewById(R.id.targetNameTextView);
        fullNameTextView.setText(mParcel.getRecipientFirstName() + " " + mParcel.getRecipientLastName());

        phoneNumberTextView = getActivity().findViewById(R.id.targetPhoneNumberTextView);
        phoneNumberTextView.setText(mParcel.getRecipientPhoneNumber());
    }

}
