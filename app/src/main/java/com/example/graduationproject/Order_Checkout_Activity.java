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
    private GeoPoint selectedLocation = new GeoPoint(31.5, 34.4);

    private TextView tvQuantityCount, tvUnit;
    private CardView btnByLiter, btnByTank;
    private EditText etAddressDetails, etNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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

        btnBack.setOnClickListener(v -> finish());

        btnByLiter.setOnClickListener(v -> selectQuantityType(true));
        btnByTank.setOnClickListener(v -> selectQuantityType(false));

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

        // Osmdroid Map initialization
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(selectedLocation);

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(selectedLocation);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("موقع التوصيل");
        startMarker.setDraggable(true);
        startMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {}
            @Override
            public void onMarkerDragEnd(Marker marker) {
                selectedLocation = (GeoPoint) marker.getPosition();
            }
            @Override
            public void onMarkerDragStart(Marker marker) {}
        });
        mapView.getOverlays().add(startMarker);

        btnLocateMe.setOnClickListener(v -> {
            Toast.makeText(this, "جاري تحديد الموقع...", Toast.LENGTH_SHORT).show();
            mapView.getController().animateTo(selectedLocation);
        });

        // Submit Order
        btnSubmitOrder.setOnClickListener(v -> {
            String address = etAddressDetails.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال تفاصيل الموقع", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(Order_Checkout_Activity.this, Review_Order_Activity.class);
            intent.putExtra("quantity", quantity);
            intent.putExtra("unit", unit);
            intent.putExtra("address", address);
            intent.putExtra("notes", etNotes.getText().toString().trim());
            intent.putExtra("scheduledTime", selectedTime);
            intent.putExtra("lat", selectedLocation.getLatitude());
            intent.putExtra("lng", selectedLocation.getLongitude());
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
            
            updateTextColor(btnByLiter, "#0C4A6E");
            updateTextColor(btnByTank, "#64748B");
        } else {
            unit = "خزان";
            quantity = 1;
            btnByLiter.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            btnByTank.setCardBackgroundColor(Color.parseColor("#BAE6FD"));

            updateTextColor(btnByLiter, "#64748B");
            updateTextColor(btnByTank, "#0C4A6E");
        }
        tvUnit.setText(unit);
        updateQuantityText();
    }

    private void updateTextColor(CardView card, String colorHex) {
        View layout = card.getChildAt(0);
        if (layout instanceof LinearLayout) {
            View text = ((LinearLayout) layout).getChildAt(0);
            if (text instanceof TextView) ((TextView) text).setTextColor(Color.parseColor(colorHex));
        } else if (layout instanceof TextView) {
            ((TextView) layout).setTextColor(Color.parseColor(colorHex));
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
