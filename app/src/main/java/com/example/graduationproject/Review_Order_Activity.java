package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;

public class Review_Order_Activity extends AppCompatActivity {

    private MapView mapView;
    private GeoPoint deliveryLoc;

    private int quantity;
    private String unit, address, notes, scheduledTime;
    private String providerId, providerName, serviceId;
    private double totalPrice;
    private String orderType = "single"; 
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_review_order);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // استقبال البيانات من الواجهة السابقة
        Intent data = getIntent();
        providerId = data.getStringExtra("provider_id");
        providerName = data.getStringExtra("provider_name");
        serviceId = data.getStringExtra("service_id");
        
        quantity = data.getIntExtra("quantity", 500);
        unit = data.getStringExtra("unit");
        address = data.getStringExtra("address");
        notes = data.getStringExtra("notes");
        scheduledTime = data.getStringExtra("scheduledTime");
        
        if (unit != null && unit.contains("اشتراك")) {
            orderType = "monthly";
        } else if (unit != null && unit.contains("جماعي")) {
            orderType = "group";
        } else {
            orderType = "single";
        }

        double lat = data.getDoubleExtra("lat", 31.516);
        double lng = data.getDoubleExtra("lng", 34.448);
        deliveryLoc = new GeoPoint(lat, lng);

        totalPrice = (unit != null && unit.equals("لتر")) ? (quantity * 0.05) + 15.0 : (quantity * 30.0) + 10.0;

        setupUI();
    }

    private void setupUI() {
        TextView tvServiceName = findViewById(R.id.tvServiceName);
        TextView tvLocationMain = findViewById(R.id.tvLocationMain);
        TextView tvOrderNotes = findViewById(R.id.tvOrderNotes);
        TextView tvWaterPrice = findViewById(R.id.tvWaterPrice);
        TextView tvTotalPriceMain = findViewById(R.id.tvTotalPriceMain);
        TextView tvFooterPriceText = findViewById(R.id.tvFooterPriceText);
        
        String displayTitle = (providerName != null ? providerName : "طلب تزويد مياه");
        tvServiceName.setText(displayTitle);
        
        tvLocationMain.setText(address != null ? address : "موقع محدد على الخريطة");
        tvOrderNotes.setText(notes != null && !notes.isEmpty() ? notes : "لا توجد ملاحظات إضافية");
        
        tvWaterPrice.setText(String.format("%.2f ILS", totalPrice - 10.0));
        tvTotalPriceMain.setText(String.format("%.2f ILS", totalPrice));
        tvFooterPriceText.setText(String.format("%.2f ILS", totalPrice));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirmAndSend).setOnClickListener(v -> saveToFirebase());

        mapView = findViewById(R.id.mapViewReview);
        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.getController().setZoom(16.0);
            mapView.getController().setCenter(deliveryLoc);
            Marker marker = new Marker(mapView);
            marker.setPosition(deliveryLoc);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle("موقع التوصيل");
            mapView.getOverlays().add(marker);
        }
    }

    private void saveToFirebase() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "يجب تسجيل الدخول لإتمام الطلب", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerId = mAuth.getCurrentUser().getUid();

        Map<String, Object> order = new HashMap<>();
        order.put("customer_id", customerId);
        order.put("provider_id", providerId);
        order.put("service_id", serviceId);
        order.put("provider_name", providerName);
        order.put("quantity", quantity);
        order.put("unit", unit);
        order.put("address_details", address);
        order.put("notes", notes);
        order.put("scheduled_time", scheduledTime);
        order.put("delivery_lat", deliveryLoc.getLatitude());
        order.put("delivery_lng", deliveryLoc.getLongitude());
        order.put("total_price", totalPrice);
        order.put("status", "pending");
        order.put("order_type", orderType);
        order.put("created_at", com.google.firebase.Timestamp.now());

        db.collection("orders").add(order)
                .addOnSuccessListener(documentReference -> {
                    String orderDocId = documentReference.getId();
                    Toast.makeText(Review_Order_Activity.this, "تم إرسال طلبك بنجاح!", Toast.LENGTH_LONG).show();
                    
                    // محاكاة قبول الطلب تلقائياً بعد ثانيتين
                    simulateProviderAcceptance(orderDocId);

                    Intent intent = new Intent(Review_Order_Activity.this, Order_Status_Activity.class);
                    intent.putExtra("order_id", orderDocId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error saving order", e);
                    Toast.makeText(Review_Order_Activity.this, "حدث خطأ أثناء إرسال الطلب", Toast.LENGTH_SHORT).show();
                });
    }

    private void simulateProviderAcceptance(String orderDocId) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            db.collection("orders").document(orderDocId)
                    .update("status", "accepted")
                    .addOnSuccessListener(aVoid -> Log.d("Simulation", "Order accepted automatically"))
                    .addOnFailureListener(e -> Log.e("Simulation", "Failed to accept order", e));
        }, 2000); // تأخير لمدة ثانيتين
    }

    @Override protected void onResume() { super.onResume(); if(mapView!=null) mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); if(mapView!=null) mapView.onPause(); }
}
