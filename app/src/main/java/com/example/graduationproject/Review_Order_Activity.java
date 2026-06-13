package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Review_Order_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnEditLocation = findViewById(R.id.btnEditLocation);
        Button btnConfirmAndSend = findViewById(R.id.btnConfirmAndSend);
        mapView = findViewById(R.id.mapViewReview);

        btnBack.setOnClickListener(v -> finish());
        btnEditLocation.setOnClickListener(v -> finish());

        btnConfirmAndSend.setOnClickListener(v -> {
            Intent intent = new Intent(Review_Order_Activity.this, Order_Status_Activity.class);
            startActivity(intent);
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng defaultLoc = new LatLng(31.5, 34.4);
        googleMap.addMarker(new MarkerOptions().position(defaultLoc).title("موقع التوصيل"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 15));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
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
