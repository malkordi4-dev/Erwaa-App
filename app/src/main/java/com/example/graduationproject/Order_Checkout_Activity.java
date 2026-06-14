package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Order_Checkout_Activity extends AppCompatActivity {

    private MapView mapView;
    private int quantity = 500;
    private String unit = "لتر";
    private String selectedTime = "الآن";

    private TextView tvQuantityCount, tvUnit;
    private CardView btnByLiter, btnByTank;
    // Commented out since they are missing from the XML layout
    // private CardView btnNow, btnTodayLater, btnTomorrow;
    private EditText etAddressDetails, etNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // OSMDroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        
        setContentView(R.layout.activity_order_checkout);

        // Initialize Views
        ImageView btnBack = findViewById(R.id.btnBack);
        btnByLiter = findViewById(R.id.btnByLiter);
        btnByTank = findViewById(R.id.btnByTank);
        CardView btnMinus = findViewById(R.id.btnMinus);
        CardView btnPlus = findViewById(R.id.btnPlus);
        tvQuantityCount = findViewById(R.id.tvQuantityCount);
        tvUnit = findViewById(R.id.tvUnit);

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
                int step = (unit.equals("لتر") ? 50 : 1);
                quantity -= step;
                if (quantity < 0) quantity = 0;
                updateQuantityText();
            }
        });

        btnPlus.setOnClickListener(v -> {
            int step = (unit.equals("لتر") ? 50 : 1);
            quantity += step;
            updateQuantityText();
        });

        // Delivery Time Selection (Commented out because buttons are missing in activity_order_checkout.xml)
        /*
        btnNow = findViewById(R.id.btnNow);
        btnTodayLater = findViewById(R.id.btnTodayLater);
        btnTomorrow = findViewById(R.id.btnTomorrow);
        
        if (btnNow != null) btnNow.setOnClickListener(v -> selectDeliveryTime("الآن", btnNow));
        if (btnTodayLater != null) btnTodayLater.setOnClickListener(v -> selectDeliveryTime("اليوم", btnTodayLater));
        if (btnTomorrow != null) btnTomorrow.setOnClickListener(v -> selectDeliveryTime("غداً", btnTomorrow));
        */

        // Osmdroid Map initialization
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(31.5, 34.4); // Example: Gaza
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("موقع التوصيل");
        mapView.getOverlays().add(startMarker);

        btnLocateMe.setOnClickListener(v -> {
            Toast.makeText(this, "جاري تحديد الموقع...", Toast.LENGTH_SHORT).show();
            // In a real app, use FusedLocationProviderClient to get current location
            mapView.getController().animateTo(startPoint);
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
            
            View layoutLiter = btnByLiter.getChildAt(0);
            if (layoutLiter instanceof LinearLayout) {
                View textLiter = ((LinearLayout) layoutLiter).getChildAt(0);
                if (textLiter instanceof TextView) ((TextView) textLiter).setTextColor(Color.parseColor("#0C4A6E"));
            }

            View layoutTank = btnByTank.getChildAt(0);
            if (layoutTank instanceof LinearLayout) {
                View textTank = ((LinearLayout) layoutTank).getChildAt(0);
                if (textTank instanceof TextView) ((TextView) textTank).setTextColor(Color.parseColor("#64748B"));
            }
        } else {
            unit = "خزان";
            quantity = 1;
            btnByLiter.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            btnByTank.setCardBackgroundColor(Color.parseColor("#BAE6FD"));

            View layoutLiter = btnByLiter.getChildAt(0);
            if (layoutLiter instanceof LinearLayout) {
                View textLiter = ((LinearLayout) layoutLiter).getChildAt(0);
                if (textLiter instanceof TextView) ((TextView) textLiter).setTextColor(Color.parseColor("#64748B"));
            }

            View layoutTank = btnByTank.getChildAt(0);
            if (layoutTank instanceof LinearLayout) {
                View textTank = ((LinearLayout) layoutTank).getChildAt(0);
                if (textTank instanceof TextView) ((TextView) textTank).setTextColor(Color.parseColor("#0C4A6E"));
            }
        }
        tvUnit.setText(unit);
        updateQuantityText();
    }

    /*
    private void selectDeliveryTime(String time, CardView selectedBtn) {
        selectedTime = time;
        if (btnNow == null || btnTodayLater == null || btnTomorrow == null) return;
        
        // Reset all
        btnNow.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
        btnTodayLater.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
        btnTomorrow.setCardBackgroundColor(Color.parseColor("#F1F5F9"));
        
        if (btnNow.getChildAt(0) instanceof TextView) ((TextView)btnNow.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
        if (btnTodayLater.getChildAt(0) instanceof TextView) ((TextView)btnTodayLater.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
        if (btnTomorrow.getChildAt(0) instanceof TextView) ((TextView)btnTomorrow.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));

        // Highlight selected
        selectedBtn.setCardBackgroundColor(Color.parseColor("#BAE6FD"));
        if (selectedBtn.getChildAt(0) instanceof TextView) ((TextView)selectedBtn.getChildAt(0)).setTextColor(Color.parseColor("#0C4A6E"));
    }
    */

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
