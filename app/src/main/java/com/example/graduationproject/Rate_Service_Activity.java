package com.example.graduationproject;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rate_Service_Activity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etNotes;
    private TextView tvStationName, tvOrderNumber, tvOrderDate, tvQuantity, tvPrice;
    private String orderId;
    private int providerId;

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

        // Get data from intent (passed from Water_Delivered_Activity)
        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("order_id");
            providerId = getIntent().getIntExtra("provider_id", -1);
            
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
        btnSkip.setOnClickListener(v -> finish());

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
        // 1. Get current User ID
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String customerId = prefs.getString("user_id", null);

        if (customerId == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = etNotes.getText().toString().trim();

        // 2. Create Model
        RatingModel rating = new RatingModel(orderId, customerId, providerId, ratingValue, comment);

        // 3. Call API
        SupabaseApi api = SupbaseClient.getClient().create(SupabaseApi.class);
        api.submitRating(rating).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Rate_Service_Activity.this, "تم إرسال تقييمك بنجاح!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Rate_Service_Activity.this, "فشل إرسال التقييم", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Rate_Service_Activity.this, "خطأ في الاتصال بالسيرفر", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
