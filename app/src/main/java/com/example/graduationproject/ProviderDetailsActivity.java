package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView tvProviderName, tvLocation, tvPrice;

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
        tvProviderName = findViewById(R.id.tvProviderName);
        tvLocation = findViewById(R.id.tvLocation);
        tvPrice = findViewById(R.id.tvPrice);

        // Get Data from Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("provider_name");
        String address = intent.getStringExtra("address");
        String sourceType = intent.getStringExtra("source_type");
        double lat = intent.getDoubleExtra("lat", 31.51);
        double lng = intent.getDoubleExtra("lng", 34.45);

        // Update UI with received data
        if (name != null) tvProviderName.setText(name);
        if (address != null) tvLocation.setText(address);
        
        // Update price based on source type (example logic)
        if (sourceType != null && sourceType.contains("صهريج")) {
            tvPrice.setText("30 شيكل / كوب");
        } else if (sourceType != null && sourceType.contains("بئر")) {
            tvPrice.setText("15 شيكل / كوب");
        }

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Select Service button
        if (btnSelectService != null) {
            btnSelectService.setOnClickListener(v -> {
                Intent sIntent = new Intent(ProviderDetailsActivity.this, ServicesActivity.class);
                sIntent.putExtra("provider_name", name);
                startActivity(sIntent);
            });
        }

        // Initialize Map with provider location
        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setMultiTouchControls(true);
            
            GeoPoint providerLocation = new GeoPoint(lat, lng); 
            mapView.getController().setZoom(16.0);
            mapView.getController().setCenter(providerLocation);

            // Add marker for the provider
            Marker marker = new Marker(mapView);
            marker.setPosition(providerLocation);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(name);
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
