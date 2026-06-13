package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Order_Checkout_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private int quantity = 500;
    private String unit = "لتر";
    private String selectedTime = "الآن";

    private TextView tvQuantityCount, tvUnit;
    private CardView btnByLiter, btnByTank;
    private CardView btnNow, btnTodayLater, btnTomorrow;
    private EditText etAddressDetails, etNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_checkout);

        // Initialize Views
        ImageView btnBack = findViewById(R.id.btnBack);
        btnByLiter = findViewById(R.id.btnByLiter);
        btnByTank = findViewById(R.id.btnByTank);
        CardView btnMinus = findViewById(R.id.btnMinus);
        CardView btnPlus = findViewById(R.id.btnPlus);
        tvQuantityCount = findViewById(R.id.tvQuantityCount);
        tvUnit = findViewById(R.id.tvUnit);
        btnNow = findViewById(R.id.btnNow);
        btnTodayLater = findViewById(R.id.btnTodayLater);
        btnTomorrow = findViewById(R.id.btnTomorrow);
        mapView = findViewById(R.id.mapView);
        CardView btnLocateMe = findViewById(R.id.btnLocateMe);
        etAddressDetails = findViewById(R.id.etAddressDetails);
        etNotes = findViewById(R.id.etNotes);
        CardView btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Quantity Type Selection
        btnByLiter.setOnClickListener(v -> selectQuantityType(true));
        btnByTank.setOnClickListener(v -> selectQuantityType(false));

        // Quantity Adjustment
        btnMinus.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity -= (unit.equals("لتر") ? 50 : 1);
                if (quantity < 0) quantity = 0;
                updateQuantityText();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity += (unit.equals("لتر") ? 50 : 1);
            updateQuantityText();
        });

        // Delivery Time Selection
        btnNow.setOnClickListener(v -> selectDeliveryTime("الآن", btnNow));
        btnTodayLater.setOnClickListener(v -> selectDeliveryTime("اليوم", btnTodayLater));
        btnTomorrow.setOnClickListener(v -> selectDeliveryTime("غداً", btnTomorrow));

        // Map initialization
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnLocateMe.setOnClickListener(v -> {
            // In a real app, use FusedLocationProviderClient to get current location
            Toast.makeText(this, "جاري تحديد الموقع...", Toast.LENGTH_SHORT).show();
            if (googleMap != null) {
                LatLng dummyLocation = new LatLng(31.5, 34.4); // Example: Gaza
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dummyLocation, 15));
            }
        });

        // Submit Order
        btnSubmitOrder.setOnClickListener(v -> {
            String address = etAddressDetails.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال تفاصيل الموقع", Toast.LENGTH_SHORT).show();
                return;
            }
            // Logic for submitting order -> Move to Review
            Intent intent = new Intent(Order_Checkout_Activity.this, Review_Order_Activity.class);
            startActivity(intent);
        });
    }

    private void updateQuantityText() {
        tvQuantityCount.setText(String.valueOf(quantity));
    }

    private void selectQuantityType(boolean isLiter) {
        if (isLiter) {
            unit = "لتر";
            quantity = 500;
            btnByLiter.setCardBackgroundColor(Color.parseColor("#BAE6FD"));
            btnByTank.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            ((TextView)((android.widget.LinearLayout)btnByLiter.getChildAt(0)).getChildAt(0)).setTextColor(Color.parseColor("#0C4A6E"));
            ((TextView)((android.widget.LinearLayout)btnByTank.getChildAt(0)).getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
        } else {
            unit = "خزان";
            quantity = 1;
            btnByLiter.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            btnByTank.setCardBackgroundColor(Color.parseColor("#BAE6FD"));
            ((TextView)((android.widget.LinearLayout)btnByLiter.getChildAt(0)).getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
            ((TextView)((android.widget.LinearLayout)btnByTank.getChildAt(0)).getChildAt(0)).setTextColor(Color.parseColor("#0C4A6E"));
        }
        tvUnit.setText(unit);
        updateQuantityText();
    }

    private void selectDeliveryTime(String time, CardView selectedBtn) {
        selectedTime = time;
        // Reset all
        btnNow.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
        btnTodayLater.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
        btnTomorrow.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
        
        ((TextView)btnNow.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
        ((TextView)btnTodayLater.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
        ((TextView)btnTomorrow.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));

        // Highlight selected
        selectedBtn.setCardBackgroundColor(Color.parseColor("#BAE6FD"));
        ((TextView)selectedBtn.getChildAt(0)).setTextColor(Color.parseColor("#0C4A6E"));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng defaultLoc = new LatLng(31.5, 34.4); // Example coordinates
        googleMap.addMarker(new MarkerOptions().position(defaultLoc).title("موقع التوصيل"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 13));
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
