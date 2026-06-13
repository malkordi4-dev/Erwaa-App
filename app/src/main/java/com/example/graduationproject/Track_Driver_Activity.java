package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

public class Track_Driver_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_driver);

        ImageView btnBack = findViewById(R.id.btnBackTrack);
        MaterialButton btnCall = findViewById(R.id.btnCall);
        MaterialButton btnReceived = findViewById(R.id.btnReceived);
        mapView = findViewById(R.id.mapView);

        btnBack.setOnClickListener(v -> finish());

        btnCall.setOnClickListener(v -> {
            Toast.makeText(this, "جاري الاتصال بالسائق...", Toast.LENGTH_SHORT).show();
        });

        btnReceived.setOnClickListener(v -> {
            Intent intent = new Intent(Track_Driver_Activity.this, Water_Delivered_Activity.class);
            startActivity(intent);
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng driverLoc = new LatLng(31.51, 34.45); // Simulated driver position
        googleMap.addMarker(new MarkerOptions().position(driverLoc).title("السائق"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLoc, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
