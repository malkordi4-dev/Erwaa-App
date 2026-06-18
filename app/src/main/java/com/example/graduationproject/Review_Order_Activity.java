package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Review_Order_Activity extends AppCompatActivity {

    private MapView mapView;
    private GeoPoint deliveryLoc;

    private int quantity;
    private String unit, address, notes, scheduledTime;
    private double totalPrice;
    private String orderType = "single"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_review_order);

        // استقبال البيانات من الواجهات السابقة (فردي، جماعي، اشتراك)
        Intent data = getIntent();
        quantity = data.getIntExtra("quantity", 500);
        unit = data.getStringExtra("unit");
        address = data.getStringExtra("address");
        notes = data.getStringExtra("notes");
        scheduledTime = data.getStringExtra("scheduledTime");
        
        // تحديد نوع الخدمة برمجياً لتخزينها في قاعدة البيانات
        if (unit != null && unit.contains("اشتراك")) {
            orderType = "monthly";
        } else if (unit != null && unit.contains("جماعي")) {
            orderType = "group";
        } else {
            orderType = "single";
        }

        double lat = data.getDoubleExtra("lat", 31.5);
        double lng = data.getDoubleExtra("lng", 34.4);
        deliveryLoc = new GeoPoint(lat, lng);

        double passedPrice = data.getDoubleExtra("totalPrice", 0.0);
        totalPrice = (passedPrice > 0) ? passedPrice : (quantity * 0.1) + 10.0;

        setupUI();
    }

    private void setupUI() {
        TextView tvServiceName = findViewById(R.id.tvServiceName);
        TextView tvLocationMain = findViewById(R.id.tvLocationMain);
        TextView tvOrderNotes = findViewById(R.id.tvOrderNotes);
        TextView tvWaterPrice = findViewById(R.id.tvWaterPrice);
        TextView tvTotalPriceMain = findViewById(R.id.tvTotalPriceMain);
        TextView tvFooterPriceText = findViewById(R.id.tvFooterPriceText);

        tvServiceName.setText(unit != null ? unit : "طلب تزويد مياه");
        tvLocationMain.setText(address != null ? address : "موقع محدد");
        tvOrderNotes.setText(notes != null && !notes.isEmpty() ? notes : "لا يوجد ملاحظات");
        
        tvWaterPrice.setText(String.format("%.2f ILS", totalPrice - 10.0));
        tvTotalPriceMain.setText(String.format("%.2f ILS", totalPrice));
        tvFooterPriceText.setText(String.format("%.2f ILS", totalPrice));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirmAndSend).setOnClickListener(v -> saveToSupabase());

        mapView = findViewById(R.id.mapViewReview);
        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(deliveryLoc);
            Marker marker = new Marker(mapView);
            marker.setPosition(deliveryLoc);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
        }
    }

    private void saveToSupabase() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        // بناء كائن الطلب الموحد
        OrderModel order = new OrderModel();
        order.setCustomerId(userId);
        order.setQuantity(quantity);
        order.setUnit(unit);
        order.setAddressDetails(address);
        order.setNotes(notes);
        order.setScheduledTime(scheduledTime);
        order.setDeliveryLat(deliveryLoc.getLatitude());
        order.setDeliveryLng(deliveryLoc.getLongitude());
        order.setTotalPrice(totalPrice);
        order.setStatus("pending");
        order.setOrderType(orderType); // تخزين نوع الخدمة

        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);
        api.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String message = "تم إرسال " + getOrderTypeArabic() + " بنجاح!";
                    Toast.makeText(Review_Order_Activity.this, message, Toast.LENGTH_LONG).show();
                    
                    // الانتقال لصفحة النجاح/الحالة
                    Intent intent = new Intent(Review_Order_Activity.this, Order_Status_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Log.e("Supabase", "Error Code: " + response.code());
                    Toast.makeText(Review_Order_Activity.this, "فشل في حفظ البيانات في سوبابيز", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Review_Order_Activity.this, "خطأ في الشبكة", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOrderTypeArabic() {
        if ("monthly".equals(orderType)) return "طلب الاشتراك";
        if ("group".equals(orderType)) return "الطلب الجماعي";
        return "الطلب";
    }

    @Override
    protected void onResume() { super.onResume(); if(mapView!=null) mapView.onResume(); }
    @Override
    protected void onPause() { super.onPause(); if(mapView!=null) mapView.onPause(); }
}
