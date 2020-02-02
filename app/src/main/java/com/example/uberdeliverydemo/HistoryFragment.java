package com.example.uberdeliverydemo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.uberdeliverydemo.Entities.Member;
import com.example.uberdeliverydemo.Entities.Parcel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ListView parcelsListView;
    ArrayList<Parcel> parcels = new ArrayList<>();
    ArrayAdapter<String> adapter;


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFirebase(view);
    }

    private void setupFirebase(View view){
        parcelsListView = view.findViewById(R.id.parcelsListView);
        adapter = new ArrayAdapter(view.getContext(),android.R.layout.simple_expandable_list_item_1, parcels);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference parcelsRef = database.getReference("Parcel");
        getHistoryParcels();

        parcelsListView.setAdapter(adapter);
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
        double lat = Double.parseDouble(latlong[0]);
        double lng = Double.parseDouble(latlong[1]);

        return new LatLng(lat, lng);
    }

    private Parcel findParcelById(String id){
        for (Parcel parcel : parcels){
            if (parcel.getID().equals(id))
                return parcel;
        }

        return null;
    }

    private void getHistoryParcels(){
        //Get Parcels
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parcel");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Parcel parcel = dataSnapShotToParcel(dataSnapshot);

                Member member = MainActivity.member;

                if (member != null && parcel.getSenderEmailAddress().equals(member.getEmailAddress())){
                    parcels.add(parcel);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Parcel parcel = dataSnapShotToParcel(dataSnapshot);
                parcels.remove(findParcelById(parcel.getID()));
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
