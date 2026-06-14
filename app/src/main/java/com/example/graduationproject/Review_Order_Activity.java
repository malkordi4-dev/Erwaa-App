package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Review_Order_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng deliveryLoc;
    
    private int quantity;
    private String unit, address, notes, scheduledTime;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);

        // 1. استقبال البيانات من Intent
        Intent data = getIntent();
        quantity = data.getIntExtra("quantity", 500);
        unit = data.getStringExtra("unit");
        address = data.getStringExtra("address");
        notes = data.getStringExtra("notes");
        scheduledTime = data.getStringExtra("scheduledTime");
        double lat = data.getDoubleExtra("lat", 31.5);
        double lng = data.getDoubleExtra("lng", 34.4);
        deliveryLoc = new LatLng(lat, lng);

        // 2. حساب السعر التقديري (مثال: 0.1 لكل لتر + 10 رسوم توصيل)
        double waterPrice = quantity * 0.1;
        double deliveryFee = 10.0;
        totalPrice = waterPrice + deliveryFee;

        // 3. ربط عناصر الواجهة وتحديثها
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

        // 4. إعداد الأزرار
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnEditLocation = findViewById(R.id.btnEditLocation);
        Button btnConfirmAndSend = findViewById(R.id.btnConfirmAndSend);

        btnBack.setOnClickListener(v -> finish());
        btnEditLocation.setOnClickListener(v -> finish());
        btnConfirmAndSend.setOnClickListener(v -> saveOrderToSupabase());

        // 5. إعداد الخريطة
        mapView = findViewById(R.id.mapViewReview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void saveOrderToSupabase() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // إنشاء كائن الطلب
        OrderModel order = new OrderModel();
        order.setCustomerId(userId);
        order.setQuantity(quantity);
        order.setUnit(unit);
        order.setAddressDetails(address);
        order.setNotes(notes);
        order.setScheduledTime(scheduledTime);
        order.setDeliveryLat(deliveryLoc.latitude);
        order.setDeliveryLng(deliveryLoc.longitude);
        order.setTotalPrice(totalPrice);
        order.setStatus("pending");

        // إرسال الطلب عبر API
        SupabaseApi api = SupbaseClient.getClient().create(SupabaseApi.class);
        api.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Review_Order_Activity.this, "تم إرسال طلبك بنجاح!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Review_Order_Activity.this, Order_Status_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Log.e("Supabase_Order", "Error: " + response.code());
                    Toast.makeText(Review_Order_Activity.this, "فشل في إرسال الطلب، حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Supabase_Order", "Failure", t);
                Toast.makeText(Review_Order_Activity.this, "خطأ في الاتصال بالسيرفر", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.addMarker(new MarkerOptions().position(deliveryLoc).title("موقع التوصيل"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliveryLoc, 15));
        googleMap.getUiSettings().setAllGesturesEnabled(false); // تثبيت الخريطة للمعاينة فقط
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override protected void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState); }
}
