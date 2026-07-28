package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class ProviderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button btnSelectService;
    private ImageView btnBack;
    private TextView tvProviderName, tvLocation, tvPrice;
    private String providerId, providerName, address, sourceType;
    private double providerLat, providerLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this);

        setContentView(R.layout.activity_provider_details);

        btnSelectService = findViewById(R.id.btnSelectService);
        btnBack = findViewById(R.id.btnBack);
        mapView = findViewById(R.id.mapView);
        tvProviderName = findViewById(R.id.tvProviderName);
        tvLocation = findViewById(R.id.tvLocation);
        tvPrice = findViewById(R.id.tvPrice);

        Intent intent = getIntent();
        providerId = intent.getStringExtra("provider_id");
        providerName = intent.getStringExtra("provider_name");
        address = intent.getStringExtra("address");
        sourceType = intent.getStringExtra("source_type");
        providerLat = intent.getDoubleExtra("lat", 31.51);
        providerLng = intent.getDoubleExtra("lng", 34.45);

        if (providerName != null) tvProviderName.setText(providerName);
        if (address != null) tvLocation.setText(address);
        
        if (sourceType != null && sourceType.contains("صهريج")) {
            tvPrice.setText("30 شيكل / كوب");
        } else if (sourceType != null && sourceType.contains("بئر")) {
            tvPrice.setText("15 شيكل / كوب");
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSelectService != null) {
            btnSelectService.setOnClickListener(v -> {
                Intent sIntent = new Intent(ProviderDetailsActivity.this, ServicesActivity.class);
                sIntent.putExtra("provider_id", providerId);
                sIntent.putExtra("provider_name", providerName);
                startActivity(sIntent);
            });
        }

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        setupBottomNavigation();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        
        mapboxMap.setStyle(new Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright"), style -> {
            LatLng providerLocation = new LatLng(providerLat, providerLng);
            
            mapboxMap.addMarker(new MarkerOptions()
                    .position(providerLocation)
                    .title(providerName));

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(providerLocation)
                            .zoom(15.0)
                            .build()), 2000);
        });
    }

    private void setupBottomNavigation() {
        if (findViewById(R.id.navHome) != null) {
            findViewById(R.id.navHome).setOnClickListener(v -> {
                Intent intent = new Intent(this, MapExplorerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        if (findViewById(R.id.navWallet) != null) {
            findViewById(R.id.navWallet).setOnClickListener(v -> {
                 startActivity(new Intent(this, WalletActivity.class));
            });
        }

        if (findViewById(R.id.navOrders) != null) {
            findViewById(R.id.navOrders).setOnClickListener(v -> {
                 startActivity(new Intent(this, My_Orders_Activity.class));
            });
        }

        if (findViewById(R.id.navProfile) != null) {
            findViewById(R.id.navProfile).setOnClickListener(v -> {
                 startActivity(new Intent(this, HomeActivity.class));
            });
        }
    }

    @Override protected void onStart() { super.onStart(); if (mapView != null) mapView.onStart(); }
    @Override protected void onResume() { super.onResume(); if (mapView != null) mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); if (mapView != null) mapView.onPause(); }
    @Override protected void onStop() { super.onStop(); if (mapView != null) mapView.onStop(); }
    @Override protected void onSaveInstanceState(@NonNull Bundle outState) { super.onSaveInstanceState(outState); if (mapView != null) mapView.onSaveInstanceState(outState); }
    @Override public void onLowMemory() { super.onLowMemory(); if (mapView != null) mapView.onLowMemory(); }
    @Override protected void onDestroy() { if (mapView != null) mapView.onDestroy(); super.onDestroy(); }
}
