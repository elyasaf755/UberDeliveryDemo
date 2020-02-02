package com.example.uberdeliverydemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.uberdeliverydemo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;
    Button pickUpButton;

    public MarkerInfoWindowAdapter(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null);
        pickUpButton = mWindow.findViewById(R.id.pickUpButton);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    private void renderWindowText(Marker marker, View view){
        String address = marker.getTitle();
        TextView addressTV = (TextView)view.findViewById(R.id.address);

        if (address == null){
            addressTV.setText(address);
        }

        String phoneNumber = marker.getSnippet();
        TextView phoneNumberTV = (TextView)view.findViewById(R.id.phoneNumber);

        if (phoneNumber == null){
            phoneNumberTV.setText(address);
        }
    }
}
