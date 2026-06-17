package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class ProviderDetailsActivity extends AppCompatActivity {

    private MapView mapView;
    private Button btnSelectService;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OSMDroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_provider_details);

        // Initialize Views
        btnSelectService = findViewById(R.id.btnSelectService);
        btnBack = findViewById(R.id.btnBack);
        mapView = findViewById(R.id.mapView);

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Select Service button
        if (btnSelectService != null) {
            btnSelectService.setOnClickListener(v -> {
                Intent intent = new Intent(ProviderDetailsActivity.this, ServicesActivity.class);
                startActivity(intent);
            });
        }

        // Initialize Map
        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setMultiTouchControls(true);
            
            // Example location for the provider (Gaza)
            GeoPoint providerLocation = new GeoPoint(31.51, 34.45); 
            mapView.getController().setZoom(16.0);
            mapView.getController().setCenter(providerLocation);

            // Add marker for the provider
            Marker marker = new Marker(mapView);
            marker.setPosition(providerLocation);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle("موقع المزود");
            mapView.getOverlays().add(marker);
        }
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
