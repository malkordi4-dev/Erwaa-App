package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rate_Service_Activity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText etNotes;
    private TextView tvStationName, tvOrderNumber, tvOrderDate, tvQuantity, tvPrice;
    private String orderUuid;
    private Integer providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_service);

        // Initialize views
        ImageView btnBack = findViewById(R.id.btnBack);
        ratingBar = findViewById(R.id.ratingBar);
        etNotes = findViewById(R.id.etNotes);
        tvStationName = findViewById(R.id.tvStationName);
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvPrice = findViewById(R.id.tvPrice);

        LinearLayout btnSubmitRating = findViewById(R.id.btnSubmitRating);
        LinearLayout btnSkip = findViewById(R.id.btnSkip);

        // Get data from intent
        if (getIntent() != null) {
            orderUuid = getIntent().getStringExtra("order_uuid");
            providerId = getIntent().getIntExtra("provider_id", -1);
            if (providerId == -1) providerId = null;

            String stationName = getIntent().getStringExtra("station_name");
            String orderNum = getIntent().getStringExtra("order_number");
            String orderDate = getIntent().getStringExtra("order_date");
            String quantity = getIntent().getStringExtra("quantity");
            String price = getIntent().getStringExtra("price");

            if (stationName != null) tvStationName.setText(stationName);
            if (orderNum != null) tvOrderNumber.setText(orderNum);
            if (orderDate != null) tvOrderDate.setText(orderDate);
            if (quantity != null) tvQuantity.setText(quantity);
            if (price != null) tvPrice.setText(price);
        }

        btnBack.setOnClickListener(v -> finish());
        
        // Skip button: Navigate back to MapExplorerActivity
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(Rate_Service_Activity.this, MapExplorerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnSubmitRating.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(this, "يرجى اختيار التقييم أولاً", Toast.LENGTH_SHORT).show();
                return;
            }
            performSubmitRating((int) rating);
        });
    }

    private void performSubmitRating(int ratingValue) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String customerId = prefs.getString("user_id", null);
        String accessToken = prefs.getString("access_token", null);

        if (customerId == null || accessToken == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderUuid == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرّف الطلب الصحيح", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = etNotes.getText().toString().trim();
        RatingModel rating = new RatingModel(orderUuid, customerId, providerId, ratingValue, comment);

        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);
/*
        // Note: authHeader is handled by the Interceptor in SupbaseClient
        api.submitRating(rating).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Rate_Service_Activity.this, "تم إرسال تقييمك بنجاح!", Toast.LENGTH_LONG).show();
                    // Also return to map after successful rating
                    Intent intent = new Intent(Rate_Service_Activity.this, MapExplorerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorBody = "";
                    try { if (response.errorBody() != null) errorBody = response.errorBody().string(); } catch (IOException e) { e.printStackTrace(); }
                    Log.e("Supabase_Rating", "Error: " + response.code() + " Body: " + errorBody);
                    Toast.makeText(Rate_Service_Activity.this, "فشل إرسال التقييم: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Supabase_Rating", "Failure", t);
                Toast.makeText(Rate_Service_Activity.this, "خطأ في الاتصال بالشبكة", Toast.LENGTH_SHORT).show();
            }
        });
*/
    }
}
