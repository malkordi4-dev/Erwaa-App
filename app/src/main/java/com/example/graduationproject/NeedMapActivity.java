package com.example.graduationproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class NeedMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "NeedMapDebug";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;

    private MaterialCardView btnZoomIn, btnZoomOut, btnMyLocation;
    private MaterialCardView bottomSheetDetails;
    private MaterialButton btnCoordinateInitiative;

    private TextView tvLocationName, tvDeficitPercentage, tvWaterVolumeNeeded;

    private LinearLayout bottomNavProvider;
    private LinearLayout navDashboard, navInitiatives, navNeedMap, navWallet;

    private FirebaseFirestore db;
    private Map<String, Map<String, Object>> markerDataMap = new HashMap<>();
    private LatLng defaultLocation = new LatLng(31.5234, 34.4485);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_map);

        db = FirebaseFirestore.getInstance();
        initViews();
        setupBottomSheet();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupClickListeners();
    }

    private void initViews() {
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        bottomSheetDetails = findViewById(R.id.bottomSheetDetails);
        btnCoordinateInitiative = findViewById(R.id.btnCoordinateInitiative);
        bottomNavProvider = findViewById(R.id.bottomNavProvider);

        tvLocationName = findViewById(R.id.tvLocationName);
        tvDeficitPercentage = findViewById(R.id.tvDeficitPercentage);
        tvWaterVolumeNeeded = findViewById(R.id.tvWaterVolumeNeeded);

        if (bottomNavProvider != null) {
            navDashboard = (LinearLayout) bottomNavProvider.getChildAt(0);
            navInitiatives = (LinearLayout) bottomNavProvider.getChildAt(1);
            navNeedMap = (LinearLayout) bottomNavProvider.getChildAt(2);
            navWallet = (LinearLayout) bottomNavProvider.getChildAt(3);
        }
    }

    private void setupBottomSheet() {
        if (bottomSheetDetails == null) return;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDetails);
        bottomSheetBehavior.setPeekHeight((int) (320 * getResources().getDisplayMetrics().density));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
        getNeedsLocationsFromFirestore();

        mMap.setOnMarkerClickListener(marker -> {
            String markerId = marker.getId();
            if (markerDataMap.containsKey(markerId)) {
                Map<String, Object> data = markerDataMap.get(markerId);
                if (data != null) updateBottomSheetDetails(data);
            }
            return false;
        });
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getNeedsLocationsFromFirestore() {
        db.collection("needs_map")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "فشل جلب نقاط الاحتياج: " + error.getMessage());
                        return;
                    }
                    if (value != null && mMap != null) {
                        mMap.clear();
                        markerDataMap.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Double lat = doc.getDouble("latitude");
                            Double lng = doc.getDouble("longitude");
                            String title = doc.getString("title");
                            if (lat != null && lng != null) {
                                LatLng position = new LatLng(lat, lng);
                                com.google.android.gms.maps.model.Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(position)
                                        .title(title != null ? title : "منطقة بحاجة للمياه"));
                                if (marker != null) markerDataMap.put(marker.getId(), doc.getData());
                            }
                        }
                    }
                });
    }

    private void updateBottomSheetDetails(Map<String, Object> data) {
        String title = (String) data.get("title");
        String deficit = (String) data.get("deficitPercentage");
        String volume = (String) data.get("waterVolumeNeeded");

        if (tvLocationName != null && title != null) tvLocationName.setText(title);
        if (tvDeficitPercentage != null && deficit != null) tvDeficitPercentage.setText("نسبة العجز: " + deficit);
        if (tvWaterVolumeNeeded != null && volume != null) tvWaterVolumeNeeded.setText("الكمية المطلوبة: " + volume + " لتر");

        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setupClickListeners() {
        if (btnZoomIn != null) btnZoomIn.setOnClickListener(v -> { if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomIn()); });
        if (btnZoomOut != null) btnZoomOut.setOnClickListener(v -> { if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomOut()); });
        if (btnMyLocation != null) btnMyLocation.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f));
        });

        if (btnCoordinateInitiative != null) btnCoordinateInitiative.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateInitiativeActivity.class));
        });

        if (navDashboard != null) navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, InitiatorDashboardActivity.class));
            finish();
        });
        
        if (navInitiatives != null) navInitiatives.setOnClickListener(v -> {
            startActivity(new Intent(this, InitiativesListActivity.class));
            finish();
        });

        if (navWallet != null) navWallet.setOnClickListener(v -> {
            startActivity(new Intent(this, WalletActivity.class));
            finish();
        });
    }
}
