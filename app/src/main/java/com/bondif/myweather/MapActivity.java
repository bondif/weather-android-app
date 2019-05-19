package com.bondif.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bondif.myweather.room.Photo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Photo photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        photo = (Photo) intent.getExtras().getSerializable("com.bondif.myweather.photo");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double lat = Double.parseDouble(photo.latitude);
        Double lng = Double.parseDouble(photo.longitude);
        Log.i("MyLog", lat + ", " + lng);

        LatLng place = new LatLng(lng, lat);
        mMap.addMarker(new MarkerOptions().position(place).title(photo.placeName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
