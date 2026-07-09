package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

public class ProviderOrderDetailsActivity extends AppCompatActivity {

    private TextView tvOrderNumber, tvOrderStatus, tvCustomerName, tvCustomerAddress, tvWaterType, tvWaterQty, tvPriceTotal;
    private MaterialButton btnConfirmArrival, btnCompleteTask;
    private View btnCallCustomer;
    private MapView map;
    private FirebaseFirestore db;
    private String orderId, customerPhone;
    private ListenerRegistration orderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // إعدادات Osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_provider_order_details);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("order_id");

        initViews();
        setupBottomNavigation();
        
        if (orderId != null) {
            fetchOrderDetails();
        } else {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الطلب", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        tvWaterType = findViewById(R.id.tvWaterType);
        tvWaterQty = findViewById(R.id.tvWaterQty);
        tvPriceTotal = findViewById(R.id.tvPriceTotal);
        btnConfirmArrival = findViewById(R.id.btnConfirmArrival);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        btnCallCustomer = findViewById(R.id.btnCallCustomer);
        map = findViewById(R.id.mapView);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        btnCallCustomer.setOnClickListener(v -> {
            if (customerPhone != null && !customerPhone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customerPhone));
                startActivity(intent);
            } else {
                Toast.makeText(this, "رقم هاتف الزبون غير متوفر حالياً", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmArrival.setOnClickListener(v -> updateStatus("on_way"));
        btnCompleteTask.setOnClickListener(v -> updateStatus("delivered"));

        setupMap();
    }

    private void setupMap() {
        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            map.getController().setZoom(16.0);
        }
    }

    private void fetchOrderDetails() {
        orderListener = db.collection("orders").document(orderId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Details", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                OrderModel order = snapshot.toObject(OrderModel.class);
                if (order == null) return;

                // تحديث النصوص
                tvOrderNumber.setText("#" + orderId.substring(0, Math.min(orderId.length(), 6)).toUpperCase());
                tvCustomerAddress.setText(order.getAddressDetails() != null ? "📍 " + order.getAddressDetails() : "العنوان غير محدد");
                tvWaterType.setText(order.getOrderType() != null ? order.getOrderType() : "تزويد مياه");
                tvWaterQty.setText(order.getQuantity() + " " + (order.getUnit() != null ? order.getUnit() : "لتر"));
                tvPriceTotal.setText(String.format(Locale.getDefault(), "%.2f ₪", order.getTotalPrice() != null ? order.getTotalPrice() : 0.0));

                updateStatusUI(order.getStatus());
                
                // تحديث موقع الزبون على الخريطة
                if (order.getDeliveryLat() != 0) {
                    GeoPoint point = new GeoPoint(order.getDeliveryLat(), order.getDeliveryLng());
                    map.getController().animateTo(point);
                    Marker marker = new Marker(map);
                    marker.setPosition(point);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle("موقع الزبون");
                    map.getOverlays().clear();
                    map.getOverlays().add(marker);
                    map.invalidate();
                }

                // جلب بيانات العميل (الاسم والهاتف)
                if (order.getCustomerId() != null) {
                    fetchCustomerInfo(order.getCustomerId());
                }
            }
        });
    }

    private void fetchCustomerInfo(String customerId) {
        db.collection("users").document(customerId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                tvCustomerName.setText(doc.getString("full_name"));
                customerPhone = doc.getString("phone");
            }
        });
    }

    private void updateStatusUI(String status) {
        if (status == null) return;
        switch (status) {
            case "accepted":
                tvOrderStatus.setText("● تم القبول");
                tvOrderStatus.setTextColor(Color.parseColor("#3B82F6"));
                btnConfirmArrival.setVisibility(View.VISIBLE);
                btnCompleteTask.setVisibility(View.GONE);
                break;
            case "on_way":
                tvOrderStatus.setText("● في الطريق");
                tvOrderStatus.setTextColor(Color.parseColor("#10B981"));
                btnConfirmArrival.setVisibility(View.GONE);
                btnCompleteTask.setVisibility(View.VISIBLE);
                break;
            case "delivered":
                tvOrderStatus.setText("● طلب مكتمل");
                tvOrderStatus.setTextColor(Color.parseColor("#15803D"));
                btnConfirmArrival.setVisibility(View.GONE);
                btnCompleteTask.setVisibility(View.GONE);
                break;
            case "cancelled":
                tvOrderStatus.setText("● ملغي");
                tvOrderStatus.setTextColor(Color.parseColor("#EF4444"));
                btnConfirmArrival.setVisibility(View.GONE);
                btnCompleteTask.setVisibility(View.GONE);
                break;
            default:
                tvOrderStatus.setText("● " + status);
                btnConfirmArrival.setVisibility(View.GONE);
                btnCompleteTask.setVisibility(View.GONE);
        }
    }

    private void updateStatus(String status) {
        db.collection("orders").document(orderId).update("status", status)
                .addOnSuccessListener(aVoid -> {
                    String msg = "on_way".equals(status) ? "تم بدء التوصيل بنجاح" : "تم إكمال الطلب، شكراً لك!";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    if ("delivered".equals(status)) {
                        finish(); // العودة لقائمة الطلبات عند الانتهاء
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "فشل تحديث الحالة", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navProviderDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, ProviderDashboardActivity.class));
            finish();
        });
        findViewById(R.id.navProviderOrders).setOnClickListener(v -> finish()); // العودة للخلف للقائمة
        findViewById(R.id.navProviderServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ProviderServicesActivity.class));
            finish();
        });
        findViewById(R.id.navProviderHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, TripsHistoryActivity.class));
            finish();
        });
    }

    @Override public void onResume() { super.onResume(); if (map != null) map.onResume(); }
    @Override public void onPause() { super.onPause(); if (map != null) map.onPause(); }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) orderListener.remove();
        if (map != null) map.onDetach();
    }
}
