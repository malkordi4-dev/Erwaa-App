package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.android.material.button.MaterialButton;

public class Track_Driver_Activity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // OSMDroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        
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

        // Initialize OSMDroid Map
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        
        GeoPoint driverLoc = new GeoPoint(31.51, 34.45); // Simulated driver position
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(driverLoc);

        Marker marker = new Marker(mapView);
        marker.setPosition(driverLoc);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("السائق");
        mapView.getOverlays().add(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }
}
