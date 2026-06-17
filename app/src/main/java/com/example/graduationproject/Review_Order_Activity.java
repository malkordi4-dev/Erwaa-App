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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // إعدادات الـ User Agent لتجنب حظر خريطة OpenStreetMap
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_review_order);

        // استقبال البيانات القادمة من الواجهة السابقة
        Intent data = getIntent();
        quantity = data.getIntExtra("quantity", 500);
        unit = data.getStringExtra("unit");
        address = data.getStringExtra("address");
        notes = data.getStringExtra("notes");
        scheduledTime = data.getStringExtra("scheduledTime");
        double lat = data.getDoubleExtra("lat", 31.5);
        double lng = data.getDoubleExtra("lng", 34.4);
        deliveryLoc = new GeoPoint(lat, lng);

        // حساب الأسعار تلقائياً بناءً على الكمية
        double waterPrice = quantity * 0.1;
        double deliveryFee = 10.0;
        totalPrice = waterPrice + deliveryFee;

        // ربط عناصر الواجهة وعرض تفاصيل الفاتورة والعنوان
        TextView tvLocationMain = findViewById(R.id.tvLocationMain);
        TextView tvWaterPrice = findViewById(R.id.tvWaterPrice);
        TextView tvDeliveryPrice = findViewById(R.id.tvDeliveryPrice);
        TextView tvTotalPriceMain = findViewById(R.id.tvTotalPriceMain);
        TextView tvFooterPriceText = findViewById(R.id.tvFooterPriceText);

        tvLocationMain.setText(address);
        tvWaterPrice.setText(String.format("%.2f ILS", waterPrice));
        tvDeliveryPrice.setText(String.format("%.2f ILS", deliveryFee));
        tvTotalPriceMain.setText(String.format("%.2f ILS", totalPrice));
        tvFooterPriceText.setText(String.format("%.2f ILS", totalPrice));

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnEditLocation = findViewById(R.id.btnEditLocation);
        Button btnConfirmAndSend = findViewById(R.id.btnConfirmAndSend);

        btnBack.setOnClickListener(v -> finish());
        btnEditLocation.setOnClickListener(v -> finish());
        btnConfirmAndSend.setOnClickListener(v -> saveOrderToSupabase());

        // إعدادات عرض الخريطة لتثبيت موقع التوصيل
        mapView = findViewById(R.id.mapViewReview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(deliveryLoc);
        mapView.setMultiTouchControls(false); // خريطة ثابتة للعرض فقط

        // إضافة الدبوس (Marker) على موقع التوصيل المحدد
        Marker marker = new Marker(mapView);
        marker.setPosition(deliveryLoc);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("موقع التوصيل");
        mapView.getOverlays().add(marker);
    }

    private void saveOrderToSupabase() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        String accessToken = prefs.getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        // بناء وتعبئة كائن الطلب (OrderModel) لتخزينه في Supabase
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

        // تعديل لحل المشكلة: تم تمرير قيم فارغة (null) مؤقتاً للمزود والخدمة لتجنب
        // أخطاء القيود في السيرفر (Foreign Keys) إذا كانت جداولهم فارغة.
        order.setProviderId(null);
        order.setServiceId(null);

        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);

        // إرسال الطلب (كائن الـ order فقط بناءً على الحل الثاني المعتمد للـ API)
        api.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Review_Order_Activity.this, "تم إرسال طلبك بنجاح!", Toast.LENGTH_LONG).show();

                    // التوجه لواجهة تتبع حالة الطلب وتصفير مكدس الواجهات لعدم الرجوع للخلف بالخطأ
                    Intent intent = new Intent(Review_Order_Activity.this, Order_Status_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // طباعة نص الخطأ التفصيلي في الـ Logcat لمساعدتك في التتبع
                    Log.e("Supabase_Order", "فشل في السيرفر - كود الخطأ: " + response.code() + " | التفاصيل: " + errorBody);
                    Toast.makeText(Review_Order_Activity.this, "فشل في إرسال الطلب (خطأ " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Supabase_Order", "خطأ في الشبكة/الاتصال", t);
                Toast.makeText(Review_Order_Activity.this, "خطأ في الاتصال بالسيرفر", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
            mapView.onDetach(); // لمنع تسريب الذاكرة وتحسين استقرار التطبيق
        }
    }
}
